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

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.ViewGroup;
import java.util.Collection;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class TriadActivityInstrumentationTestCase<T extends Activity> {

    @Rule
    public final ActivityTestRule<T> mActivityRule;

    public TriadActivityInstrumentationTestCase(final Class<T> activityClass) {
        mActivityRule = new MyActivityTestRule(activityClass);
    }

    protected void createNote(final String title, final String contents) throws InterruptedException {
        onView(withText(com.nhaarman.triad.sample.R.string.create_note)).perform(click());
        onView(withHint(com.nhaarman.triad.sample.R.string.title)).perform(typeText(title));
        onView(withHint(com.nhaarman.triad.sample.R.string.contents)).perform(typeText(contents));
        onView(withText(com.nhaarman.triad.sample.R.string.save)).perform(click());
        getInstrumentation().waitForIdleSync();
    }

    public T getActivity() {
        return mActivityRule.getActivity();
    }

    protected Triad getTriad() {
        return ((TriadProvider) mActivityRule.getActivity().getApplicationContext()).getTriad();
    }

    protected ViewGroup getScreenHolder() {
        return (ViewGroup) getActivity().findViewById(com.nhaarman.triad.R.id.view_triad);
    }

    protected Instrumentation getInstrumentation() {
        return InstrumentationRegistry.getInstrumentation();
    }

    protected void rotate() {
        int orientation = InstrumentationRegistry.getTargetContext().getResources().getConfiguration().orientation;

        getActivity().setRequestedOrientation(
              orientation == Configuration.ORIENTATION_PORTRAIT ?
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        );
        getInstrumentation().waitForIdleSync();
    }

    private class MyActivityTestRule extends ActivityTestRule<T> {

        private T mActivity;

        MyActivityTestRule(final Class<T> activityClass) {
            super(activityClass);
        }

        @Override
        protected void beforeActivityLaunched() {
            getInstrumentation().callApplicationOnCreate((Application) InstrumentationRegistry.getTargetContext().getApplicationContext());
        }

        @Override
        public T getActivity() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                    if (activities.isEmpty()) {
                        mActivity = null;
                    } else {
                        mActivity = (T) activities.iterator().next();
                    }
                }
            };
            if (Looper.myLooper() == Looper.getMainLooper()) {
                runnable.run();
            } else {
                getInstrumentation().runOnMainSync(runnable);
                getInstrumentation().waitForIdleSync();
            }

            return mActivity == null ? super.getActivity() : mActivity;
        }
    }
}
