/*
 * Copyright 2015 Niek Haarman
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

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.nhaarman.triad.Triad.Callback;
import java.util.Iterator;

import static com.nhaarman.triad.Preconditions.checkNotNull;
import static com.nhaarman.triad.Preconditions.checkState;
import static com.nhaarman.triad.TriadIntents.createBackstack;
import static com.nhaarman.triad.TriadIntents.createScreen;

/**
 * This class represents a delegate which can be used to use Triad in any
 * {@link Activity}.
 * <p>
 * When using the {@code TriadDelegate}, you must proxy the following Activity
 * lifecycle methods to it:
 * <ul>
 * <li>{@link #onCreate(Intent)}</li>
 * <li>{@link #onResume()}</li>
 * <li>{@link #onDestroy()}</li>
 * <li>{@link #onBackPressed()}</li>
 * <li>{@link #onActivityResult(int, int, Intent)}</li>
 * </ul>
 *
 * @param <ApplicationComponent> The {@code ApplicationComponent} to use for {@code BasePresenter} creation.
 */
public class TriadDelegate<ApplicationComponent> {

    /**
     * The {@link Activity} instance this {@code TriadDelegate} is bound to.
     */
    @NonNull
    private final Activity activity;

    @NonNull
    private final TransitionAnimator defaultTransitionAnimator;

    @Nullable
    private ApplicationComponent applicationComponent;

    /**
     * The {@link Triad} instance that is used to navigate between {@link Screen}s.
     */
    @Nullable
    private Triad triad;

    @Nullable
    private ViewGroup rootView;

    @Nullable
    private Screen<ApplicationComponent> currentScreen;

    /**
     * An optional {@link OnScreenChangedListener} that is notified of screen changes.
     */
    @Nullable
    private OnScreenChangedListener<ApplicationComponent> onScreenChangedListener;

    private TriadDelegate(
          @NonNull final Activity activity,
          @NonNull final TransitionAnimator transitionAnimator
    ) {
        this.activity = activity;
        defaultTransitionAnimator = transitionAnimator;
    }

    @NonNull
    public Screen<ApplicationComponent> getCurrentScreen() {
        return checkNotNull(currentScreen, "Current screen is null.");
    }

    public void onCreate(@Nullable final Intent intent) {
        checkState(activity.getApplication() instanceof TriadProvider, "Make sure your Application class implements TriadProvider.");
        checkState(activity.getApplication() instanceof ApplicationComponentProvider, "Make sure your Application class implements ApplicationComponentProvider.");

        applicationComponent = ((ApplicationComponentProvider<ApplicationComponent>) activity.getApplication()).getApplicationComponent();
        rootView = (ViewGroup) activity.findViewById(android.R.id.content);

        triad = ((TriadProvider) activity.getApplication()).getTriad();
        triad.setActivity(activity);
        triad.setListener(new MyTriadListener());

        if (triad.getBackstack().size() > 0 || triad.isTransitioning()) {
            triad.showCurrent();
        } else if (intent != null) {
            Screen<?> screen = createScreen(intent);
            if (screen != null) {
                triad.startWith(screen);
            } else {
                Backstack backstack = createBackstack(intent);
                if (backstack != null) {
                    triad.startWith(backstack);
                }
            }
        }
    }

    public void onResume() {
        if (currentScreen != null) {
            currentScreen.attach(rootView);
        }
    }

    public boolean onBackPressed() {
        checkState(triad != null, "Triad is null. Make sure to call TriadDelegate.onCreate().");

        boolean handledByScreen = currentScreen != null && currentScreen.onBackPressed();
        if (handledByScreen) return true;

        boolean handledByTriad = triad.goBack();
        if (handledByTriad) return true;

        boolean backstackIsEmpty = triad.getBackstack().size() == 0;
        return !backstackIsEmpty;
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        checkState(triad != null, "Triad is null. Make sure to call TriadDelegate.onCreate()");

        triad.onActivityResult(requestCode, resultCode, data);
    }

    public void onPause() {
        if (currentScreen != null) {
            currentScreen.detach();
        }
    }

    public void onDestroy() {
        checkState(triad != null, "Triad is null. Make sure to call TriadDelegate.onCreate()");

        if (!activity.isFinishing()) return;

        for (Iterator<Screen<?>> iterator = triad.getBackstack().reverseIterator(); iterator.hasNext(); ) {
            Screen<?> screen = iterator.next();
            screen.onDestroy();
        }
    }

    /**
     * Returns the {@link Triad} instance to be used to navigate between {@link Screen}s.
     */
    @NonNull
    public Triad getTriad() {
        checkState(triad != null, "Triad is null. Make sure to call TriadDelegate.onCreate().");

        return triad;
    }

    /**
     * Sets an {@link OnScreenChangedListener} to be notified of screen changes.
     */
    public void setOnScreenChangedListener(@Nullable final OnScreenChangedListener<ApplicationComponent> onScreenChangedListener) {
        this.onScreenChangedListener = onScreenChangedListener;
    }

    private void onScreenChanged(@NonNull final Screen<ApplicationComponent> screen) {
        if (onScreenChangedListener != null) {
            onScreenChangedListener.onScreenChanged(screen);
        }
    }

    @NonNull
    public static <T> TriadDelegate<T> createFor(@NonNull final Activity activity) {
        return new TriadDelegate(activity, DefaultTransitionAnimator.INSTANCE);
    }

    @NonNull
    public static <T> TriadDelegate<T> createFor(@NonNull final Activity activity, @NonNull final TransitionAnimator defaultTransitionAnimator) {
        return new TriadDelegate(activity, defaultTransitionAnimator);
    }

    private class MyTriadListener implements Triad.Listener<ApplicationComponent> {

        @Override
        public void screenPushed(@NonNull final Screen<ApplicationComponent> pushedScreen) {
            checkState(applicationComponent != null, "ApplicationComponent is null. Make sure to call TriadDelegate.onCreate().");

            pushedScreen.setApplicationComponent(applicationComponent);
            pushedScreen.onCreate();
        }

        @Override
        public void screenPopped(@NonNull final Screen<ApplicationComponent> poppedScreen) {
            poppedScreen.onDestroy();
        }

        @Override
        public void forward(@NonNull final Screen<ApplicationComponent> newScreen, @Nullable final TransitionAnimator animator, @NonNull final Triad.Callback callback) {
            checkState(rootView != null, "Root view is null. Make sure to call TriadDelegate.onCreate().");

            final View oldView = rootView.getChildAt(0);
            final Screen<ApplicationComponent> oldScreen = currentScreen;
            if (oldView != null && oldScreen != null) {
                oldScreen.saveState(oldView);
            }

            currentScreen = newScreen;
            final View newView = newScreen.createView(rootView);

            boolean handled = false;
            if (animator != null) {
                handled = animator.forward(oldView, newView, rootView, new Callback() {
                    @Override
                    public void onComplete() {
                        newScreen.attach(rootView);

                        if (oldScreen != null) {
                            oldScreen.detach();
                        }

                        callback.onComplete();
                    }
                });
            }

            if (!handled) {
                defaultTransitionAnimator.forward(oldView, newView, rootView, new Callback() {
                    @Override
                    public void onComplete() {
                        newScreen.attach(rootView);

                        if (oldScreen != null) {
                            oldScreen.detach();
                        }

                        callback.onComplete();
                    }
                });
            }

            onScreenChanged(newScreen);
        }

        @Override
        public void backward(@NonNull final Screen<ApplicationComponent> newScreen, @Nullable final TransitionAnimator animator, @NonNull final Triad.Callback callback) {
            checkState(rootView != null, "Root view is null. Make sure to call TriadDelegate.onCreate().");

            final Screen<ApplicationComponent> oldScreen = currentScreen;
            currentScreen = newScreen;

            final View oldView = rootView.getChildAt(0);
            final View newView = newScreen.createView(rootView);
            newScreen.restoreState(newView);

            boolean handled = false;
            if (animator != null) {
                handled = animator.backward(oldView, newView, rootView, new Callback() {
                    @Override
                    public void onComplete() {
                        newScreen.attach(rootView);

                        if (oldScreen != null) {
                            oldScreen.detach();
                        }

                        callback.onComplete();
                    }
                });
            }

            if (!handled) {
                defaultTransitionAnimator.backward(oldView, newView, rootView, new Callback() {
                    @Override
                    public void onComplete() {
                        newScreen.attach(rootView);

                        if (oldScreen != null) {
                            oldScreen.detach();
                        }

                        callback.onComplete();
                    }
                });
            }

            onScreenChanged(newScreen);
        }

        @Override
        public void replace(@NonNull final Screen<ApplicationComponent> newScreen, @Nullable final TransitionAnimator animator, @NonNull final Triad.Callback callback) {
            checkState(rootView != null, "Root view is null. Make sure to call TriadDelegate.onCreate().");

            final Screen<ApplicationComponent> oldScreen = currentScreen;
            currentScreen = newScreen;

            final View oldView = rootView.getChildAt(0);
            final View newView = newScreen.createView(rootView);

            boolean handled = false;
            if (animator != null) {
                handled = animator.forward(oldView, newView, rootView, new Callback() {
                    @Override
                    public void onComplete() {
                        newScreen.attach(rootView);

                        if (oldScreen != null) {
                            oldScreen.detach();
                        }

                        callback.onComplete();
                    }
                });
            }

            if (!handled) {
                defaultTransitionAnimator.forward(oldView, newView, rootView, new Callback() {
                    @Override
                    public void onComplete() {
                        newScreen.attach(rootView);

                        if (oldScreen != null) {
                            oldScreen.detach();
                        }

                        callback.onComplete();
                    }
                });
            }

            onScreenChanged(newScreen);
        }
    }
}
