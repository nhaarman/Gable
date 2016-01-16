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

package com.nhaarman.mockito

import org.mockito.ArgumentCaptor

inline fun <reified T : Any> argumentCaptor(): KotlinArgumentCaptor<T> {
    return KotlinArgumentCaptor(T::class.java)
}

class KotlinArgumentCaptor<T>(clazz: Class<T>) {

    val argumentCaptor: ArgumentCaptor<T> = ArgumentCaptor.forClass(clazz)

    fun capture(): T {
        @Suppress("USELESS_CAST")
        return argumentCaptor.capture() as T
    }

    fun getValue(): T {
        return argumentCaptor.value
    }
}