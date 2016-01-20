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

package com.nhaarman.triad.sample.editnote

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.nhaarman.triad.RelativeLayoutContainer
import com.nhaarman.triad.sample.ActivityComponent
import kotlinx.android.synthetic.main.view_editnote.view.*

class EditNoteView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
      RelativeLayoutContainer<EditNotePresenter, ActivityComponent>(context, attrs, defStyle), EditNoteContainer {

    override fun onFinishInflate() {
        super.onFinishInflate()

        saveButton.setOnClickListener { presenter.onSaveNoteClicked() }
    }

    override var title: String
        get() = titleET.text.toString()
        set(title) = titleET.setText(title)

    override var contents: String
        get() = contentsET.text.toString()
        set(contents) = contentsET.setText(contents)

    override fun setTitleError(message: String?) {
        titleET.hint = message
        titleET.setHintTextColor(Color.RED)
    }

    override fun setContentsError(message: String?) {
        contentsET.hint = message
        contentsET.setHintTextColor(Color.RED)
    }
}
