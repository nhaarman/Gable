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

package com.nhaarman.triad.sample.notes

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.nhaarman.triad.TransitionAnimator
import com.nhaarman.triad.Triad

internal class TranslateLeftAnimator : TransitionAnimator {

    override fun forward(oldView: View?, newView: View, callback: Triad.Callback): Boolean {
        if (oldView == null) {
            return false
        }

        oldView.animate().x((-oldView.width).toFloat()).setInterpolator(AccelerateInterpolator())
        newView.x = (newView.parent as View).width.toFloat()
        newView.animate().x(0f).setInterpolator(DecelerateInterpolator()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                newView.animate().setListener(null)
                (oldView.parent as ViewManager).removeView(oldView)

                callback.onComplete()
            }
        })

        return true
    }

    override fun backward(oldView: View?, newView: View, callback: Triad.Callback): Boolean {
        if (oldView == null) {
            return false
        }

        newView.x = (-(newView.parent as View).width).toFloat()
        newView.animate().x(0f).setInterpolator(AccelerateInterpolator())
        oldView.animate().x((oldView.parent as View).width.toFloat()).setInterpolator(DecelerateInterpolator()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                oldView.animate().setListener(null)
                (oldView.parent as ViewManager).removeView(oldView)

                callback.onComplete()
            }
        })

        return true
    }
}