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

import static com.nhaarman.triad.Preconditions.checkState;
import static com.nhaarman.triad.TriadUtil.findActivityComponent;

/**
 * An abstract RelativeLayout {@link Container} instance that handles {@link Presenter} management
 * for use in an adapter View, and uses Butter Knife to bind UI components in implementing classes.
 *
 * @param <P> The specialized {@link Presenter} type.
 */
public abstract class AdapterRelativeLayoutContainer
      <P extends Presenter<?, ActivityComponent>, ActivityComponent>
      extends RelativeLayout implements AdapterContainer<P> {

    @NonNull
    private final ActivityComponent mActivityComponent;

    /* Use a raw type in favor of an easier API. */
    @SuppressWarnings("rawtypes")
    @Nullable
    private Presenter mPresenter;

    private boolean mIsAttachedToWindow;

    public AdapterRelativeLayoutContainer(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdapterRelativeLayoutContainer(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        mActivityComponent = findActivityComponent(context);
    }

    /**
     * Returns the {@link P} instance that is tied to this {@code RelativeLayoutContainer}.
     */
    @NonNull
    public P getPresenter() {
        checkState(mPresenter != null, "Presenter is null");

        return (P) mPresenter;
    }

    @Override
    public void setPresenter(@NonNull final P presenter) {
        if (mPresenter != null) {
            mPresenter.releaseContainer();
        }

        mPresenter = presenter;

        if (mIsAttachedToWindow) {
            mPresenter.acquire(this, mActivityComponent);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mPresenter != null) {
            mPresenter.acquire(this, mActivityComponent);
        }

        mIsAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mPresenter != null) {
            mPresenter.releaseContainer();
        }

        mIsAttachedToWindow = false;
    }

    @NonNull
    @Override
    public Context context() {
        return getContext();
    }
}
