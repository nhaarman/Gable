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
import android.widget.TextView;
import com.nhaarman.triad.tests.R;
import com.nhaarman.triad.utils.ViewWaiter;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.assertj.android.api.Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class RotationStateTest extends TestActivityInstrumentationTestCase {

    @Test
    public void test_initially_theCounterTextIsZero() throws InterruptedException {
        assertThat(getCounterTV()).hasText("0");
    }

    @Test
    public void test_afterRotation_theCounterTextIsStillZero() throws InterruptedException {
        rotate();
        assertThat(getCounterTV()).hasText("0");
    }

    @Test
    public void test_afterIncrement_theCounterTextIsOne() throws InterruptedException {
        onView(withId(R.id.view_screen_first_button)).perform(click());
        getInstrumentation().waitForIdleSync();
        assertThat(getCounterTV()).hasText("1");
    }

    @Test
    public void test_afterIncrementAndRotation_theCounterTextIsOne() throws InterruptedException {
        onView(withId(R.id.view_screen_first_button)).perform(click());
        rotate();
        assertThat(getCounterTV()).hasText("1");
    }

    private TextView getCounterTV() throws InterruptedException {
        ViewWaiter.waitUntil(ViewWaiter.viewVisible(getScreenHolder(), R.id.view_screen_first_tv));
        return (TextView) getActivity().findViewById(R.id.view_screen_first_tv);
    }
}
