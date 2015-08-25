/*
 * Copyright 2015 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.triad;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;

/**
 * An abstract {@link Container} instance that handles {@link Presenter} management,
 * and uses Butter Knife to bind view fields in implementing classes.
 *
 * @param <P> The specialized {@link Presenter} type.
 * @param <C> The specialized {@link Container} type.
 */
public abstract class RelativeLayoutContainer<
    ActivityComponent,
    P extends Presenter<ActivityComponent, P, C>,
    C extends Container<ActivityComponent, P, C>
    > extends RelativeLayout implements Container<ActivityComponent, P, C> {

  /**
   * The {@link P} that is tied to this instance.
   */
  @Nullable
  private P mPresenter;

  @Nullable
  private ActivityComponent mActivityComponent;

  /**
   * Whether we're attached to the window.
   */
  private boolean mAttachedToWindow;

  public RelativeLayoutContainer(final Context context) {
    super(context);
  }

  public RelativeLayoutContainer(final Context context, final AttributeSet attrs) {
    super(context, attrs);
  }

  public RelativeLayoutContainer(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);
  }

  @TargetApi(21)
  public RelativeLayoutContainer(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  /**
   * Returns the {@link P} instance that is tied to this {@code RelativeLayoutContainer}.
   */
  @NonNull
  public P getPresenter() {
    if (mPresenter == null) {
      throw new NullPointerException("Presenter has not been set for " + getClass().getCanonicalName());
    }

    return mPresenter;
  }

  /**
   * Sets the {@link P} that controls this {@code RelativeLayoutContainer}.
   *
   * @param presenter The {@link P} instance.
   */
  @Override
  @CallSuper
  public void setPresenterAndActivityComponent(@NonNull final P presenter, @NonNull final ActivityComponent activityComponent) {
    mPresenter = presenter;
    mActivityComponent = activityComponent;

    if (mAttachedToWindow) {
      acquire();
    }
  }

  @NonNull
  public ActivityComponent getActivityComponent() {
    if (mActivityComponent == null) {
      throw new NullPointerException("ActivityComponent has not been set for " + getClass().getCanonicalName());
    }

    return mActivityComponent;
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (isInEditMode()) {
      return;
    }

    ButterKnife.bind(this);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mAttachedToWindow = true;

    if (isInEditMode()) {
      return;
    }

    acquire();
  }

  void acquire() {
    getPresenter().acquire((C) this, getActivityComponent());
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mAttachedToWindow = false;

    getPresenter().releaseContainer();
  }
}
