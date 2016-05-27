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

import android.app.Activity;
import android.app.Application;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class RelativeLayoutContainerTest {

    private TestRelativeLayoutContainer mRelativeLayoutContainer;

    @SuppressWarnings("rawtypes")
    private Presenter mPresenterMock;

    private ActivityComponent mActivityComponentMock;

    @Before
    public void setUp() {
        Activity activity = mock(Activity.class, withSettings().extraInterfaces(ActivityComponentProvider.class, ScreenProvider.class));
        Application application = mock(Application.class, withSettings().extraInterfaces(TriadProvider.class));
        when(activity.getApplicationContext()).thenReturn(application);

        Screen<ApplicationComponent> screen = mock(Screen.class);
        mPresenterMock = mock(Presenter.class);
        when(screen.getPresenter(anyInt())).thenReturn(mPresenterMock);

        when(((ScreenProvider<ApplicationComponent>) activity).getCurrentScreen()).thenReturn(screen);
        mActivityComponentMock = mock(ActivityComponent.class);
        when(((ActivityComponentProvider<ActivityComponent>) activity).getActivityComponent()).thenReturn(mActivityComponentMock);

        mRelativeLayoutContainer = spy(new TestRelativeLayoutContainer(activity, null, 0));
        when(mRelativeLayoutContainer.getContext()).thenReturn(activity);
    }

    @Test
    public void getPresenter_returnsProperPresenter() {
        /* Then */
        assertThat(mRelativeLayoutContainer.getPresenter(), is(mPresenterMock));
    }

    @Test
    public void onAttachedToWindow_givesControlToPresenter() {
        /* When */
        mRelativeLayoutContainer.onAttachedToWindow();

        /* Then */
        verify(mPresenterMock).acquire(mRelativeLayoutContainer, mActivityComponentMock);
    }

    @Test
    public void onDetachedFromWindow_releasesPresenterControl() {
        /* When */
        mRelativeLayoutContainer.onDetachedFromWindow();

        /* Then */
        verify(mPresenterMock).releaseContainer(mRelativeLayoutContainer);
    }
}