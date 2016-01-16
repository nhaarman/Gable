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

package com.nhaarman.triad.sample.notes.noteslist

import com.nhaarman.mockito.*
import com.nhaarman.triad.sample.ActivityComponent
import com.nhaarman.triad.sample.MemoryNoteRepository
import com.nhaarman.triad.sample.Note
import com.nhaarman.triad.sample.NoteRepository
import org.junit.Before
import org.junit.Test

class NotesListPresenterTest {

    private lateinit var mNotesListPresenter: NotesListPresenter

    private lateinit var mNoteRepository: NoteRepository

    private lateinit var mNote1: Note

    private lateinit var mNote2: Note

    private lateinit var mOnNoteClickedListener: NotesListPresenter.OnNoteClickedListener

    @Before
    fun setUp() {
        mNoteRepository = spy(MemoryNoteRepository())
        mOnNoteClickedListener = mock()
        mNotesListPresenter = NotesListPresenter(mNoteRepository, mOnNoteClickedListener)

        mNote1 = Note()
        mNoteRepository.create(mNote1)

        mNote2 = Note()
        mNoteRepository.create(mNote2)

        reset(mNoteRepository)
    }

    @Test
    fun onControlGained_requestsAllNotes() {
        /* When */
        mNotesListPresenter.onControlGained(mock(), ActivityComponent())

        /* Then */
        verify(mNoteRepository).findAll()
    }

    @Test
    fun onControlGained_setsNotePresentersToContainer() {
        /* Given */
        val containerMock = mock<NotesListContainer>()

        /* When */
        mNotesListPresenter.onControlGained(containerMock, ActivityComponent())

        /* Then */
        verify(containerMock).setNotes(argThat { size == 2 })
    }

    @Test
    fun onNoteClicked_notifiesListener() {
        /* Given */
        mNotesListPresenter.acquire(mock(), mock())

        /* When */
        mNotesListPresenter.onNoteClicked(0)

        /* Then */
        verify(mOnNoteClickedListener).onNoteClicked(mNote1)
    }
}