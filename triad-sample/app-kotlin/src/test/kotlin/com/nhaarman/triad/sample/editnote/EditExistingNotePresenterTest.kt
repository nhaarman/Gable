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

package com.nhaarman.triad.sample.editnote

import android.content.Context
import com.nhaarman.mockito.*
import com.nhaarman.triad.Triad
import com.nhaarman.triad.sample.*
import org.junit.Before
import org.junit.Test

class EditExistingNotePresenterTest {

    private lateinit var mEditNotePresenter: EditNotePresenter

    private lateinit var mEditNoteContainerMock: EditNoteContainer

    private lateinit var mNote: Note

    private lateinit var mNoteRepository: NoteRepository

    private lateinit var mNoteValidator: NoteValidator

    private lateinit var mTriad: Triad

    @Before
    fun setUp() {
        mNote = Note()
        mNote.title = TITLE
        mNote.contents = CONTENTS

        mNoteValidator = spy(NoteValidator())
        mNoteRepository = spy(MemoryNoteRepository())
        mTriad = mock()

        mEditNotePresenter = EditNotePresenter(mNote, mNoteValidator, mock(), mNoteRepository, mTriad)

        mEditNoteContainerMock = mock<EditNoteContainer>()
        whenever(mEditNoteContainerMock.context()).thenReturn(mock<Context>())
    }

    @Test
    fun onControlGained_setsNoteTitleToEditNoteContainer() {
        /* Given */
        val containerMock = mock<EditNoteContainer>()
        val activityComponentMock = mock<ActivityComponent>()

        /* When */
        mEditNotePresenter.onControlGained(containerMock, activityComponentMock)

        /* Then */
        verify(containerMock).title = TITLE
    }

    @Test
    fun onControlGained_setsNoteContentsToEditNoteContainer() {
        /* Given */
        val containerMock = mock<EditNoteContainer>()
        val activityComponentMock = mock<ActivityComponent>()

        /* When */
        mEditNotePresenter.onControlGained(containerMock, activityComponentMock)

        /* Then */
        verify(containerMock).contents = CONTENTS
    }

    @Test
    fun onSaveNoteClicked_validatesTitle() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<NoteValidator>(mNoteValidator).validateTitle(TITLE)
    }

    @Test
    fun onSaveNoteClicked_validatesContents() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<NoteValidator>(mNoteValidator).validateContents(CONTENTS)
    }

    @Test
    fun onSaveNoteClickedWithEmptyTitle_showsErrorMessage() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn("")
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify(mEditNoteContainerMock).setTitleError(any())
    }

    @Test
    fun onSaveNoteClickedWithEmptyContents_showsErrorMessage() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn("")

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<EditNoteContainer>(mEditNoteContainerMock).setContentsError(any())
    }

    @Test
    fun onSaveNoteClicked_persistsTheNote() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<NoteRepository>(mNoteRepository).update(mNote)
    }

    @Test
    fun onSaveNoteClicked_navigatesBack() {
        /* Given */
        val containerMock = mock<EditNoteContainer>()
        mEditNotePresenter.container = containerMock

        whenever(containerMock.title).thenReturn(TITLE)
        whenever(containerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<Triad>(mTriad).goBack()
    }

    companion object {

        private val TITLE = "title"

        private val CONTENTS = "contents"
    }
}