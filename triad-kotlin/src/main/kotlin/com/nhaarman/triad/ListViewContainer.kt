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
import android.widget.ListView

/**
 * An abstract ListView [Container] instance that handles [Presenter] management.
 */
abstract class ListViewContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
) : ListView(context, attrs, defStyle), Container {

    private val presenter: Presenter<Container> by lazy { findPresenter(context, this) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (isInEditMode) {
            return
        }

        presenter.acquire(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        presenter.releaseContainer(this)
    }
}
