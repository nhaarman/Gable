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

package com.nhaarman.triad.sample.notes;

import android.support.test.runner.AndroidJUnit4;
import com.nhaarman.triad.TriadActivityInstrumentationTestCase;
import com.nhaarman.triad.sample.MainActivity;
import com.nhaarman.triad.sample.R;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class NotesScreenTest extends TriadActivityInstrumentationTestCase<MainActivity> {

    private static final String TITLE = "Some title";

    private static final String CONTENTS = "Some contents";

    public NotesScreenTest() {
        super(MainActivity.class);
    }

    @Test
    public void notesScreen_hasCreateNoteButton() {
        onView(withText(R.string.create_note)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingCreateNotesButton_transitionsToEditNoteScreen() {
    /* When */
        onView(withText(R.string.create_note)).perform(click());

    /* Then */
        onView(withId(R.id.editNoteView)).check(matches(isDisplayed()));
    }

    @Test
    public void afterCreatingANewNote_theCreatedNoteAppearsOnScreen() throws InterruptedException {
    /* When */
        createNote(TITLE, CONTENTS);

    /* Then */
        onView(withText(TITLE)).check(matches(isDisplayed()));
        onView(withText(CONTENTS)).check(matches(isDisplayed()));
    }

    @Test
    public void clickingANote_transitionsToEditNoteScreen() throws InterruptedException {
    /* Given */
        createNote(TITLE, CONTENTS);

    /* When */
        onView(withText(TITLE)).perform(click());

    /* Then */
        onView(withId(R.id.editNoteView)).check(matches(isDisplayed()));
    }

    @Test
    public void afterCreatingANoteAndRotating_theCreatedNoteStillAppearsOnScreen() throws InterruptedException {
    /* Given */
        createNote(TITLE, CONTENTS);

    /* When */
        rotate();

    /* Then */
        onView(withText(TITLE)).check(matches(isDisplayed()));
        onView(withText(CONTENTS)).check(matches(isDisplayed()));
    }
}
