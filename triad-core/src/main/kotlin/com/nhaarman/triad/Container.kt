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

/**
 * The View class in the MVP context.

 * In the Model-View-Presenter pattern, the View displays data formatted by the
 * [Presenter], and notifies the `Presenter` of user input.
 */
interface Container {

    /**
     * Returns the context the container is running in, through which it can
     * access the current theme, resources, etc.

     * @return The container's Context.
     */
    fun context(): Context
}
