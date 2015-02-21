package com.nhaarman.triad.tests.utils;

import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;

public class ViewWaiter {

  public static void waitUntil(@NotNull final Condition condition) throws InterruptedException {
    waitUntil(condition, 10000);
  }

  public static void waitUntil(@NotNull final Condition condition, final long timeoutMs) throws InterruptedException {
    long start = System.currentTimeMillis();
    do {
      Thread.sleep(25);
    }
    while (!condition.condition() && System.currentTimeMillis() - start < timeoutMs);

    if (System.currentTimeMillis() - start > timeoutMs) {
      throw new AssertionError(condition.failureDescription(timeoutMs));
    }
  }

  public static Condition viewVisible(@NotNull final ViewGroup parent, final int viewResourceId) {
    return new ViewVisibleCondition(parent, viewResourceId);
  }

  public static Condition viewNotPresent(@NotNull final ViewGroup parent, final int viewResourceId) {
    return new ViewNotPresentCondition(parent, viewResourceId);
  }

  public static Condition viewHasAlpha(@NotNull final View view, final float alpha) {
    return new ViewHasAlphaCondition(view, alpha);
  }

  public interface Condition {
    boolean condition();

    String failureDescription(long timeoutMs);
  }

  public static class ViewHasAlphaCondition implements Condition {
    @NotNull
    private final View mView;
    private final float mAlpha;

    public ViewHasAlphaCondition(@NotNull final View view, final float alpha) {
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

    @NotNull
    private final ViewGroup mParent;
    private final int mViewResourceId;

    public ViewVisibleCondition(@NotNull final ViewGroup parent, final int viewResourceId) {
      mParent = parent;
      mViewResourceId = viewResourceId;
    }

    @Override
    public boolean condition() {
      View childView = mParent.findViewById(mViewResourceId);

      return childView != null
          && childView.getVisibility() == View.VISIBLE
          && childView.getAlpha() > .99f;
    }

    @Override
    public String failureDescription(final long timeoutMs) {
      return "Expected View with id " + mViewResourceId + " to be visible within " + timeoutMs + "ms, but it wasn't.";
    }
  }

  public static class ViewNotPresentCondition implements Condition {

    @NotNull
    private final ViewGroup mParent;
    private final int mViewResourceId;

    public ViewNotPresentCondition(@NotNull final ViewGroup parent, final int viewResourceId) {
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
