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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import butterknife.OnItemClick;
import com.nhaarman.triad.ListViewContainer;
import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.R;
import java.util.List;

public class NotesListView extends ListViewContainer<NotesListPresenter, ActivityComponent> implements NotesListContainer {

    @NonNull
    private final MyAdapter mAdapter;

    @Nullable
    private List<NotePresenter> mNotes;

    public NotesListView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotesListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        mAdapter = new MyAdapter();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            setAdapter(mAdapter);
        }
    }

    @Override
    public void setNotes(@NonNull final List<NotePresenter> notes) {
        mNotes = notes;
        mAdapter.notifyDataSetChanged();
    }

    @OnItemClick
    public void onItemClicked(final int position) {
        getPresenter().onNoteClicked(position);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNotes == null ? 0 : mNotes.size();
        }

        @Override
        public NotePresenter getItem(final int position) {
            return mNotes == null ? null : mNotes.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            NoteContainer noteContainer = (NoteContainer) convertView;
            if (noteContainer == null) {
                noteContainer = (NoteContainer) LayoutInflater.from(getContext()).inflate(R.layout.view_note, parent, false);
            }

            noteContainer.setPresenter(getItem(position));

            return (View) noteContainer;
        }
    }
}

