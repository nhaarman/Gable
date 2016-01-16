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

package com.nhaarman.triad.sample.notes;

import android.support.annotation.NonNull;
import com.nhaarman.triad.Presenter;
import com.nhaarman.triad.Screen;
import com.nhaarman.triad.sample.ApplicationComponent;
import com.nhaarman.triad.sample.R;
import com.nhaarman.triad.sample.notes.noteslist.NotesListPresenter;

public class NotesScreen extends Screen<ApplicationComponent> {

    @Override
    protected int getLayoutResId() {
        return R.layout.view_notes;
    }

    @NonNull
    @Override
    protected Presenter<?, ?> createPresenter(final int viewId) {
        if (viewId == R.id.view_notes) {
            return new NotesPresenter(
                  applicationComponent.triad()
            );
        }

        if (viewId == R.id.view_notes_noteslistview) {
            return new NotesListPresenter(
                  applicationComponent.noteRepository(),
                  (NotesListPresenter.OnNoteClickedListener) getPresenter(R.id.view_notes)
            );
        }

        throw new AssertionError("Unknown presenter class for view with id: " + viewId);
    }
}
