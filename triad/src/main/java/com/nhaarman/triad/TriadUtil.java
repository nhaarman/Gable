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
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import com.nhaarman.triad.ActivityComponentProvider;
import com.nhaarman.triad.Presenter;
import com.nhaarman.triad.ScreenProvider;

/**
 * An utility class that retrieves the ActivityComponent and Presenter using a Context instance.
 */
public class TriadUtil {

    private TriadUtil() {
    }

    @NonNull
    public static <ActivityComponent> ActivityComponent findActivityComponent(@NonNull final Context context) {
        Context baseContext = context;
        while (!(baseContext instanceof Activity) && baseContext instanceof ContextWrapper) {
            baseContext = ((ContextWrapper) baseContext).getBaseContext();
        }

        if (baseContext instanceof ActivityComponentProvider) {
            //noinspection unchecked
            return ((ActivityComponentProvider<ActivityComponent>) baseContext).getActivityComponent();
        } else {
            /* We return null, since the layout editor can not return the Activity Component. */
            //noinspection ConstantConditions
            return null;
        }
    }

    @NonNull
    public static <P extends Presenter<?, ?>> P findPresenter(@NonNull final Context context, final int viewId) {
        Context baseContext = context;
        while (!(baseContext instanceof Activity) && baseContext instanceof ContextWrapper) {
            baseContext = ((ContextWrapper) baseContext).getBaseContext();
        }

        if (baseContext instanceof ScreenProvider) {
            //noinspection unchecked
            return (P) ((ScreenProvider) baseContext).getCurrentScreen().getPresenter(viewId);
        } else {
            /* We return null, since the layout editor can not return the ScreenProvider. */
            //noinspection ConstantConditions
            return null;
        }
    }
}
