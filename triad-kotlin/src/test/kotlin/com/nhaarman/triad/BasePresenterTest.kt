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

import com.nhaarman.expect.expect
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test

@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
class BasePresenterTest {

    private lateinit var presenter: TestPresenter

    @Before
    fun setUp() {
        presenter = TestPresenter()
    }

    @Test
    fun initially_theContainerIsNotPresent() {
        expect(presenter.container).toBeNull()
    }

    @Test
    fun initially_theActivityComponentIsNotPresent() {
        expect(presenter.activityComponent).toBeNull()
    }

    @Test
    fun afterAcquiringContainer_theContainerIsPresent() {
        /* Given */
        val container = mock<TestRelativeLayoutContainer>()

        /* When */
        presenter.acquire(container, mock())

        /* Then */
        expect(presenter.container).toBe(container)
    }

    @Test
    fun afterAcquiringActivityComponent_theActivityComponentIsPresent() {
        /* Given */
        val activityComponent = mock<ActivityComponent>()

        /* When */
        presenter.acquire(mock(), activityComponent)

        /* Then */
        expect(presenter.activityComponent).toBe(activityComponent)
    }

    @Test
    fun afterReleasingContainer_theContainerIsNotPresent() {
        /* Given */
        val container = mock<TestRelativeLayoutContainer>()
        presenter.acquire(container, mock())

        /* When */
        presenter.releaseContainer(container)

        /* Then */
        expect(presenter.container).toBeNull()
    }

    @Test
    fun afterReleasingContainer_theActivityComponentIsNotPresent() {
        /* Given */
        val container = mock<TestRelativeLayoutContainer>()
        presenter.acquire(container, mock())

        /* When */
        presenter.releaseContainer(container)

        /* Then */
        expect(presenter.activityComponent).toBeNull()
    }

    @Test
    fun afterAcquiringContainer_onControlGainedIsCalled() {
        /* Given */
        val container = mock<TestRelativeLayoutContainer>()

        /* When */
        presenter.acquire(container, mock())

        /* Then */
        expect(presenter.onControlGainedCalled).toBe(true)
        expect(presenter.onControlLostCalled).toBe(false)
    }

    @Test
    fun afterReleasingContainer_onControlLostIsCalled() {
        /* Given */
        val container = mock<TestRelativeLayoutContainer>()
        presenter.acquire(container, mock())
        presenter.onControlGainedCalled = false

        /* When */
        presenter.releaseContainer(container)

        /* Then */
        expect(presenter.onControlLostCalled).toBe(true)
        expect(presenter.onControlGainedCalled).toBe(false)
    }

    @Test
    fun acquiringTheSameContainerTwice_doesNothing() {
        /* Given */
        val container = mock<TestRelativeLayoutContainer>()
        presenter.acquire(container, mock())
        presenter.onControlLostCalled = false
        presenter.onControlGainedCalled = false

        /* When */
        presenter.acquire(container, mock())

        /* Then */
        expect(presenter.onControlLostCalled).toBe(false)
        expect(presenter.onControlGainedCalled).toBe(false)
    }

    @Test
    fun acquiringAnotherContainer_firstReleasesTheCurrentContainer() {
        /* Given */
        val container1 = mock<TestRelativeLayoutContainer>()
        val container2 = mock<TestRelativeLayoutContainer>()

        presenter.acquire(container1, mock())
        presenter.onControlLostCalled = false
        presenter.onControlGainedCalled = false

        /* When */
        presenter.acquire(container2, mock())

        /* Then */
        expect(presenter.onControlLostCalled).toBe(true)
        expect(presenter.onControlGainedCalled).toBe(true)
    }


    @Test
    fun releasingADifferentContainer_doesNotCallOnControlLost() {
        /* Given */
        val container1 = mock<TestRelativeLayoutContainer>()
        val container2 = mock<TestRelativeLayoutContainer>()

        presenter.acquire(container1, mock())
        presenter.onControlLostCalled = false
        presenter.onControlGainedCalled = false

        /* When */
        presenter.releaseContainer(container2)

        /* Then */
        expect(presenter.onControlLostCalled).toBe(false)
    }
}