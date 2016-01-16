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

package com.nhaarman.triad.sample.editnote;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.nhaarman.triad.RelativeLayoutContainer;
import com.nhaarman.triad.sample.ActivityComponent;
import com.nhaarman.triad.sample.R;

public class EditNoteView extends RelativeLayoutContainer<EditNotePresenter, ActivityComponent> implements EditNoteContainer {

    @Bind(R.id.view_editnote_titleet)
    protected EditText mTitleET;

    @Bind(R.id.view_editnote_contentset)
    protected EditText mContentsET;

    public EditNoteView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EditNoteView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @OnClick(R.id.view_editnote_savebutton)
    public void onSaveButtonClicked() {
        getPresenter().onSaveNoteClicked();
    }

    @NonNull
    @Override
    public String getTitle() {
        return mTitleET.getText().toString();
    }

    @Override
    public void setTitle(@NonNull final String title) {
        mTitleET.setText(title);
    }

    @NonNull
    @Override
    public String getContents() {
        return mContentsET.getText().toString();
    }

    @Override
    public void setContents(@NonNull final String contents) {
        mContentsET.setText(contents);
    }

    @Override
    public void setTitleError(@Nullable final String message) {
        mTitleET.setHint(message);
        mTitleET.setHintTextColor(Color.RED);
    }

    @Override
    public void setContentsError(@Nullable final String message) {
        mContentsET.setHint(message);
        mContentsET.setHintTextColor(Color.RED);
    }
}
