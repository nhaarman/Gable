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

package com.nhaarman.triad.utils;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public class ViewWaiter {

    public static void waitUntil(@NonNull final Condition condition) throws InterruptedException {
        waitUntil(condition, 1000);
    }

    public static void waitUntil(@NonNull final Condition condition, final long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        do {
            Thread.sleep(25);
        }
        while (!condition.condition() && System.currentTimeMillis() - start < timeoutMs);

        if (System.currentTimeMillis() - start > timeoutMs) {
            throw new AssertionError(condition.failureDescription(timeoutMs));
        }
    }

    public static Condition viewVisible(@NonNull final ViewGroup parent, final int viewResourceId) {
        return new ViewVisibleCondition(parent, viewResourceId);
    }

    public static Condition viewNotPresent(@NonNull final ViewGroup parent, final int viewResourceId) {
        return new ViewNotPresentCondition(parent, viewResourceId);
    }

    public static Condition viewHasAlpha(@NonNull final View view, final float alpha) {
        return new ViewHasAlphaCondition(view, alpha);
    }

    public interface Condition {

        boolean condition();

        String failureDescription(long timeoutMs);
    }

    public static class ViewHasAlphaCondition implements Condition {

        @NonNull
        private final View mView;

        private final float mAlpha;

        public ViewHasAlphaCondition(@NonNull final View view, final float alpha) {
            mView = view;
            mAlpha = alpha;
        }

        @Override
        public boolean condition() {
            return mView.getAlpha() >= mAlpha - .0001f && mView.getAlpha() <= mAlpha + .0001f;
        }

        @Override
        public String failureDescription(final long timeoutMs) {
            return "Expected View " + mView + " to have alpha " + mAlpha + " within " + timeoutMs + "ms, but it didn't.";
        }
    }

    public static class ViewVisibleCondition implements Condition {

        @NonNull
        private final ViewGroup mParent;

        private final int mViewResourceId;

        public ViewVisibleCondition(@NonNull final ViewGroup parent, final int viewResourceId) {
            mParent = parent;
            mViewResourceId = viewResourceId;
        }

        @Override
        public boolean condition() {
            View childView = mParent.findViewById(mViewResourceId);

            return childView != null
                  && childView.getVisibility() == View.VISIBLE
                  && childView.getAlpha() > .999f;
        }

        @Override
        public String failureDescription(final long timeoutMs) {
            return "Expected View with id " + mViewResourceId + " to be visible within " + timeoutMs + "ms, but it wasn't.";
        }
    }

    public static class ViewNotPresentCondition implements Condition {

        @NonNull
        private final ViewGroup mParent;

        private final int mViewResourceId;

        public ViewNotPresentCondition(@NonNull final ViewGroup parent, final int viewResourceId) {
            mParent = parent;
            mViewResourceId = viewResourceId;
        }

        @Override
        public boolean condition() {
            return mParent.findViewById(mViewResourceId) == null;
        }

        @Override
        public String failureDescription(final long timeoutMs) {
            return "Expected View with id " + mViewResourceId + " not to be present within " + timeoutMs + "ms, but it wasn't.";
        }
    }
}
