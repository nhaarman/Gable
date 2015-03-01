package com.nhaarman.triad.sample.notes.noteslist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.nhaarman.triad.container.RelativeLayoutContainer;
import com.nhaarman.triad.sample.Note;
import com.nhaarman.triad.sample.R;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NotesListView extends RelativeLayoutContainer<NotesListPresenter, NotesListContainer> implements NotesListContainer {

  @NotNull
  private final MyAdapter mAdapter;

  @NotNull
  @InjectView(R.id.view_notes_listview)
  protected ListView mListView;

  @Nullable
  private List<Note> mNotes;

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
      mListView.setAdapter(mAdapter);
    }
  }

  @Override
  public void setNotes(@NotNull final List<Note> notes) {
    mNotes = notes;
    mAdapter.notifyDataSetChanged();
  }

  @OnItemClick(R.id.view_notes_listview)
  public void onItemClicked(final int position) {
    getPresenter().onNoteClicked(position);
  }

  private class MyAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return mNotes == null ? 0 : mNotes.size();
    }

    @Override
    public Note getItem(final int position) {
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
        noteContainer.setPresenter(new NotePresenter());
        noteContainer.getPresenter().acquire(noteContainer);
      }

      noteContainer.getPresenter().setNote(getItem(position));

      return (View) noteContainer;
    }
  }
}

