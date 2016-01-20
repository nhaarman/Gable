/*
 * Copyright 2016 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.triad

import android.app.Activity
import android.content.Intent
import android.view.ViewGroup
import com.nhaarman.triad_core.R

/**
 * This class represents a delegate which can be used to use Triad in any
 * [Activity].
 *
 *
 * When using the `TriadDelegate`, you must proxy the following Activity
 * lifecycle methods to it:
 *
 *  * [.onCreate]
 *  * [.onBackPressed]
 *  * [.onActivityResult]
 *

 * @param  The `ApplicationComponent` to use for `Presenter` creation.
 */
class TriadDelegate<ApplicationComponent : Any> private constructor(
      /**
       * The [Activity] instance this `TriadDelegate` is bound to.
       */
      private val activity: Activity) {

    private lateinit var applicationComponent: ApplicationComponent

    /**
     * The [Triad] instance that is used to navigate between [Screen]s.
     */
    private var _triad: Triad? = null
    val triad: Triad
        get() = _triad ?: throw IllegalStateException("Triad is null. Make sure to call TriadDelegate.onCreate().")

    private var _currentScreen: Screen<ApplicationComponent>? = null
    val currentScreen: Screen<ApplicationComponent>
        get() = _currentScreen ?: throw IllegalStateException("Current screen is null.")

    private var currentView: ViewGroup? = null

    /**
     * An optional [OnScreenChangedListener] that is notified of screen changes.
     */
    var onScreenChangedListener: OnScreenChangedListener<ApplicationComponent>? = null

    private lateinit var triadView: TriadView

    fun onCreate() {
        if (activity.application !is TriadProvider) throw IllegalStateException("Make sure your Application class implements TriadProvider.")
        if (activity.application !is ApplicationComponentProvider<*>) throw IllegalStateException("Make sure your Application class implements ApplicationComponentProvider.")

        applicationComponent = (activity.application as ApplicationComponentProvider<ApplicationComponent>).applicationComponent

        activity.setContentView(R.layout.view_triad)
        triadView = activity.findViewById(R.id.view_triad) as TriadView

        _triad = (activity.application as TriadProvider).triad.apply {
            setActivity(activity)
            listener = MyTriadListener()

            if (backstack.size() > 0) {
                showCurrent()
            }
        }
    }

    fun onBackPressed(): Boolean {
        return _currentScreen != null && _currentScreen!!.onBackPressed() || triad.goBack()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        triad.onActivityResult(requestCode, resultCode, data)
    }

    fun onDestroy() {
        val iterator = triad.backstack.reverseIterator()
        while (iterator.hasNext()) {
            val screen = iterator.next()
            screen.onDestroy()
        }
    }

    private inner class MyTriadListener : Triad.Listener<ApplicationComponent> {

        override fun screenPushed(pushedScreen: Screen<ApplicationComponent>) {
            pushedScreen.setApplicationComponent(applicationComponent)
            pushedScreen.onCreate()
        }

        override fun screenPopped(poppedScreen: Screen<ApplicationComponent>) {
            poppedScreen.onDestroy()
        }

        override fun forward(newScreen: Screen<ApplicationComponent>, animator: TransitionAnimator?, callback: Triad.Callback) {
            _currentScreen?.apply {
                currentView?.let {
                    saveState(it)
                }
            }

            _currentScreen = newScreen
            val oldView = currentView
            val newView = newScreen.createView(triadView)
            currentView = newView

            triadView.forward(oldView, newView, animator, callback)
            onScreenChangedListener?.onScreenChanged(newScreen)
        }

        override fun backward(newScreen: Screen<ApplicationComponent>, animator: TransitionAnimator?, callback: Triad.Callback) {
            _currentScreen = newScreen

            val oldView = currentView
            val newView = newScreen.createView(triadView)
            currentView = newView

            currentView?.let {
                newScreen.restoreState(it)
            }

            triadView.backward(oldView, newView, animator, callback)
            onScreenChangedListener?.onScreenChanged(newScreen)
        }

        override fun replace(newScreen: Screen<ApplicationComponent>, animator: TransitionAnimator?, callback: Triad.Callback) {
            _currentScreen = newScreen

            val oldView = currentView
            val newView = newScreen.createView(triadView)
            currentView = newView
            triadView.forward(oldView, newView, animator, callback)

            onScreenChangedListener?.onScreenChanged(newScreen)
        }
    }

    companion object {

        fun <T : Any> createFor(activity: Activity): TriadDelegate<T> {
            return TriadDelegate(activity)
        }
    }
}
