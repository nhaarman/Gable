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

import android.os.Parcelable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class Screen<ApplicationComponent : Any> {

    lateinit var applicationComponent: ApplicationComponent

    private val presenters = SparseArray<Presenter<*, *>>()

    private val state = SparseArray<Parcelable>()

    protected abstract val layoutResId: Int

    open fun createView(parent: ViewGroup): ViewGroup {
        return LayoutInflater.from(parent.context).inflate(layoutResId, parent, false) as ViewGroup
    }

    open fun getPresenter(viewId: Int): Presenter<*, *> {
        var presenter = presenters.get(viewId)

        if (presenter == null) {
            presenter = createPresenter(viewId)
            presenters.put(viewId, presenter)
        }

        return presenter
    }

    protected abstract fun createPresenter(viewId: Int): Presenter<*, *>

    internal fun setApplicationComponent(applicationComponent: ApplicationComponent) {
        this.applicationComponent = applicationComponent
    }

    internal fun saveState(view: ViewGroup) {
        view.saveHierarchyState(state)
    }

    internal fun restoreState(view: ViewGroup) {
        view.restoreHierarchyState(state)
    }

    open fun onCreate() {
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    open fun onDestroy() {
    }
}
