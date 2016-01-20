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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.nhaarman.triad.ListViewContainer
import com.nhaarman.triad.sample.ActivityComponent
import com.nhaarman.triad.sample.R

class NotesListView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) :
      ListViewContainer<NotesListPresenter, ActivityComponent>(context, attrs, defStyle), NotesListContainer {

    private val adapter = MyAdapter()
    private var notes: List<NotePresenter>? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        setAdapter(adapter)
        setOnItemClickListener { adapterView, view, position, id -> presenter.onNoteClicked(position) }
    }

    override fun setNotes(notes: List<NotePresenter>) {
        this.notes = notes
        adapter.notifyDataSetChanged()
    }

    private inner class MyAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return notes?.size ?: 0
        }

        override fun getItem(position: Int): NotePresenter {
            return notes?.get(position) ?: throw AssertionError()
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return ((convertView ?: createView(parent)) as NoteContainer).apply {
                presenter = getItem(position)
            } as View
        }

        private fun createView(parent: ViewGroup) = LayoutInflater.from(context).inflate(R.layout.view_note, parent, false)
    }
}

