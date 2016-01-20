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

import org.mockito.Answers
import org.mockito.Mockito
import org.mockito.internal.creation.MockSettingsImpl
import org.mockito.internal.util.MockUtil
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.verification.VerificationMode
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaType

inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)
inline fun <reified T : Any> spy(value: T) = Mockito.spy(value)
fun inOrder(vararg value: Any) = Mockito.inOrder(*value)

fun doReturn(toBeReturned: Any) = Mockito.doReturn(toBeReturned)
fun <T> doAnswer(answer: (InvocationOnMock) -> T) = Mockito.doAnswer(answer)
fun <T> doAnswer(answer: Answer<T>) = Mockito.doAnswer(answer)
fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)
fun <T> verify(mock: T) = Mockito.verify(mock)
fun <T> verify(mock: T, mode: VerificationMode) = Mockito.verify(mock, mode)
fun <T> verifyNoMoreInteractions(mock: T) = Mockito.verifyNoMoreInteractions(mock)
fun <T> reset(mock: T) = Mockito.reset(mock)

fun never() = Mockito.never()

@Suppress("USELESS_CAST")
fun <T> eq(value: T) = Mockito.eq(value) as T

@Suppress("UNCHECKED_CAST")
fun <T> isNull(): T = Mockito.isNull() as T

inline fun <reified T : Any> any() = any(T::class)

fun <T : Any> any(clzz: KClass<T>) = Mockito.any(clzz.java) ?: createNonNullInstance(clzz)

internal fun <T : Any> createNonNullInstance(kClass: KClass<T>): T {
    if (!Modifier.isFinal(kClass.java.modifiers)) {
        return uncheckedMock(kClass.java)
    }

    if (kClass.java.isEnum) {
        return kClass.java.enumConstants[0]
    }

    if (kClass.isArray()) {
        return intArrayOf() as T
    }

    val construct = kClass.constructors
          .sortedBy { it.parameters.size }
          .first()

    val params: MutableMap<KParameter, Any?> = HashMap()

    val requiredParameters = construct.parameters
          .filter { !it.isOptional }

    requiredParameters.forEach {
        val type = it.type
        if (type.isMarkedNullable) {
            params.put(it, null)
        } else {
            val javaType = type.javaType
            if (javaType is ParameterizedType) {
                val rawType = javaType.rawType
                params.put(it, createNonNullInstance(rawType as Class<*>))
            } else if ((javaType as Class<*>).isPrimitive) {
                params.put(it, defaultPrimitive(javaType))
            } else {
                params.put(it, createNonNullInstance(javaType))
            }
        }
    }

    return construct.callBy(params)
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
private fun <T : Any> createNonNullInstance(jClass: Class<T>): T? {
    if (!Modifier.isFinal(jClass.modifiers)) {
        return uncheckedMock(jClass)
    }

    if (jClass.isPrimitive || jClass == String::class.java) {
        return defaultPrimitive(jClass)
    }

    val constructor = jClass.constructors
          .sortedBy { it.parameterTypes.size }
          .first()

    val parameters = constructor.parameterTypes
    if (parameters.size == 0) {
        return constructor.newInstance() as T
    }

    val params: MutableList<Any?> = ArrayList()
    parameters.forEach {
        params.add(createNonNullInstance(it))
    }

    return when (params.size) {
        1 -> constructor.newInstance(params[0])
        2 -> constructor.newInstance(params[0], params[1])
        else -> throw UnsupportedOperationException("${params.size} is a lot of arguments!")
    } as T
}

@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
private fun <T> defaultPrimitive(clzz: Class<T>): T {
    return when (clzz.canonicalName) {
        "int" -> 0
        "java.lang.String" -> ""
        else -> throw UnsupportedOperationException()
    } as T
}

private fun <T> uncheckedMock(clzz: Class<T>): T {
    val impl = MockSettingsImpl<T>().defaultAnswer(Answers.RETURNS_DEFAULTS) as MockSettingsImpl<*>
    val creationSettings = impl.confirm(clzz)
    return MockUtil().createMock(creationSettings) as T
}

private fun <T : Any> KClass<T>.isArray(): Boolean {
    return simpleName?.endsWith("Array") ?: toString().endsWith("Array")
}