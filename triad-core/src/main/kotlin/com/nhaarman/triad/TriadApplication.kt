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

import android.app.Application

/**
 * A base [Application] implementation that handles creation of the [Triad] and
 * `ApplicationComponent` instance.
 */
abstract class TriadApplication<ApplicationComponent> : Application(), TriadProvider, ApplicationComponentProvider<ApplicationComponent> {

    private var _triad: Triad? = null
    override val triad: Triad
        get() = _triad ?: throw IllegalStateException("Calling getTriad() before onCreate().")

    private var _applicationComponent: ApplicationComponent? = null
    override val applicationComponent: ApplicationComponent
        get() = _applicationComponent ?: throw IllegalStateException("Calling getApplicationComponent() before onCreate().")

    override fun onCreate() {
        super.onCreate()

        _triad = Triad.emptyInstance()
        _applicationComponent = createApplicationComponent()
    }

    /**
     * Creates a new instance of the `ApplicationComponent`.
     */
    protected abstract fun createApplicationComponent(): ApplicationComponent
}
