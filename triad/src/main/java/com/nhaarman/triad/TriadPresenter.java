package com.nhaarman.triad;

import android.view.View;
import android.view.ViewGroup;
import com.nhaarman.triad.container.Container;
import com.nhaarman.triad.container.ScreenContainer;
import com.nhaarman.triad.presenter.Presenter;
import com.nhaarman.triad.presenter.ScreenPresenter;
import com.nhaarman.triad.screen.Screen;
import flow.Flow;
import java.util.Stack;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link Presenter} which handles the showing of the {@link Screen}s in the application.
 * For each {@link Screen} that is to be shown, it sets up the {@link ScreenContainer} and {@link ScreenPresenter}
 * for that {@link Screen}, and performs the proper {@link View} adding and removal.
 *
 * @param <M> The {@code main component} to use for {@link Presenter} creation. See {@link TriadActivity}.
 */
class TriadPresenter<M> extends Presenter<TriadPresenter<M>, TriadContainer<M>> {

  @NotNull
  private final M mMainComponent;

  @NotNull
  private final Flow mFlow;

  @NotNull
  private final Stack<Screen<?, ?, M>> mScreens;

  @NotNull
  private final Stack<ScreenContainer<?, ?>> mScreenContainers;

  //@SuppressWarnings("rawtypes")
  //@Nullable
  //private Screen<? extends ScreenPresenter, ? extends ScreenContainer, M> mCurrentScreen;

  //@Nullable
  //private View mCurrentView;

  /**
   * Creates a new {@code TriadPresenter}.
   *
   * @param mainComponent The main component to use.
   * @param flow The {@link Flow} instance to use.
   */
  TriadPresenter(@NotNull final M mainComponent, @NotNull final Flow flow) {
    mMainComponent = mainComponent;
    mFlow = flow;

    mScreens = new Stack<>();
    mScreenContainers = new Stack<>();
  }

  @Override
  protected void onControlGained(@NotNull final TriadContainer<M> container) {
    Screen<? extends ScreenPresenter, ? extends ScreenContainer, M>[] screens = new Screen[mScreens.size()];
    mScreens.copyInto(screens);
    mScreens.clear();

    for (int i = 0; i < screens.length; i++) {
      showScreen(screens[i]);
    }
  }

  /**
   * Performs the proper transitions to show given {@link Screen}.
   *
   * @param screen The {@link Screen} to show.
   */
  public <P extends ScreenPresenter<P, C>, C extends ScreenContainer<P, C>> void showScreen(@NotNull final Screen<P, C, M> screen, final Flow.Direction direction) {
    if (direction == Flow.Direction.BACKWARD && mScreens.peek().isDialog()) {
      popDialog();
    } else {
      showScreen(screen);
    }
  }

  private <P extends ScreenPresenter<P, C>, C extends ScreenContainer<P, C>> void showScreen(final Screen<P, C, M> screen) {
    if (getContainer() == null) {
      mScreens.push(screen);
      return;
    }

    C container = screen.createView((ViewGroup) getContainer());
    P presenter = screen.getPresenter(mMainComponent);
    container.setPresenter(presenter);

    boolean isDialog = screen.isDialog();
    if (isDialog) {
      getContainer().showDialog((View) container);
    } else {
      ScreenContainer<?, ?> previousContainer = null;
      if (!mScreens.empty()) {
        mScreens.pop();
        previousContainer = mScreenContainers.pop();
      }
      getContainer().transition((View) previousContainer, (View) container);
    }

    mScreens.push(screen);
    mScreenContainers.push(container);
  }

  private void popDialog() {
    if (getContainer() == null) {
      return;
    }

    mScreens.pop();
    ScreenContainer<?, ?> container = mScreenContainers.pop();
    getContainer().dismissDialog((View) container);
  }

  /**
   * Called when the back button has been pressed.
   *
   * @return true if the event has been handled, false otherwise.
   */
  public boolean onBackPressed() {
    if (mScreens.empty()) {
      return false;
    }

    Screen<?, ?, M> screen = mScreens.peek();
    return screen.getPresenter(mMainComponent).onBackPressed() || mFlow.goBack();
  }

  public void onDimmerClicked() {
    onBackPressed();
  }

  public <P extends ScreenPresenter<P, C>, C extends ScreenContainer<P, C>> void onStart() {
    if (mScreens.empty()) {
      return;
    }

    Screen<P, C, M> screen = (Screen<P, C, M>) mScreens.peek();
    P presenter = screen.getPresenter(mMainComponent);
    C container = (C) mScreenContainers.peek();
    presenter.acquire(container);
  }

  public void onStop() {
    if (mScreens.empty()) {
      return;
    }

    Screen<?, ?, M> screen = mScreens.peek();
    ScreenPresenter<?, ?> presenter = screen.getPresenter(mMainComponent);
    presenter.releaseContainer();
  }
}

