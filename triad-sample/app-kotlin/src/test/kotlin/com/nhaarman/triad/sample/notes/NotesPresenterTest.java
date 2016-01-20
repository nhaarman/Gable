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

import com.nhaarman.triad.TransitionAnimator;
import com.nhaarman.triad.Triad;
import com.nhaarman.triad.sample.Note;
import com.nhaarman.triad.sample.editnote.EditNoteScreen;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NotesPresenterTest {

    private NotesPresenter mNotesPresenter;

    private Triad mTriad;

    @Before
    public void setUp() {
        mTriad = mock(Triad.class);

        mNotesPresenter = new NotesPresenter(mTriad);
    }

    @Test
    public void onCreateNoteClicked_goesToEditNoteScreen() {
    /* When */
        mNotesPresenter.onCreateNoteClicked();

    /* Then */
        verify(mTriad).goTo(any(EditNoteScreen.class), any(TransitionAnimator.class));
    }

    @Test
    public void onNoteClicked_goesToEditNoteScreen() {
    /* Given */
        Note note = new Note();

    /* When */
        mNotesPresenter.onNoteClicked(note);

    /* Then */
        verify(mTriad).goTo(any(EditNoteScreen.class));
    }
}
