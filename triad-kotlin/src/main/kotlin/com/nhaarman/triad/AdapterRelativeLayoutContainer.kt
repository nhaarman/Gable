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

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * An abstract RelativeLayout [Container] instance that handles [Presenter] management
 * for use in an adapter View.

 * @param  The specialized [Presenter] type.
 */
abstract class AdapterRelativeLayoutContainer<P : Presenter<*, ActivityComponent>, ActivityComponent>
@JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle),
      AdapterContainer<P> {

    private val activityComponent: ActivityComponent by lazy { findActivityComponent<ActivityComponent>(context) }

    private var attachedToWindow: Boolean = false

    /**
     * Returns the [P] instance that is tied to this `AdapterRelativeLayoutContainer`.
     */
    @Suppress("UNCHECKED_CAST")
    override var presenter: P? = null
        set(value) {
            field?.let { (it as Presenter<Container, ActivityComponent>).releaseContainer(this) }
            field = value
            value?.let {
                if (attachedToWindow) {
                    (it as Presenter<Container, ActivityComponent>).acquire(this, activityComponent)
                }
            }
        }

    @Suppress("UNCHECKED_CAST")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        presenter?.let {
            (it as Presenter<Container, ActivityComponent>).acquire(this, activityComponent)
        }

        attachedToWindow = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        presenter?.let {
            (it as Presenter<Container, ActivityComponent>).releaseContainer(this)
        }

        attachedToWindow = false
    }

    override final fun context() = context
}
