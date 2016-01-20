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

package com.nhaarman.triad.sample.notes.noteslist

import android.content.Context
import android.util.AttributeSet
import com.nhaarman.triad.AdapterRelativeLayoutContainer
import com.nhaarman.triad.sample.ActivityComponent
import kotlinx.android.synthetic.main.view_note.view.*

class NoteView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
      AdapterRelativeLayoutContainer<NotePresenter, ActivityComponent>(context, attrs, defStyle), NoteContainer {

    override fun setTitle(title: String) {
        titleTV.text = title
    }

    override fun setContents(contents: String) {
        contentsTV.text = contents
    }
}
