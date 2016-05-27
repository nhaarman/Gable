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

import android.content.res.Resources;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

/**
 * A default base implementation of the {@link Presenter} interface.
 *
 * This class handles acquiring and releasing of the {@link Container} instance,
 * and forwards these calls to one of the following lifecycle methods:
 *
 * <li>
 * <ul>{@link #onControlGained(Container, Object)}</ul>
 * <ul>{@link #onControlLost()}</ul>
 * </li>
 *
 * Control over the {@code Container} instance starts at {@link #onControlGained(Container, Object)},
 * and ends at {@link #onControlLost()}.
 *
 * An {@link Optional} reference to the {@code Container} instance can be obtained using {@link #container()},
 * which returns an empty reference when the {@code Presenter} does not have control over the instance.
 *
 * @param <ActivityComponent> The activity component.
 * @param <C>                 The specialized type of the {@link Container}.
 */
public class BasePresenter<C extends Container, ActivityComponent> implements Presenter<C, ActivityComponent> {

    /**
     * The {@link Container} this {@link BasePresenter} controls.
     */
    @NonNull
    private Optional<C> mCurrentContainer = Optional.empty();

    @NonNull
    private Optional<Resources> mResources = Optional.empty();

    @Nullable
    private ActivityComponent mActivityComponent;

    /**
     * Sets the {@link C} this {@code BasePresenter} controls, and calls {@link #onControlGained(Container, Object)} )}
     * to notify implementers of this class that the {@link C} is available.
     *
     * @param container The {@link C} to gain control over.
     */
    @Override
    @MainThread
    public final void acquire(@NonNull final C container, @NonNull final ActivityComponent activityComponent) {
        if (mCurrentContainer.isPresent() && container.equals(mCurrentContainer.get())) {
            return;
        }

        if (mCurrentContainer.isPresent()) {
            onControlLost();
        }

        mCurrentContainer = Optional.of(container);
        mResources = resources(container);
        mActivityComponent = activityComponent;
        onControlGained(container, activityComponent);
    }

    /**
     * Work around the {@link #acquire(Container, Object)} method to easily set the {@link Container}
     * instance when running Unit tests.
     *
     * <b>Do not call this in production code!</b>
     */
    @VisibleForTesting
    public final void setContainer(@NonNull final C container) {
        mCurrentContainer = Optional.of(container);
        mResources = resources(container);
    }

    /* Perform null checks to avoid unit tests from failing. */
    private Optional<Resources> resources(@NonNull final C container) {
        //noinspection ConstantConditions
        if (container.context() != null && container.context().getResources() != null) {
            return Optional.of(container.context().getResources());
        }

        return Optional.empty();
    }

    /**
     * Releases the {@link C} this {@code BasePresenter} controls, and calls {@link #onControlLost()}
     * to notify implementers of this class that the {@link C} is no longer available.
     */
    @Override
    @MainThread
    public final void releaseContainer(@NonNull final C container) {
        if (!mCurrentContainer.isPresent() || !mCurrentContainer.get().equals(container)) {
            return;
        }

        mCurrentContainer = Optional.empty();
        mResources = Optional.empty();
        mActivityComponent = null;
        onControlLost();
    }

    /**
     * Called when the {@link Container} for this {@code BasePresenter} is attached to the window and ready to display the state.
     *
     * From this point on, {@link #container()} will return the {@link Container} instance, until {@link #onControlLost()} is called.
     *
     * @param container The {@link Container} to gain control over.
     */
    @MainThread
    protected void onControlGained(@NonNull final C container,
                                   @NonNull final ActivityComponent activityComponent) {
    }

    /**
     * Called when this {@code BasePresenter} no longer controls the {@link Container} instance.
     *
     * From this point on, {@link #container()} will return an empty {@link Optional}.
     */
    @MainThread
    protected void onControlLost() {
    }

    /**
     * Returns an {@link Optional} of the {@link Container} instance this {@code Presenter} controls.
     *
     * Upon retrieving this instance, always perform a check to {@link Optional#isPresent()} to ensure
     * that the {@code Container} is present.
     *
     * @return An {@code Optional} of the {@code Container}, guaranteed to be present between the
     * {@link #onControlGained(Container, Object)} and {@link #onControlLost()} calls.
     */
    @NonNull
    public Optional<C> container() {
        return mCurrentContainer;
    }

    /**
     * Returns an {@link Optional} of the {@code ActivityComponent} instance that is acquired.
     *
     * Upon retrieving this instance, always perform a check to {@link Optional#isPresent()} to ensure
     * that the {@code ActivityComponent} is present.
     *
     * @return An {@code Optional} of the {@code ActivityComponent}, guaranteed to be present between the
     * {@link #onControlGained(Container, Object)} and {@link #onControlLost()} calls.
     */
    @NonNull
    public Optional<ActivityComponent> activityComponent() {
        return Optional.of(mActivityComponent);
    }

    /**
     * Returns an {@link Optional} of the {@link Resources} instance retrieved from the {@link Container} instance.
     *
     * Upon retrieving this instance, always perform a check to {@link Optional#isPresent()} to ensure
     * that the {@code Resources} is present.
     *
     * @return An {@code Optional} of the {@code Resources}, guaranteed to be present between the
     * {@link #onControlGained(Container, Object)} and {@link #onControlLost()} calls.
     */
    @NonNull
    public Optional<Resources> resources() {
        return mResources;
    }
}
