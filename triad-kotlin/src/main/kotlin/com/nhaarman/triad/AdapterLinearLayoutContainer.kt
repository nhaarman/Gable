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
import android.widget.LinearLayout

/**
 * An abstract LinearLayout [Container] instance that handles [Presenter] management
 * for use in an adapter View.

 * @param  The specialized [Presenter] type.
 */
abstract class AdapterLinearLayoutContainer<P : Presenter<Container, ActivityComponent>, ActivityComponent>
@JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle),
      AdapterContainer<P> {

    private val activityComponent: ActivityComponent by lazy { findActivityComponent<ActivityComponent>(context) }

    private var attachedToWindow: Boolean = false

    /**
     * Returns the [P] instance that is tied to this `LinearLayoutContainer`.
     */
    override var presenter: P? = null
        set(value) {
            field?.releaseContainer()
            field = value
            if (attachedToWindow) {
                value?.acquire(this, activityComponent)
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        presenter?.acquire(this, activityComponent)
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        presenter?.releaseContainer()

        attachedToWindow = false
    }
}
