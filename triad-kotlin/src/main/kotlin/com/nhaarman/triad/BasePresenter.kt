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

import android.content.res.Resources
import android.support.annotation.MainThread

/**
 * A default base implementation of the [Presenter] interface.

 * This class handles acquiring and releasing of the [Container] instance,
 * and forwards these calls to one of the following lifecycle methods:

 * [.onControlGained]
 * [.onControlLost]
 *

 * Control over the `Container` instance starts at [.onControlGained],
 * and ends at [.onControlLost].

 * An [Optional] reference to the `Container` instance can be obtained using [.container],
 * which returns an empty reference when the `Presenter` does not have control over the instance.

 * @param The activity component.
 *
 * @param The specialized type of the [Container].
 */
open class BasePresenter<C : Container, ActivityComponent> : Presenter<C, ActivityComponent> {

    /**
     * The [Container] this [BasePresenter] controls.
     */
    var container: C? = null
        set(value) {
            field = value
            resources = value?.context()?.resources
        }

    var resources: Resources? = null

    var activityComponent: ActivityComponent? = null

    /**
     * Sets the [C] this `BasePresenter` controls, and calls [.onControlGained] )}
     * to notify implementers of this class that the [C] is available.

     * @param container The [C] to gain control over.
     */
    @MainThread
    override fun acquire(container: C, activityComponent: ActivityComponent) {
        if (container == this.container) {
            return
        }

        this.container?.apply {
            onControlLost()
        }

        this.container = container

        this.activityComponent = activityComponent
        onControlGained(container, activityComponent)
    }

    /**
     * Releases the [C] this `BasePresenter` controls, and calls [.onControlLost]
     * to notify implementers of this class that the [C] is no longer available.
     */
    @MainThread
    override fun releaseContainer() {
        if (container == null) {
            return
        }

        container = null
        activityComponent = null
        onControlLost()
    }

    /**
     * Called when the [Container] for this `BasePresenter` is attached to the window and ready to display the state.

     * From this point on, [.container] will return the [Container] instance, until [.onControlLost] is called.

     * @param container The [Container] to gain control over.
     */
    @MainThread
    protected open fun onControlGained(container: C, activityComponent: ActivityComponent) {
    }

    /**
     * Called when this `BasePresenter` no longer controls the [Container] instance.

     * From this point on, [.container] will return an empty [Optional].
     */
    @MainThread
    protected open fun onControlLost() {
    }
}
