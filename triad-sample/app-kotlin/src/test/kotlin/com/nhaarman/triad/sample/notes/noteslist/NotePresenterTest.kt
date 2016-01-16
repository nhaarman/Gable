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

import com.nhaarman.triad.sample.ActivityComponent
import com.nhaarman.triad.sample.Note
import org.junit.Before
import org.junit.Test

import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class NotePresenterTest {

    private lateinit var noteContainerMock: NoteContainer

    private lateinit var note: Note

    @Before
    fun setUp() {
        noteContainerMock = mock(NoteContainer::class.java)

        note = Note()
        note.title = TITLE
        note.contents = CONTENTS
    }

    @Test
    fun onControlGained_setsTitleToContainer() {
        /* Given */
        val notePresenter = NotePresenter(note)

        /* When */
        notePresenter.acquire(noteContainerMock, mock(ActivityComponent::class.java))

        /* Then */
        verify(noteContainerMock).setTitle(TITLE)
    }

    @Test
    fun onControlGained_setsContentsToContainer() {
        /* Given */
        val notePresenter = NotePresenter(note)

        /* When */
        notePresenter.acquire(noteContainerMock, mock(ActivityComponent::class.java))

        /* Then */
        verify<NoteContainer>(noteContainerMock).setContents(CONTENTS)
    }

    companion object {

        private val TITLE = "title"

        private val CONTENTS = "contents"
    }
}