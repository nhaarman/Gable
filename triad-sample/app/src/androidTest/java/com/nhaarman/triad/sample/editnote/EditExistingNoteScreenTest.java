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

package com.nhaarman.triad.sample.editnote;

import android.support.test.runner.AndroidJUnit4;
import com.nhaarman.triad.TriadActivityInstrumentationTestCase;
import com.nhaarman.triad.sample.MainActivity;
import com.nhaarman.triad.sample.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EditExistingNoteScreenTest extends TriadActivityInstrumentationTestCase<MainActivity> {

    private static final String TITLE = "Some title";

    private static final String CONTENTS = "Some contents";

    public EditExistingNoteScreenTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        getActivity();
        createNote(TITLE, CONTENTS);
        onView(withText(TITLE)).perform(click());
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void editNoteScreen_hasTitleEditText() {
        onView(withHint(R.string.title)).check(matches(withText(TITLE)));
    }

    @Test
    public void editNoteScreen_hasContentsEditText() {
        onView(withHint(R.string.contents)).check(matches(withText(CONTENTS)));
    }

    @Test
    public void editNoteScreen_hasSaveButton() {
        onView(withText(R.string.save)).check(matches(isDisplayed()));
    }

    @Test
    public void savingEmptyTitle_showsError() {
    /* Given */
        onView(withText(TITLE)).perform(clearText());

    /* When */
        onView(withText(R.string.save)).perform(click());

    /* Then */
        onView(withHint(R.string.error_title)).check(matches(isDisplayed()));
    }

    @Test
    public void savingEmptyContents_showsError() {
    /* Given */
        onView(withText(CONTENTS)).perform(clearText());

    /* When */
        onView(withText(R.string.save)).perform(click());

    /* Then */
        onView(withHint(R.string.error_contents)).check(matches(isDisplayed()));
    }

    @Test
    public void savingValidContents_movesBackToNotesScreen() {
    /* When */
        onView(withText(R.string.save)).perform(click());

    /* Then */
        onView(withId(R.id.view_notes)).check(matches(isDisplayed()));
    }
}
