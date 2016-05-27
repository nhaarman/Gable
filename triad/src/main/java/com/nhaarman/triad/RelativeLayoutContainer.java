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

package com.nhaarman.triad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;

import static com.nhaarman.triad.TriadUtil.findActivityComponent;
import static com.nhaarman.triad.TriadUtil.findPresenter;

/**
 * An abstract {@link Container} instance that handles {@link Presenter} management,
 * and uses Butter Knife to bind UI components in implementing classes.
 *
 * @param <P> The specialized {@link Presenter} type.
 */
public abstract class RelativeLayoutContainer
      <P extends Presenter<?, ActivityComponent>, ActivityComponent>
      extends RelativeLayout implements Container {

    @NonNull
    private final ActivityComponent mActivityComponent;

    /* Use a raw type in favor of an easier API. */
    @SuppressWarnings("rawtypes")
    @Nullable
    private Presenter mPresenter;

    public RelativeLayoutContainer(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RelativeLayoutContainer(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mActivityComponent = findActivityComponent(context);
    }

    /**
     * Returns the {@link P} instance that is tied to this {@code RelativeLayoutContainer}.
     */
    @NonNull
    public P getPresenter() {
        if (mPresenter == null) {
            mPresenter = findPresenter(getContext(), this);
        }

        return (P) mPresenter;
    }

    @NonNull
    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isInEditMode()) {
            return;
        }

        //noinspection rawtypes
        ((Presenter) getPresenter()).acquire(this, mActivityComponent);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //noinspection rawtypes
        ((Presenter) getPresenter()).releaseContainer(this);
    }

    @NonNull
    @Override
    public Context context() {
        return getContext();
    }
}
