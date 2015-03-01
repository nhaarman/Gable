package com.nhaarman.triad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import com.nhaarman.triad.container.RelativeLayoutContainer;
import com.nhaarman.triad.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link View}, {@link TriadContainer}, which hosts all {@link View}s belonging to {@link Screen}s in the application.
 *
 * @param <M> The main module in the application. See {@link TriadPresenter}.
 */
@SuppressWarnings("AnonymousInnerClass")
public class TriadView<M> extends RelativeLayoutContainer<TriadPresenter<M>, TriadContainer<M>> implements TriadContainer<M> {

  private static final float DIMMED_ALPHA_VALUE = .5f;

  private final long mTransitionAnimationDurationMs;

  @NotNull
  private ViewGroup mScreenHolder;

  @NotNull
  private View mDimmerView;

  @NotNull
  private ViewGroup mDialogHolder;

  public TriadView(final Context context, final AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public TriadView(final Context context, final AttributeSet attrs, final int defStyle) {
    super(context, attrs, defStyle);

    mTransitionAnimationDurationMs = getResources().getInteger(android.R.integer.config_shortAnimTime);
  }

  public TriadView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    mTransitionAnimationDurationMs = getResources().getInteger(android.R.integer.config_shortAnimTime);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();

    mScreenHolder = (ViewGroup) findViewById(R.id.view_triad_screenholder);
    mDialogHolder = (ViewGroup) findViewById(R.id.view_triad_dialogholder);

    mDimmerView = findViewById(R.id.view_triad_dimmerview);
    mDimmerView.setOnClickListener(new DimmerViewOnCLickListener());
    mDimmerView.setClickable(false);
  }

  @Override
  public void transition(@Nullable final View oldView, @Nullable final View newView) {
    if (newView != null) {
      mScreenHolder.addView(newView);
      newView.getViewTreeObserver().addOnPreDrawListener(new TransitionPreDrawListener(oldView, newView));
    } else if (oldView != null) {
      animateViewExit(oldView);
    }
  }

  @Override
  public void showDialog(@NotNull final View dialogView) {
    mDimmerView
        .animate()
        .alpha(DIMMED_ALPHA_VALUE)
        .setDuration(mTransitionAnimationDurationMs)
        .withEndAction(new Runnable() {
          @Override
          public void run() {
            mDimmerView.setClickable(true);
          }
        });

    mDialogHolder.addView(dialogView);
    dialogView.getViewTreeObserver().addOnPreDrawListener(new DialogPreDrawListener(dialogView));
  }

  @Override
  public void dismissDialog(@NotNull final View dialogView) {
    dialogView.animate()
        .alpha(0f)
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(mTransitionAnimationDurationMs)
        .withEndAction(new Runnable() {
          @Override
          public void run() {
            mDialogHolder.removeView(dialogView);
            if (mDialogHolder.getChildCount() == 0) {
              mDimmerView
                  .animate()
                  .alpha(0f);
              mDimmerView.setClickable(false);
            }
          }
        });
  }

  /**
   * Executes an exit animation for given {@link View},
   * after which the {@link View} will be removed from its parent.
   *
   * @param view The {@link View} to animate and remove.
   */
  private void animateViewExit(@NotNull final View view) {
    view.animate()
        .alpha(0)
        .setDuration(mTransitionAnimationDurationMs)
        .withEndAction(new Runnable() {
          @Override
          public void run() {
            ((ViewManager) view.getParent()).removeView(view);
          }
        });
  }

  /**
   * Observes when the new {@link View} is about to be drawn, so a replacement animation can be executed.
   */
  private class TransitionPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    @Nullable
    private final View mOldView;

    @NotNull
    private final View mNewView;

    /**
     * @param oldView The {@link View} to execute an exit animation for.
     * @param newView The {@link View} to execute an entering animation for.
     */
    TransitionPreDrawListener(@Nullable final View oldView, @NotNull final View newView) {
      mOldView = oldView;
      mNewView = newView;
    }

    @Override
    public boolean onPreDraw() {
      mNewView.getViewTreeObserver().removeOnPreDrawListener(this);
      animateViewReplacing();
      return true;
    }

    /**
     * Executes an exit animation for the old {@link View}, and an entering animation for the new {@link View}.
     * The old {@link View} will be removed from its parent after the animation.
     */
    private void animateViewReplacing() {
      if (mOldView != null) {
        mOldView.animate()
            .alpha(0)
            .setDuration(mTransitionAnimationDurationMs);
      }

      mNewView.setAlpha(0);
      mNewView.animate()
          .alpha(1f)
          .setDuration(mTransitionAnimationDurationMs)
          .withEndAction(new Runnable() {
            @Override
            public void run() {
              if (mOldView != null) {
                ((ViewManager) mOldView.getParent()).removeView(mOldView);
              }
            }
          });
    }
  }

  private class DimmerViewOnCLickListener implements OnClickListener {

    @Override
    public void onClick(final View v) {
      getPresenter().onDimmerClicked();
    }
  }

  private class DialogPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    @NotNull
    private final View mDialogView;

    DialogPreDrawListener(@NotNull final View dialogView) {
      mDialogView = dialogView;
    }

    @Override
    public boolean onPreDraw() {
      mDialogView.getViewTreeObserver().removeOnPreDrawListener(this);

      mDialogView.setScaleX(0);
      mDialogView.setScaleY(0);
      mDialogView.setAlpha(0f);
      mDialogView.animate()
          .alpha(1f)
          .scaleX(1f)
          .scaleY(1f)
          .setDuration(mTransitionAnimationDurationMs);

      return true;
    }
  }
}
