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

import android.content.Context;
import android.util.AttributeSet;
import butterknife.Bind;
import butterknife.OnClick;
import com.nhaarman.triad.RelativeLayoutContainer;
import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.R;
import com.nhaarman.triad.sample.notes.noteslist.NotesListView;

public class NotesView extends RelativeLayoutContainer<NotesPresenter, ActivityComponent> implements NotesContainer {

    @Bind(R.id.view_notes_noteslistview)
    protected NotesListView mNotesListView;

    public NotesView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public NotesView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @OnClick(R.id.view_notes_createnotebutton)
    public void onCreateNoteButtonClicked() {
        getPresenter().onCreateNoteClicked();
    }
}
