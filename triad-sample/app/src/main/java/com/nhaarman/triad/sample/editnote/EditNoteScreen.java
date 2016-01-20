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
import com.nhaarman.triad.Presenter;
import com.nhaarman.triad.Screen;
import com.nhaarman.triad.sample.ApplicationComponent;
import com.nhaarman.triad.sample.Note;
import com.nhaarman.triad.sample.R;

public class EditNoteScreen extends Screen<ApplicationComponent> {

    @Nullable
    private final Note mNote;

    public EditNoteScreen() {
        this(null);
    }

    public EditNoteScreen(@Nullable final Note note) {
        mNote = note;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_editnote;
    }

    @NonNull
    @Override
    protected Presenter<?, ?> createPresenter(final int viewId) {
        return new EditNotePresenter(
              mNote,
              applicationComponent.noteValidator(),
              applicationComponent.noteCreator(),
              applicationComponent.noteRepository(),
              applicationComponent.triad()
        );
    }
}
