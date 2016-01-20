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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.nhaarman.triad.BasePresenter;
import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.Note;
import com.nhaarman.triad.sample.NoteRepository;
import java.util.ArrayList;
import java.util.List;

public class NotesListPresenter extends BasePresenter<NotesListContainer, ActivityComponent> {

    @NonNull
    private final NoteRepository mNoteRepository;

    @NonNull
    private final OnNoteClickedListener mListener;

    @Nullable
    private List<NotePresenter> mNotePresenters;

    public NotesListPresenter(@NonNull final NoteRepository noteRepository,
                              @NonNull final OnNoteClickedListener listener) {
        mNoteRepository = noteRepository;
        mListener = listener;
    }

    @Override
    protected void onControlGained(@NonNull final NotesListContainer container, @NonNull final ActivityComponent activityComponent) {
        List<Note> notes = mNoteRepository.findAll();

        mNotePresenters = new ArrayList<>();
        for (Note note : notes) {
            mNotePresenters.add(new NotePresenter(note));
        }

        container.setNotes(mNotePresenters);
    }

    public void onNoteClicked(final int position) {
        if (mNotePresenters == null) {
            return;
        }

        mListener.onNoteClicked(mNotePresenters.get(position).getNote());
    }

    public interface OnNoteClickedListener {

        void onNoteClicked(@NonNull Note note);
    }
}
