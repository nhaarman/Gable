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

import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;

public class MemoryNoteRepositoryTest {

    private NoteRepository mNoteRepository;

    @Before
    public void setUp() {
        mNoteRepository = new MemoryNoteRepository();
    }

    @Test
    public void creatingAFirstNote_returnsProperId() {
    /* Given */
        Note note = new Note();

    /* When */
        Long id = mNoteRepository.create(note);

    /* Then */
        assertThat(id, is(1L));
    }

    @Test
    public void creatingASecondNote_returnsProperId() {
    /* Given */
        Note note1 = new Note();
        mNoteRepository.create(note1);
        Note note2 = new Note();

    /* When */
        Long id = mNoteRepository.create(note2);

    /* Then */
        assertThat(id, is(2L));
    }

    @Test
    public void aCreatedNote_canBeRetrieved() {
     /* Given */
        Note note = new Note();
        Long id = mNoteRepository.create(note);

    /* When */
        Note result = mNoteRepository.find(id);

    /* Then */
        assertThat(result, is(note));
    }

    @Test
    public void aSecondCreatedNote_canBeRetrieved() {
    /* Given */
        Note note1 = new Note();
        mNoteRepository.create(note1);
        Note note2 = new Note();
        Long id = mNoteRepository.create(note2);

    /* When */
        Note result = mNoteRepository.find(id);

    /* Then */
        assertThat(result, is(note2));
    }

    @Test
    public void theFirstCreatedNote_canBeRetrieved() {
    /* Given */
        Note note1 = new Note();
        Long id = mNoteRepository.create(note1);
        Note note2 = new Note();
        mNoteRepository.create(note2);

    /* When */
        Note result = mNoteRepository.find(id);

    /* Then */
        assertThat(result, is(note1));
    }

    @Test
    public void creatingANote_setsItsCreatedProperty() {
    /* Given */
        Note note = new Note();

    /* When */
        mNoteRepository.create(note);

    /* Then */
        assertThat(note.getCreated(), is(greaterThan(System.currentTimeMillis() - 5000)));
    }

    @Test
    public void findAllWithoutNotes_returnsEmptyCollection() {
    /* When */
        Collection<Note> notes = mNoteRepository.findAll();

    /* Then */
        assertThat(notes, is(empty()));
    }

    @Test
    public void findAllWithASingleNote_returnsThatNote() {
    /* Given */
        Note note = new Note();
        mNoteRepository.create(note);

    /* When */
        Collection<Note> notes = mNoteRepository.findAll();

    /* Then */
        assertThat(notes, contains(note));
    }

    @Test
    public void findAllWithAMultipleNotes_returnsThoseNotes() {
    /* Given */
        Note note1 = new Note();
        mNoteRepository.create(note1);

        Note note2 = new Note();
        mNoteRepository.create(note2);

    /* When */
        Collection<Note> notes = mNoteRepository.findAll();

    /* Then */
        assertThat(notes, contains(note1, note2));
    }

    @Test
    public void updateExistingNote_succeeds() {
    /* Given */
        Note note = new Note();
        mNoteRepository.create(note);

    /* When */
        boolean result = mNoteRepository.update(note);

    /* Then */
        assertThat(result, is(true));
    }

    @Test
    public void updateNonExistingNote_fails() {
    /* Given */
        Note note = new Note();

    /* When */
        boolean result = mNoteRepository.update(note);

    /* Then */
        assertThat(result, is(false));
    }
}
