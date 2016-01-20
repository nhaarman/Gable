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
import android.util.SparseArray
import java.lang.ref.WeakReference

interface Triad {

    var backstack: Backstack
    var listener: Listener<*>?

    /**
     * Sets the current Activity, to be able to start other Activities from the Triad instance.
     */
    fun setActivity(activity: Activity?)

    /**
     * Initializes the backstack with given Screen. If the backstack is not empty, this call is ignored.
     * This method must be called before any other backstack operation.

     * @param screen The Screen to start with.
     */
    fun startWith(screen: Screen<*>)

    /**
     * Pushes given Screen onto the backstack.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The screen to push onto the backstack.
     */
    fun goTo(screen: Screen<*>) = goTo(screen, null)

    /**
     * Pushes given Screen onto the backstack.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The screen to push onto the backstack.
     */
    fun goTo(screen: Screen<*>, animator: TransitionAnimator?)

    /**
     * Forces a notification of the current screen.
     */
    fun showCurrent()

    /**
     * Pops the backstack until given Screen is found.
     * If the Screen is not found, the Screen is pushed onto the current backstack.
     * Does nothing if the Screen is already on top of the stack.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The Screen to pop to.
     */
    fun popTo(screen: Screen<*>) = popTo(screen, null)

    /**
     * Pops the backstack until given Screen is found.
     * If the Screen is not found, the Screen is pushed onto the current backstack.
     * Does nothing if the Screen is already on top of the stack.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The Screen to pop to.
     */
    fun popTo(screen: Screen<*>, animator: TransitionAnimator? = null)

    /**
     * Replaces the current Screen with given Screen.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The Screen to replace the current Screen.
     */
    fun replaceWith(screen: Screen<*>) = replaceWith(screen, null)

    /**
     * Replaces the current Screen with given Screen.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The Screen to replace the current Screen.
     */
    fun replaceWith(screen: Screen<*>, animator: TransitionAnimator? = null)

    /**
     * Pops the current screen off the backstack.
     * Does nothing if the backstack would be empty afterwards.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @return true if the transition will execute.
     */
    fun goBack(): Boolean

    /**
     * Replaces the entire backstack with given backstack, in a forward manner.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param newBackstack The new backstack.
     */
    fun forward(newBackstack: Backstack)

    /**
     * Replaces the entire backstack with given backstack, in a backward manner.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param newBackstack The new backstack.
     */
    fun backward(newBackstack: Backstack)

    /**
     * Replaces the entire backstack with given backstack, in a replace manner.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param newBackstack The new backstack.
     */
    fun replace(newBackstack: Backstack)

    /**
     * Launches the Activity described by given Intent.

     * @param intent The Activity to start.
     */
    fun startActivity(intent: Intent)

    /**
     * Launches the Activity described by given Intent for result.

     * @param intent   The Activity to start for result.
     * *
     * @param listener The callback to notify for the result.
     */
    fun startActivityForResult(intent: Intent, listener: ActivityResultListener)

    /**
     * Propagates the activity result to the proper [ActivityResultListener].

     * @param requestCode The request code, as created by [.startActivityForResult].
     * *
     * @param resultCode  The result code of the returning Activity.
     * *
     * @param data        The result data of the returning Activity.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    /**
     * Supplied by Triad to the Listener, which is responsible for calling onComplete().
     */
    interface Callback {

        /**
         * Must be called exactly once to indicate that the corresponding transition has completed.
         *
         *
         * If not called, the backstack will not be updated and further calls to Triad will not execute.
         * Calling more than once will result in an exception.
         */
        fun onComplete()
    }

    interface Listener<T : Any> {

        fun screenPushed(pushedScreen: Screen<T>)

        fun screenPopped(poppedScreen: Screen<T>)

        /**
         * Notifies the listener that the backstack will forward to a new Screen.

         * @param newScreen The new Screen to be shown.
         * *
         * @param callback  Must be called to indicate completion.
         */
        fun forward(newScreen: Screen<T>, animator: TransitionAnimator?, callback: Callback)

        /**
         * Notifies the listener that the backstack will be moved back to given Screen.

         * @param newScreen The new screen to be shown.
         * *
         * @param callback  Must be called to indicate completion.
         */
        fun backward(newScreen: Screen<T>, animator: TransitionAnimator?, callback: Callback)

        /**
         * Notifies the listener that the backstack will be replaced, with given Screen on top.

         * @param newScreen The new screen to be shown.
         * *
         * @param callback  Must be called to indicate completion.
         */
        fun replace(newScreen: Screen<T>, animator: TransitionAnimator?, callback: Callback)
    }

    interface ActivityResultListener {

        fun onActivityResult(resultCode: Int, data: Intent?)
    }


    companion object {

        fun emptyInstance(): Triad {
            return TriadImpl(Backstack.emptyBuilder().build())
        }

        fun newInstance(backstack: Backstack, listener: Listener<*>): Triad {
            return TriadImpl(backstack, listener)
        }
    }
}

/**
 * Holds the current truth, the history of screens, and exposes operations to change it.
 */
open class TriadImpl internal constructor(override var backstack: Backstack, override var listener: Triad.Listener<*>? = null) : Triad {

    private val activityResultListeners = SparseArray<Triad.ActivityResultListener>()

    private var transition: Transition? = null

    private var activity = WeakReference<Activity>(null)

    private var requestCodeCounter: Int = 0

    /**
     * Sets the current Activity, to be able to start other Activities from the Triad instance.
     */
    override fun setActivity(activity: Activity?) {
        this.activity = WeakReference<Activity>(activity)
    }

    /**
     * Initializes the backstack with given Screen. If the backstack is not empty, this call is ignored.
     * This method must be called before any other backstack operation.

     * @param screen The Screen to start with.
     */
    override fun startWith(screen: Screen<*>) {
        if (backstack.size() == 0 && transition == null) {
            move {
                val newBackstack = Backstack.single(screen)
                notifyScreenPushed(screen)
                notifyForward(newBackstack)
            }
        }
    }

    override fun goTo(screen: Screen<*>, animator: TransitionAnimator?) {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        move {
            val newBackstack = backstack.buildUpon().push(screen, animator).build()
            notifyScreenPushed(screen)
            notifyForward(newBackstack)
        }
    }

    /**
     * Forces a notification of the current screen.
     */
    override fun showCurrent() {
        move {
            notifyForward(backstack)
        }
    }

    override fun popTo(screen: Screen<*>, animator: TransitionAnimator?) {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        move {
            if (backstack.current<Any>().screen == screen) {
                onComplete()
            } else {
                val builder = backstack.buildUpon()
                val poppedScreens = Backstack.emptyBuilder()
                var count = 0
                // Take care to leave the original screen instance on the stack, if we find it.  This enables
                // some arguably bad behavior on the part of clients, but it's still probably the right thing
                // to do.
                run {
                    val it = backstack.reverseEntryIterator()
                    while (it.hasNext()) {
                        val s = it.next().screen

                        if (s == screen) {
                            // Clear up to the target screen.
                            for (i in 0..backstack.size() - count - 1) {
                                val entry = builder.pop()
                                if (entry != null) {
                                    poppedScreens.push(entry)
                                }
                            }
                            break
                        } else {
                            count++
                        }
                    }
                }

                val newBackstack: Backstack
                val poppedBackstack = poppedScreens.build()
                if (poppedBackstack.size() != 0) {
                    val it = poppedBackstack.reverseEntryIterator()
                    while (it.hasNext()) {
                        val entry = it.next()
                        notifyScreenPopped(entry.screen)
                    }

                    builder.push(poppedBackstack.current<Any>())
                    newBackstack = builder.build()
                    notifyBackward(newBackstack, animator)
                } else {
                    notifyScreenPushed(screen)

                    builder.push(screen, animator)
                    newBackstack = builder.build()
                    notifyForward(newBackstack)
                }
            }
        }
    }

    /**
     * Replaces the current Screen with given Screen.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param screen The Screen to replace the current Screen.
     */
    override fun replaceWith(screen: Screen<*>, animator: TransitionAnimator?) {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        move {
            val builder = backstack.buildUpon()
            val entry = builder.pop() ?: throw IllegalStateException("Popped entry is null")
            builder.push(screen, animator)
            val newBackstack = builder.build()

            notifyScreenPopped(entry.screen)
            notifyScreenPushed(screen)
            notifyReplace(newBackstack)
        }
    }

    /**
     * Pops the current screen off the backstack.
     * Does nothing if the backstack would be empty afterwards.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @return true if the transition will execute.
     */
    override fun goBack(): Boolean {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        val canGoBack = backstack.size() > 1 || transition?.isFinished == false
        move {
            if (backstack.size() == 1) {
                // We are not calling the listener, so we must complete this noop transition ourselves.
                onComplete()
            } else {
                val builder = backstack.buildUpon()
                val entry = builder.pop() ?: throw IllegalStateException("Popped entry is null")
                val newBackstack = builder.build()

                notifyScreenPopped(entry.screen)
                notifyBackward(newBackstack, entry.animator)
            }

        }
        return canGoBack
    }

    /**
     * Replaces the entire backstack with given backstack, in a forward manner.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param newBackstack The new backstack.
     */
    override fun forward(newBackstack: Backstack) {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        move {
            for (screen in backstack) {
                notifyScreenPopped(screen)
            }
            val it = newBackstack.reverseIterator()
            while (it.hasNext()) {
                notifyScreenPushed(it.next())
            }

            notifyForward(newBackstack)
        }
    }

    /**
     * Replaces the entire backstack with given backstack, in a backward manner.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param newBackstack The new backstack.
     */
    override fun backward(newBackstack: Backstack) {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        move {
            for (screen in backstack) {
                notifyScreenPopped(screen)
            }
            val it = newBackstack.reverseIterator()
            while (it.hasNext()) {
                notifyScreenPushed(it.next())
            }

            notifyBackward(newBackstack, null)
        }
    }

    /**
     * Replaces the entire backstack with given backstack, in a replace manner.

     * One must first initialize this instance with [.startWith] before this method is called.

     * @param newBackstack The new backstack.
     */
    override fun replace(newBackstack: Backstack) {
        if (backstack.size() == 0 && transition == null) throw IllegalStateException("Use startWith(Screen) to show your first Screen.")

        move {
            for (screen in backstack) {
                notifyScreenPopped(screen)
            }
            val it = newBackstack.reverseIterator()
            while (it.hasNext()) {
                notifyScreenPushed(it.next())
            }

            notifyReplace(newBackstack)
        }
    }

    private fun move(transition: Transition.() -> Unit) = move(Transition(transition))

    private fun move(transition: Transition) {
        val currentTransition = this.transition

        if (currentTransition != null && !currentTransition.isFinished) {
            currentTransition.enqueue(transition)
        } else {
            this.transition = transition
            transition.execute()
        }
    }

    /**
     * Launches the Activity described by given Intent.

     * @param intent The Activity to start.
     */
    override fun startActivity(intent: Intent) {
        activity.get()?.startActivity(intent) ?: throw IllegalStateException("Activity reference is null.")
    }

    /**
     * Launches the Activity described by given Intent for result.

     * @param intent   The Activity to start for result.
     * *
     * @param listener The callback to notify for the result.
     */
    override fun startActivityForResult(intent: Intent, listener: Triad.ActivityResultListener) {
        activity.get()?.startActivityForResult(intent, requestCodeCounter) ?: throw IllegalStateException("Activity reference is null.")
        activityResultListeners.put(requestCodeCounter, listener)
        requestCodeCounter++
    }

    /**
     * Propagates the activity result to the proper [ActivityResultListener].

     * @param requestCode The request code, as created by [.startActivityForResult].
     * *
     * @param resultCode  The result code of the returning Activity.
     * *
     * @param data        The result data of the returning Activity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityResultListeners.get(requestCode).onActivityResult(resultCode, data)
        activityResultListeners.remove(requestCode)
    }

    inner class Transition(private val action: Transition.() -> Unit) : Triad.Callback {

        var isFinished: Boolean = false
            private set

        private var next: Transition? = null

        private var nextBackstack: Backstack? = null

        internal fun enqueue(transition: Transition): Transition {
            next = next?.enqueue(transition) ?: transition
            return this
        }

        fun notifyScreenPopped(screen: Screen<*>) {
            listener?.screenPopped(screen) ?: throw IllegalStateException("Listener is null. Be sure to call setListener(Listener).")
        }

        fun notifyScreenPushed(screen: Screen<*>) {
            listener?.screenPushed(screen) ?: throw IllegalStateException("Listener is null. Be sure to call setListener(Listener).")
        }

        fun notifyForward(nextBackstack: Backstack) {
            this.nextBackstack = nextBackstack
            listener?.forward(nextBackstack.current<Any>().screen, nextBackstack.current<Any>().animator, this)
                  ?: throw IllegalStateException("Listener is null. Be sure to call setListener(Listener).")
        }

        fun notifyBackward(nextBackstack: Backstack, animator: TransitionAnimator?) {
            this.nextBackstack = nextBackstack
            listener?.backward(nextBackstack.current<Any>().screen, animator, this)
                  ?: throw IllegalStateException("Listener is null. Be sure to call setListener(Listener).")
        }

        fun notifyReplace(nextBackstack: Backstack) {
            this.nextBackstack = nextBackstack
            listener?.replace(nextBackstack.current<Any>().screen, nextBackstack.current<Any>().animator, this)
                  ?: throw IllegalStateException("Listener is null. Be sure to call setListener(Listener).")
        }

        internal fun execute() {
            action()
        }

        override fun onComplete() {
            if (isFinished) throw IllegalStateException("onComplete already called for this transition")

            nextBackstack?.let {
                backstack = it
            }

            isFinished = true

            next?.let {
                transition = it
                it.execute()
            }
        }
    }

}