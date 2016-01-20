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
import com.nhaarman.triad.Triad;

public class ApplicationComponent {

    @NonNull
    private final Triad mTriad;

    @NonNull
    private final NoteRepository mNoteRepository;

    @NonNull
    private final NoteCreator mNoteCreator;

    @NonNull
    private final NoteValidator mNoteValidator;

    public ApplicationComponent(@NonNull final Triad triad) {
        mTriad = triad;
        mNoteRepository = new MemoryNoteRepository();
        mNoteCreator = new NoteCreator(mNoteRepository);
        mNoteValidator = new NoteValidator();
    }

    @NonNull
    public NoteRepository noteRepository() {
        return mNoteRepository;
    }

    @NonNull
    public NoteCreator noteCreator() {
        return mNoteCreator;
    }

    @NonNull
    public NoteValidator noteValidator() {
        return mNoteValidator;
    }

    @NonNull
    public Triad triad() {
        return mTriad;
    }
}
