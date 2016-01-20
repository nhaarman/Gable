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

package com.nhaarman.triad.sample;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class NoteCreatorTest {

    private NoteCreator mNoteCreator;

    private NoteRepository mNoteRepository;

    @Before
    public void setUp() {
        mNoteRepository = spy(new MemoryNoteRepository());
        mNoteCreator = new NoteCreator(mNoteRepository);
    }

    @Test
    public void creatingANote_isSuccessful() {
    /* Given */
        String title = "title";
        String contents = "contents";

    /* When */
        Note note = mNoteCreator.createNote(title, contents);

    /* Then */
        assertThat(note.getTitle(), is(title));
        assertThat(note.getContents(), is(contents));
        assertThat(note.getCreated(), is(greaterThan(System.currentTimeMillis() - 5000)));
    }

    @Test
    public void aCreatedNote_isPersisted() {
    /* Given */
        String title = "title";
        String contents = "contents";

    /* When */
        Note note = mNoteCreator.createNote(title, contents);

    /* Then */
        verify(mNoteRepository).create(note);
    }
}