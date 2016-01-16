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

package com.nhaarman.triad.sample.editnote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.nhaarman.triad.BasePresenter;
import com.nhaarman.triad.Triad;
import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.Note;
import com.nhaarman.triad.sample.NoteCreator;
import com.nhaarman.triad.sample.NoteRepository;
import com.nhaarman.triad.sample.NoteValidator;
import com.nhaarman.triad.sample.R;

public class EditNotePresenter extends BasePresenter<EditNoteContainer, ActivityComponent> {

    @Nullable
    private final Note mNote;

    @NonNull
    private final NoteValidator mNoteValidator;

    @NonNull
    private final NoteCreator mNoteCreatorMock;

    @NonNull
    private final NoteRepository mNoteRepository;

    @NonNull
    private final Triad mTriad;

    public EditNotePresenter(@Nullable final Note note,
                             @NonNull final NoteValidator noteValidator,
                             @NonNull final NoteCreator noteCreatorMock,
                             @NonNull final NoteRepository noteRepository,
                             @NonNull final Triad triad) {
        mNote = note;
        mNoteValidator = noteValidator;
        mNoteCreatorMock = noteCreatorMock;
        mNoteRepository = noteRepository;
        mTriad = triad;
    }

    @Override
    protected void onControlGained(@NonNull final EditNoteContainer container, @NonNull final ActivityComponent activityComponent) {
        String title = "";
        String contents = "";

        if (mNote != null) {
            title = mNote.getTitle();
            contents = mNote.getContents();
        }

        container.setTitle(title);
        container.setContents(contents);
    }

    public void onSaveNoteClicked() {
        if (!container().isPresent()) {
            return;
        }

        EditNoteContainer container = container().get();

        String title = container.getTitle();
        String contents = container.getContents();

        if (!mNoteValidator.validateTitle(title)) {
            container.setTitleError(resources().get().getString(R.string.error_title));
            return;
        }

        if (!mNoteValidator.validateContents(contents)) {
            container.setContentsError(resources().get().getString(R.string.error_contents));
            return;
        }

        if (mNote == null) {
            mNoteCreatorMock.createNote(title, contents);
        } else {
            mNoteRepository.update(mNote);
        }

        mTriad.goBack();
    }
}
