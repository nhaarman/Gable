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

package com.nhaarman.triad;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public class BasePresenterTest {

    private TestPresenter mPresenter;

    @Before
    public void setUp() {
        mPresenter = new TestPresenter();
    }

    @Test
    public void initially_theContainerIsNotPresent() {
        assertThat(mPresenter.container().isPresent(), is(false));
    }

    @Test
    public void initially_theActivityComponentIsNotPresent() {
        assertThat(mPresenter.activityComponent().isPresent(), is(false));
    }

    @Test
    public void afterAcquiringContainer_theContainerIsPresent() {
    /* Given */
        TestRelativeLayoutContainer container = mock(TestRelativeLayoutContainer.class);

    /* When */
        mPresenter.acquire(container, mock(ActivityComponent.class));

    /* Then */
        assertThat(mPresenter.container().isPresent(), is(true));
    }

    @Test
    public void afterAcquiringActivityComponent_theActivityComponentIsPresent() {
    /* Given */
        ActivityComponent activityComponent = mock(ActivityComponent.class);

    /* When */
        mPresenter.acquire(mock(TestRelativeLayoutContainer.class), activityComponent);

    /* Then */
        assertThat(mPresenter.activityComponent().isPresent(), is(true));
    }

    @Test
    public void afterReleasingContainer_theContainerIsNotPresent() {
    /* Given */
        mPresenter.acquire(mock(TestRelativeLayoutContainer.class), mock(ActivityComponent.class));

    /* When */
        mPresenter.releaseContainer();

    /* Then */
        assertThat(mPresenter.container().isPresent(), is(false));
    }

    @Test
    public void afterReleasingContainer_theActivityComponentIsNotPresent() {
    /* Given */
        mPresenter.acquire(mock(TestRelativeLayoutContainer.class), mock(ActivityComponent.class));

    /* When */
        mPresenter.releaseContainer();

    /* Then */
        assertThat(mPresenter.activityComponent().isPresent(), is(false));
    }

    @Test
    public void afterAcquiringContainer_onControlGainedIsCalled() {
    /* Given */
        TestRelativeLayoutContainer container = mock(TestRelativeLayoutContainer.class);

    /* When */
        mPresenter.acquire(container, mock(ActivityComponent.class));

    /* Then */
        assertThat(mPresenter.onControlGainedCalled, is(true));
        assertThat(mPresenter.onControlLostCalled, is(false));
    }

    @Test
    public void afterReleasingContainer_onControlLostIsCalled() {
    /* Given */
        TestRelativeLayoutContainer container = mock(TestRelativeLayoutContainer.class);
        mPresenter.acquire(container, mock(ActivityComponent.class));
        mPresenter.onControlGainedCalled = false;

    /* When */
        mPresenter.releaseContainer();

    /* Then */
        assertThat(mPresenter.onControlLostCalled, is(true));
        assertThat(mPresenter.onControlGainedCalled, is(false));
    }

    @Test
    public void acquiringTheSameContainerTwice_doesNothing() {
    /* Given */
        TestRelativeLayoutContainer container = mock(TestRelativeLayoutContainer.class);
        mPresenter.acquire(container, mock(ActivityComponent.class));
        mPresenter.onControlLostCalled = false;
        mPresenter.onControlGainedCalled = false;

    /* When */
        mPresenter.acquire(container, mock(ActivityComponent.class));

    /* Then */
        assertThat(mPresenter.onControlLostCalled, is(false));
        assertThat(mPresenter.onControlGainedCalled, is(false));
    }

    @Test
    public void acquiringAnotherContainer_firstReleasesTheCurrentContainer() {
    /* Given */
        TestRelativeLayoutContainer container1 = mock(TestRelativeLayoutContainer.class);
        TestRelativeLayoutContainer container2 = mock(TestRelativeLayoutContainer.class);

        mPresenter.acquire(container1, mock(ActivityComponent.class));
        mPresenter.onControlLostCalled = false;
        mPresenter.onControlGainedCalled = false;

    /* When */
        mPresenter.acquire(container2, mock(ActivityComponent.class));

    /* Then */
        assertThat(mPresenter.onControlLostCalled, is(true));
        assertThat(mPresenter.onControlGainedCalled, is(true));
    }
}