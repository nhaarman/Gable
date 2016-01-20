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
import com.nhaarman.triad.tests.TestActivity;
import java.util.Collection;
import org.junit.Rule;

public class TestActivityInstrumentationTestCase {

    @Rule
    public final ActivityTestRule<TestActivity> mActivityRule = new MyActivityTestRule();

    public TestActivity getActivity() {
        return mActivityRule.getActivity();
    }

    protected Triad getTriad() {
        return mActivityRule.getActivity().getTriad();
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

    private class MyActivityTestRule extends ActivityTestRule<TestActivity> {

        private TestActivity mActivity;

        MyActivityTestRule() {
            super(TestActivity.class);
        }

        @Override
        protected void beforeActivityLaunched() {
            getInstrumentation().callApplicationOnCreate((Application) InstrumentationRegistry.getTargetContext().getApplicationContext());
        }

        @Override
        public TestActivity getActivity() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                    if (activities.isEmpty()) {
                        mActivity = null;
                    } else {
                        mActivity = (TestActivity) activities.iterator().next();
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
