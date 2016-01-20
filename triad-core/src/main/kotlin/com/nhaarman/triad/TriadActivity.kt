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
import android.os.Bundle

/**
 * An [Activity] which is the root of an application that uses Triad.

 * @param  The `ApplicationComponent` to use for `BasePresenter` creation.
 * *
 * @param     The `ActivityComponent` to supply to `Presenters`.
 */
abstract class TriadActivity<ApplicationComponent : Any, ActivityComponent> : Activity(), ScreenProvider<ApplicationComponent>, ActivityComponentProvider<ActivityComponent> {

    private val delegate = TriadDelegate.createFor<ApplicationComponent>(this)

    override val currentScreen: Screen<ApplicationComponent>
        get() = delegate.currentScreen

    override val activityComponent: ActivityComponent by lazy {
        createActivityComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate()
    }

    /**
     * Creates the `ActivityComponent`.
     */
    protected abstract fun createActivityComponent(): ActivityComponent

    override fun onBackPressed() {
        if (!delegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        delegate.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        delegate.onDestroy()
        super.onDestroy()
    }

    /**
     * Returns the [Triad] instance to be used to navigate between [Screen]s.
     */
    open protected val triad: Triad
        get() = delegate.triad

    protected fun setOnScreenChangedListener(onScreenChangedListener: OnScreenChangedListener<ApplicationComponent>?) {
        delegate.onScreenChangedListener = onScreenChangedListener
    }
}