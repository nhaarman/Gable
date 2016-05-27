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
import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers.anyInt
import org.mockito.Mockito
import org.mockito.Mockito.withSettings

class RelativeLayoutContainerTest {

    private lateinit var relativeLayoutContainer: TestRelativeLayoutContainer

    private lateinit var presenterMock: Presenter<Container, ActivityComponent>

    private lateinit var activityComponentMock: ActivityComponent

    @Before
    fun setUp() {
        val activity = Mockito.mock(Activity::class.java, withSettings().extraInterfaces(ActivityComponentProvider::class.java, ScreenProvider::class.java))
        val application = Mockito.mock(Application::class.java, withSettings().extraInterfaces(TriadProvider::class.java))
        whenever(activity.applicationContext).thenReturn(application)

        val screen = mock<Screen<ApplicationComponent>>()
        presenterMock = mock()
        whenever(screen.getPresenter(anyInt())).thenReturn(presenterMock)

        whenever((activity as ScreenProvider<ApplicationComponent>).currentScreen).thenReturn(screen)
        activityComponentMock = mock()
        whenever((activity as ActivityComponentProvider<ActivityComponent>).activityComponent).thenReturn(activityComponentMock)

        relativeLayoutContainer = spy(TestRelativeLayoutContainer(activity))
        whenever(relativeLayoutContainer.context).thenReturn(activity)
    }

    @Test
    fun getPresenter_returnsProperPresenter() {
        /* Then */
        expect(relativeLayoutContainer.presenter).toBe(presenterMock)
    }

    @Test
    fun onAttachedToWindow_givesControlToPresenter() {
        /* When */
        relativeLayoutContainer.onAttachedToWindow()

        /* Then */
        verify(presenterMock).acquire(relativeLayoutContainer, activityComponentMock)
    }

    @Test
    fun onDetachedFromWindow_releasesPresenterControl() {
        /* When */
        relativeLayoutContainer.onDetachedFromWindow()

        /* Then */
        verify(presenterMock).releaseContainer(relativeLayoutContainer)
    }
}