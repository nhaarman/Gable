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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryNoteRepository implements NoteRepository {

    @NonNull
    private final Map<Long, Note> mNotes;

    private long mLastId;

    public MemoryNoteRepository() {
        mNotes = new HashMap<>(16);
        mLastId = 0;
    }

    @Override
    public Long create(@NonNull final Note note) {
        mLastId++;
        note.setCreated(System.currentTimeMillis());
        mNotes.put(mLastId, note);
        return mLastId;
    }

    @Nullable
    @Override
    public Note find(@Nullable final Long id) {
        if (id == null) {
            return null;
        }

        return mNotes.get(id);
    }

    @NonNull
    @Override
    public List<Note> findAll() {
        return new ArrayList<>(mNotes.values());
    }

    @Override
    public boolean update(@NonNull final Note note) {
        return mNotes.values().contains(note);
    }
}
