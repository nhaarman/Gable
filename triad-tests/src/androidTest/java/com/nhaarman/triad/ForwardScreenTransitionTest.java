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

import android.support.test.runner.AndroidJUnit4;
import com.nhaarman.triad.tests.secondscreen.SecondScreen;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.nhaarman.triad.utils.ViewWaiter.viewNotPresent;
import static com.nhaarman.triad.utils.ViewWaiter.waitUntil;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(AndroidJUnit4.class)
public class ForwardScreenTransitionTest extends TestActivityInstrumentationTestCase {

    @Before
    public void setUp() throws InterruptedException {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getTriad().goTo(new SecondScreen());
            }
        });

        waitUntil(viewNotPresent(getScreenHolder(), com.nhaarman.triad.tests.R.id.view_screen_first));
    }

    @Test
    public void afterTransition_viewsHaveBeenSwitched() throws InterruptedException {
        assertThat(getScreenHolder().findViewById(com.nhaarman.triad.tests.R.id.view_screen_second), is(not(nullValue())));
        assertThat(getScreenHolder().findViewById(com.nhaarman.triad.tests.R.id.view_screen_first), is(nullValue()));
    }
}
