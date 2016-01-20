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
import android.content.Intent
import com.nhaarman.expect.expect
import com.nhaarman.mockito.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

class TriadTest {

    @Mock
    private lateinit var mListener: Triad.Listener<Any>

    @Mock
    private lateinit var mScreen1: Screen<Any>

    @Mock
    private lateinit var mScreen2: Screen<Any>

    @Mock
    private lateinit var mScreen3: Screen<Any>

    private lateinit var inOrder: InOrder

    @Before
    fun setUp() {
        initMocks(this)
        inOrder = inOrder(mListener)

        doAnswer({ invocationOnMock ->
            val callback = invocationOnMock.arguments[2] as Triad.Callback
            callback.onComplete()
        }).`when`(mListener).forward(any(), any(), any())

        doAnswer({ invocationOnMock ->
            val callback = invocationOnMock.arguments[2] as Triad.Callback
            callback.onComplete()
        }).`when`(mListener).backward(any(), any(), any())

        doAnswer({ invocationOnMock ->
            val callback = invocationOnMock.arguments[2] as Triad.Callback
            callback.onComplete()
        }).`when`(mListener).replace(any(), any(), any())
    }

    @Test(expected = IllegalStateException::class)
    fun startWith_withoutListener_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()

        /* When */
        triad.startWith(mock())
    }

    @Test
    fun startWith_firstTime_forwardsToScreen() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.startWith(mScreen1)

        /* Then */
        inOrder.verify(mListener).screenPushed(mScreen1)
        inOrder.verify(mListener).forward(eq(mScreen1), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test
    fun startWith_secondTime_doesNotMoveForward() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener
        triad.startWith(mScreen1)
        verify(mListener).screenPushed(mScreen1)
        verify(mListener).forward(eq(mScreen1), any(), any())

        /* When */
        triad.startWith(mScreen1)

        /* Then */
        verifyNoMoreInteractions(mListener)
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test
    fun startWith_withAValidBackstack_doesNotMoveForward() {
        /* Given */
        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)

        /* When */
        triad.startWith(mScreen1)

        /* Then */
        verifyNoMoreInteractions(mListener)
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test(expected = IllegalStateException::class)
    fun goTo_withoutHavingABackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.goTo(mScreen1)
    }

    @Test
    fun goTo_withAValidBackstack_forwardsToScreen() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener
        triad.startWith(mScreen1)
        inOrder.verify(mListener).screenPushed(mScreen1)
        inOrder.verify(mListener).forward(eq(mScreen1), any(), any())

        /* When */
        triad.goTo(mScreen2)

        /* Then */
        inOrder.verify(mListener).screenPushed(mScreen2)
        inOrder.verify(mListener).forward(eq(mScreen2), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen1, mScreen2)
    }

    @Test(expected = IllegalStateException::class)
    fun popTo_withAnEmptyBackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.popTo(mScreen1)
    }

    @Test
    fun popTo_withScreenNotOnBackstack_forwardsToScreen() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener
        triad.startWith(mScreen1)
        inOrder.verify(mListener).screenPushed(mScreen1)
        inOrder.verify(mListener).forward(eq(mScreen1), any(), any())

        /* When */
        triad.popTo(mScreen2)

        /* Then */
        inOrder.verify(mListener).screenPushed(mScreen2)
        inOrder.verify(mListener).forward(eq(mScreen2), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen1, mScreen2)
    }

    @Test
    fun popTo_withScreenOnBackstack_backwardsToScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.of(mScreen1, mScreen2), mListener)

        /* When */
        triad.popTo(mScreen1)

        /* Then */
        inOrder.verify(mListener).screenPopped(mScreen2)
        inOrder.verify(mListener).backward(eq(mScreen1), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test
    fun popTo_withMultipleScreensOnBackstack_backwardsToScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.of(mScreen1, mScreen2, mScreen3), mListener)

        /* When */
        triad.popTo(mScreen1)

        /* Then */
        inOrder.verify(mListener).screenPopped(mScreen3)
        inOrder.verify(mListener).screenPopped(mScreen2)
        inOrder.verify(mListener).backward(eq(mScreen1), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test
    fun popTo_withScreenOnTop_doesNothing() {
        /* Given */
        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)

        /* When */
        triad.popTo(mScreen1)

        /* Then */
        verifyNoMoreInteractions(mListener)
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test(expected = IllegalStateException::class)
    fun replaceWith_withAnEmptyBackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.replaceWith(mScreen1)
    }

    @Test
    fun replaceWith_withAValidBackstack_replacesScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)

        /* When */
        triad.replaceWith(mScreen2)

        /* Then */
        verify(mListener).screenPopped(mScreen1)
        verify(mListener).screenPushed(mScreen2)
        verify(mListener).replace(eq(mScreen2), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen2)
    }

    @Test(expected = IllegalStateException::class)
    fun goBack_withAnEmptyBackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()

        /* When */
        triad.goBack()
    }

    @Test
    fun goBack_withSingleBackstackEntry_doesNotPopScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)

        /* When */
        val result = triad.goBack()

        /* Then */
        verify(mListener, never()).screenPopped(mScreen1)
        assertThat(result, `is`(false))
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test
    fun goBack_withMoreThanOneBackstackEntry_backwards() {
        /* Given */
        val triad = Triad.newInstance(Backstack.of(mScreen1, mScreen2), mListener)

        /* When */
        val result = triad.goBack()

        /* Then */
        assertThat(result, `is`(true))
        inOrder.verify(mListener).screenPopped(mScreen2)
        inOrder.verify(mListener).backward(eq(mScreen1), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen1)
    }

    @Test(expected = IllegalStateException::class)
    fun forward_withAnEmptyBackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.forward(Backstack.of(mScreen1, mScreen2))
    }

    @Test
    fun forward_withAValidBackstack_forwardsToLastScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.of(mScreen1, mScreen2), mListener)

        /* When */
        triad.forward(Backstack.of(mScreen2, mScreen3))

        /* Then */
        inOrder.verify(mListener).screenPopped(mScreen2)
        inOrder.verify(mListener).screenPopped(mScreen1)

        inOrder.verify(mListener).screenPushed(mScreen2)
        inOrder.verify(mListener).screenPushed(mScreen3)

        inOrder.verify(mListener).forward(eq(mScreen3), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen2, mScreen3)
    }

    @Test(expected = IllegalStateException::class)
    fun backward_withAnEmptyBackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.backward(Backstack.of(mScreen1, mScreen2))
    }

    @Test
    fun backward_withAValidBackstack_backwardsToLastScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.of(mScreen1, mScreen2), mListener)

        /* When */
        triad.backward(Backstack.of(mScreen2, mScreen3))

        /* Then */

        inOrder.verify(mListener).screenPopped(mScreen2)
        inOrder.verify(mListener).screenPopped(mScreen1)

        inOrder.verify(mListener).screenPushed(mScreen2)
        inOrder.verify(mListener).screenPushed(mScreen3)

        inOrder.verify(mListener).backward(eq(mScreen3), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen2, mScreen3)
    }

    @Test(expected = IllegalStateException::class)
    fun replace_withAnEmptyBackstack_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.emptyInstance()
        triad.listener = mListener

        /* When */
        triad.replace(Backstack.of(mScreen1, mScreen2))
    }

    @Test
    fun replace_withAValidBackstack_replacesToLastScreen() {
        /* Given */
        val triad = Triad.newInstance(Backstack.of(mScreen1, mScreen2), mListener)

        /* When */
        triad.replace(Backstack.of(mScreen2, mScreen3))

        /* Then */
        inOrder.verify(mListener).screenPopped(mScreen2)
        inOrder.verify(mListener).screenPopped(mScreen1)

        inOrder.verify(mListener).screenPushed(mScreen2)
        inOrder.verify(mListener).screenPushed(mScreen3)

        verify(mListener).replace(eq(mScreen3), any(), any())
        assertBackstackHasEntries(triad.backstack, mScreen2, mScreen3)
    }

    @Test(expected = IllegalStateException::class)
    fun startActivity_withoutActivityReference_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)

        /* When */
        triad.startActivity(mock())
    }

    @Test
    fun startActivity_withActivityReference_startsActivity() {
        /* Given */
        val activity = mock<Activity>()
        val intent = mock<Intent>()

        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)
        triad.setActivity(activity)

        /* When */
        triad.startActivity(intent)

        /* Then */
        verify(activity).startActivity(intent)
    }

    @Test(expected = IllegalStateException::class)
    fun startActivityForResult_withoutActivityReference_throwsIllegalStateException() {
        /* Given */
        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)

        /* When */
        triad.startActivityForResult(mock(), mock())
    }

    @Test
    fun startActivityForResult_withActivityReference_startsActivityForResult() {
        /* Given */
        val activity = mock<Activity>()
        val intent = mock<Intent>()
        val resultListener = mock<Triad.ActivityResultListener>()

        val triad = Triad.newInstance(Backstack.single(mScreen1), mListener)
        triad.setActivity(activity)

        /* When */
        triad.startActivityForResult(intent, resultListener)

        /* Then */
        verify(activity).startActivityForResult(eq(intent), any())
    }

    @SuppressWarnings("rawtypes")
    private fun assertBackstackHasEntries(backstack: Backstack, vararg screens: Screen<Any>) {
        assertThat("Backstack size", backstack.size(), `is`(screens.size))

        var i = 0
        val iterator = backstack.reverseIterator()
        while (iterator.hasNext()) {
            val screen = iterator.next()
            expect(screen).toBe(screens[i]) { backstack.toString() }
            i++
        }
    }
}