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
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.android.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

@RunWith(AndroidJUnit4.class)
public class InitialStateTest extends TestActivityInstrumentationTestCase {

    @Test
    public void initially_screenHolderVisible() {
        assertThat(getScreenHolder()).isVisible();
    }

    @Test
    public void initially_screenHolderContainsFirstScreen() {
        assertThat(getScreenHolder().findViewById(com.nhaarman.triad.tests.R.id.view_screen_first), is(not(nullValue())));
    }
}
