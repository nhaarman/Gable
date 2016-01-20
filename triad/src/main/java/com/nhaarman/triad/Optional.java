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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.nhaarman.triad.Preconditions.checkNotNull;

/**
 * A class that wraps a nullable type.
 */
public class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();

    @Nullable
    private final T mValue;

    private Optional() {
        mValue = null;
    }

    @SuppressWarnings("NullableProblems")
    private Optional(@NonNull final T value) {
        mValue = checkNotNull(value, "value is null.");
    }

    /**
     * Invokes the {@link Consumer}'s {@link Consumer#accept(Object)} method if and only if the value is present.
     */
    public void ifPresent(@NonNull final Consumer<? super T> consumer) {
        if (mValue != null) {
            consumer.accept(mValue);
        }
    }

    /**
     * Returns {@code true} if and only if the value can safely be retrieved using {@link #get()}.
     */
    public boolean isPresent() {
        return mValue != null;
    }

    /**
     * Returns this Optional's value.
     * Make sure the value is present using {@link #isPresent()} before calling this method.
     *
     * @throws NullPointerException if the value is not present.
     */
    public T get() {
        return checkNotNull(mValue, "value not present.");
    }

    /**
     * Creates a new Optional of a nullable value.
     */
    public static <T> Optional<T> of(@Nullable final T value) {
        return value == null ? (Optional<T>) EMPTY : new Optional<>(value);
    }

    /**
     * Returns an empty Optional.
     */
    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }

    /**
     * An interface which can be invoked with {@link #ifPresent(Consumer)}.
     */
    public interface Consumer<T> {

        void accept(@NonNull T t);
    }
}
