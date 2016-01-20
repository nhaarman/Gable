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
import android.app.Application
import com.nhaarman.mockito.any
import com.nhaarman.mockito.mock
import com.nhaarman.mockito.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations.initMocks

class TriadDelegateTest {

    @Mock
    private lateinit var activity: Activity

    private var mApplication: Application? = null

    @Mock
    private lateinit var mListener: Triad.Listener<Any>

    @Mock
    private lateinit var mScreen1: Screen<Any>

    @Mock
    private lateinit var mScreen2: Screen<Any>

    @Before
    fun setup() {
        initMocks(this)

        mApplication = Mockito.mock(Application::class.java, Mockito.withSettings().extraInterfaces(TriadProvider::class.java, ApplicationComponentProvider::class.java))

        whenever(activity.application).thenReturn(mApplication)
        whenever(activity.findViewById(any())).thenReturn(mock<TriadView>())
        whenever(mScreen2.createView(any())).thenReturn(mock())
    }

    @Test
    fun onDestroy_notifiesBackstackScreenPopped() {
        /* Given */
        val delegate = TriadDelegate.createFor<Any>(activity)
        whenever((mApplication as TriadProvider).triad).thenReturn(Triad.newInstance(Backstack.of(mScreen1, mScreen2), mListener))
        delegate.onCreate()

        /* When */
        delegate.onDestroy()

        /* Then */
        verify(mScreen2).onDestroy()
        verify(mScreen1).onDestroy()
    }
}