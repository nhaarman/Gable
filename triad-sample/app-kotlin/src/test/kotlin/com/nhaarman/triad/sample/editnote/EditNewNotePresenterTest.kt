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

import com.nhaarman.mockito.*
import com.nhaarman.triad.Triad
import com.nhaarman.triad.sample.ActivityComponent
import com.nhaarman.triad.sample.MemoryNoteRepository
import com.nhaarman.triad.sample.NoteCreator
import com.nhaarman.triad.sample.NoteValidator
import org.junit.Before
import org.junit.Test


class EditNewNotePresenterTest {

    private lateinit var mEditNotePresenter: EditNotePresenter

    private lateinit var mEditNoteContainerMock: EditNoteContainer

    private lateinit var mNoteValidator: NoteValidator

    private lateinit var mNoteCreatorMock: NoteCreator

    private lateinit var mNoteRepository: MemoryNoteRepository

    private lateinit var mTriad: Triad

    @Before
    fun setUp() {
        mNoteRepository = spy(MemoryNoteRepository())
        mNoteValidator = spy(NoteValidator())
        mNoteCreatorMock = spy(NoteCreator(mNoteRepository))

        mTriad = mock()

        mEditNotePresenter = EditNotePresenter(null, mNoteValidator, mNoteCreatorMock, mNoteRepository, mTriad)

        mEditNoteContainerMock = mock()
        whenever(mEditNoteContainerMock.context()).thenReturn(mock())
    }

    @Test
    fun onControlGained_setsEmptyTitleToEditNoteContainer() {
        /* Given */
        val containerMock = mock<EditNoteContainer>()
        val activityComponentMock = mock<ActivityComponent>()

        /* When */
        mEditNotePresenter.onControlGained(containerMock, activityComponentMock)

        /* Then */
        verify(containerMock).title = ""
    }

    @Test
    fun onControlGained_setsEmptyContentsToEditNoteContainer() {
        /* Given */
        val containerMock = mock<EditNoteContainer>()
        val activityComponentMock = mock<ActivityComponent>()

        /* When */
        mEditNotePresenter.onControlGained(containerMock, activityComponentMock)

        /* Then */
        verify(containerMock).contents = ""
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
        verify<EditNoteContainer>(mEditNoteContainerMock).setTitleError(any())
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
    fun onSaveNoteClickedWithInvalidProperties_staysOnEditNoteScreen() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn("")

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<Triad>(mTriad, never()).goTo(any())
    }

    @Test
    fun onSaveNoteClicked_createsANoteWithTheProperTitle() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<NoteCreator>(mNoteCreatorMock).createNote(eq(TITLE), any())
    }

    @Test
    fun onSaveNoteClicked_createsANoteWithTheProperContents() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<NoteCreator>(mNoteCreatorMock).createNote(any(), eq(CONTENTS))
    }

    @Test
    fun onSaveNoteClicked_persistsTheCreatedNote() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

        /* When */
        mEditNotePresenter.onSaveNoteClicked()

        /* Then */
        verify<MemoryNoteRepository>(mNoteRepository).create(any())
    }

    @Test
    fun onSaveNoteClicked_navigatesBack() {
        /* Given */
        mEditNotePresenter.container = mEditNoteContainerMock

        whenever(mEditNoteContainerMock.title).thenReturn(TITLE)
        whenever(mEditNoteContainerMock.contents).thenReturn(CONTENTS)

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