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
import android.content.Context
import android.content.ContextWrapper


@Suppress("UNCHECKED_CAST")
fun <ActivityComponent> findActivityComponent(context: Context): ActivityComponent {
    var baseContext = context
    while (baseContext !is Activity && baseContext is ContextWrapper) {
        baseContext = baseContext.baseContext
    }

    if (baseContext is ActivityComponentProvider<*>) {
        return (baseContext as ActivityComponentProvider<ActivityComponent>).activityComponent
    } else {
        throw Error()
    }
}

@Suppress("UNCHECKED_CAST")
fun <P : Presenter<*, *>> findPresenter(context: Context, viewId: Int): P {
    var baseContext = context
    while (baseContext !is Activity && baseContext is ContextWrapper) {
        baseContext = baseContext.baseContext
    }

    if (baseContext is ScreenProvider<*>) {
        return baseContext.currentScreen.getPresenter(viewId) as P
    } else {
        throw Error()
    }
}
