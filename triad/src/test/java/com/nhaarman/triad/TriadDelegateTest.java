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
import android.view.ViewGroup;
import com.nhaarman.triad.ApplicationComponentProvider;
import com.nhaarman.triad.Backstack;
import com.nhaarman.triad.Screen;
import com.nhaarman.triad.Triad;
import com.nhaarman.triad.TriadDelegate;
import com.nhaarman.triad.TriadProvider;
import com.nhaarman.triad.TriadView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.mockito.MockitoAnnotations.initMocks;

public class TriadDelegateTest {

    @Mock
    private Activity mActivity;

    private Application mApplication;

    @Mock
    private Triad.Listener<Object> mListener;

    @Mock
    private Screen<Object> mScreen1;

    @Mock
    private Screen<Object> mScreen2;

    @Before
    public void setup() {
        initMocks(this);

        mApplication = mock(Application.class, withSettings().extraInterfaces(TriadProvider.class, ApplicationComponentProvider.class));

        when(mActivity.getApplication()).thenReturn(mApplication);
        when(mActivity.findViewById(anyInt())).thenReturn(mock(TriadView.class));
        when(mScreen2.createView(any(ViewGroup.class))).thenReturn(mock(ViewGroup.class));
    }

    @Test
    public void onDestroy_notifiesBackstackScreenPopped() {
        /* Given */
        TriadDelegate<Object> delegate = TriadDelegate.Companion.createFor(mActivity);
        when(((TriadProvider) mApplication).getTriad()).thenReturn(Triad.Companion.newInstance(Backstack.Companion.of(mScreen1, mScreen2), mListener));
        delegate.onCreate();

        /* When */
        delegate.onDestroy();

        /* Then */
        verify(mScreen2).onDestroy();
        verify(mScreen1).onDestroy();
    }
}