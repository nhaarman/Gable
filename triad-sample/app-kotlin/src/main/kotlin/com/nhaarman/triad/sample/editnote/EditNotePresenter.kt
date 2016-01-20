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

import com.nhaarman.triad.BasePresenter
import com.nhaarman.triad.Triad
import com.nhaarman.triad.sample.*

open class EditNotePresenter(private val note: Note?,
                        private val noteValidator: NoteValidator,
                        private val noteCreator: NoteCreator,
                        private val noteRepository: NoteRepository,
                        private val triad: Triad) : BasePresenter<EditNoteContainer, ActivityComponent>() {

    override public fun onControlGained(container: EditNoteContainer, activityComponent: ActivityComponent) {
        var title = ""
        var contents = ""

        if (note != null) {
            title = note.title
            contents = note.contents
        }

        container.title = title
        container.contents = contents
    }

    fun onSaveNoteClicked() {
        container?.apply {

            if (!noteValidator.validateTitle(title)) {
                setTitleError(resources?.getString(R.string.error_title))
                return
            }

            if (!noteValidator.validateContents(contents)) {
                setContentsError(resources?.getString(R.string.error_contents))
                return
            }

            if (note == null) {
                noteCreator.createNote(title, contents)
            } else {
                noteRepository.update(note)
            }

            triad.goBack()
        }
    }
}
