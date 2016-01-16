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

import android.support.annotation.MainThread

/**
 * The Presenter class in the MVP context.

 * In the Model-View-Presenter pattern, the `Presenter` retrieves data from the
 * `Model`, and formats it so the View ([Container]) can present it.

 * The lifecycle of a Presenter consists of two methods:
 *  *
 * [.acquire]
 * [.releaseContainer]
 *

 * Control over the [Container] instance starts at [.acquire],
 * and ends at [.releaseContainer].
 * There are no guarantees on the order of calls to these two methods.

 * Presenters survive configuration changes, making it easy to manage data and
 * asynchronous calls.

 * @param                  The [Container] instance this `Presenter` controls.
 * *
 * @param  The `ActivityComponent`.
 */
interface Presenter<C : Container, ActivityComponent> {

    /**
     * Binds the [Container] this `BasePresenter` controls.
     * From this point on, the `Presenter` may manipulate given [Container] instance,
     * until [.releaseContainer] is called.

     * @param container The [Container] to gain control over.
     */
    @MainThread
    fun acquire(container: C, activityComponent: ActivityComponent)

    /**
     * Tells this Presenter to release the [Container] instance.
     * From this point on, the `Presenter` is not allowed to manipulate
     * the [Container] instance supplied to [.acquire] anymore.
     */
    @MainThread
    fun releaseContainer()
}
