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

package com.nhaarman.triad.sample.notes.noteslist;

import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.MemoryNoteRepository;
import com.nhaarman.triad.sample.Note;
import com.nhaarman.triad.sample.NoteRepository;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class NotesListPresenterTest {

    private NotesListPresenter mNotesListPresenter;

    private NoteRepository mNoteRepository;

    private Note mNote1;

    private Note mNote2;

    private NotesListPresenter.OnNoteClickedListener mOnNoteClickedListener;

    @Before
    public void setUp() {
        mNoteRepository = spy(new MemoryNoteRepository());
        mOnNoteClickedListener = mock(NotesListPresenter.OnNoteClickedListener.class);
        mNotesListPresenter = new NotesListPresenter(mNoteRepository, mOnNoteClickedListener);

        mNote1 = new Note();
        mNoteRepository.create(mNote1);

        mNote2 = new Note();
        mNoteRepository.create(mNote2);

        reset(mNoteRepository);
    }

    @Test
    public void onControlGained_requestsAllNotes() {
    /* When */
        mNotesListPresenter.onControlGained(mock(NotesListContainer.class), new ActivityComponent());

    /* Then */
        verify(mNoteRepository).findAll();
    }

    @Test
    public void onControlGained_setsNotePresentersToContainer() {
    /* Given */
        NotesListContainer containerMock = mock(NotesListContainer.class);

    /* When */
        mNotesListPresenter.onControlGained(containerMock, new ActivityComponent());

    /* Then */
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(containerMock).setNotes((List<NotePresenter>) captor.capture());

        List<NotePresenter> notePresenters = captor.getValue();
        assertThat(notePresenters.size(), is(2));
    }

    @Test
    public void onNoteClicked_notifiesListener() {
    /* Given */
        mNotesListPresenter.acquire(mock(NotesListContainer.class), mock(ActivityComponent.class));

    /* When */
        mNotesListPresenter.onNoteClicked(0);

    /* Then */
        verify(mOnNoteClickedListener).onNoteClicked(mNote1);
    }
}