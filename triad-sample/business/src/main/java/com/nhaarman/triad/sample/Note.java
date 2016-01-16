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

public class Note implements Comparable<Note> {

    private String mTitle;

    private String mContents;

    private long mCreated;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String title) {
        mTitle = title;
    }

    public String getContents() {
        return mContents;
    }

    public void setContents(final String contents) {
        mContents = contents;
    }

    public long getCreated() {
        return mCreated;
    }

    public void setCreated(final long created) {
        mCreated = created;
    }

    @Override
    public int compareTo(@NonNull final Note another) {
        return Long.valueOf(mCreated).compareTo(another.getCreated());
    }
}
