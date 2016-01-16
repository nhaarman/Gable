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

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import com.nhaarman.triad.AdapterRelativeLayoutContainer;
import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.R;

public class NoteView extends AdapterRelativeLayoutContainer<NotePresenter, ActivityComponent> implements NoteContainer {

    @Bind(R.id.view_note_titletv)
    protected TextView mTitleTV;

    @Bind(R.id.view_note_contentstv)
    protected TextView mContentsTV;

    public NoteView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTitle(@NonNull final String title) {
        mTitleTV.setText(title);
    }

    @Override
    public void setContents(@NonNull final String contents) {
        mContentsTV.setText(contents);
    }
}
