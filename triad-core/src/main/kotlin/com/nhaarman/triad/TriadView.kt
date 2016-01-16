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

package com.nhaarman.triad

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.RelativeLayout

@SuppressWarnings("AnonymousClassVariableHidesContainingMethodVariable")
open class TriadView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle) {

    private val mTransitionAnimationDurationMs: Long by lazy {
        resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }

    internal open fun forward(currentView: ViewGroup?,
                         newView: ViewGroup,
                         animator: TransitionAnimator?,
                         callback: Triad.Callback) {
        addView(newView)
        newView.viewTreeObserver.addOnPreDrawListener(ForwardTransitionPreDrawListener(currentView, newView, animator, callback))
    }

    internal fun backward(currentView: ViewGroup?,
                          newView: ViewGroup,
                          animator: TransitionAnimator?,
                          callback: Triad.Callback) {
        addView(newView)
        newView.viewTreeObserver.addOnPreDrawListener(BackwardTransitionPreDrawListener(currentView, newView, animator, callback))
    }

    private inner class ForwardTransitionPreDrawListener
    /**
     * @param currentView The [View] to execute an exit animation for.
     * *
     * @param newView     The [View] to execute an entering animation for.
     */
    internal constructor(currentView: View?, newView: View, animator: TransitionAnimator?,
                         callback: Triad.Callback) : TransitionPreDrawListener(currentView, newView, animator, callback) {

        override fun handleAnimation(currentView: View?, newView: View, animator: TransitionAnimator?,
                                     callback: Triad.Callback): Boolean {
            return animator != null && animator.forward(currentView, newView, callback)
        }
    }

    private inner class BackwardTransitionPreDrawListener
    /**
     * @param currentView The [View] to execute an exit animation for.
     * *
     * @param newView     The [View] to execute an entering animation for.
     */
    internal constructor(currentView: View?, newView: View, animator: TransitionAnimator?,
                         callback: Triad.Callback) : TransitionPreDrawListener(currentView, newView, animator, callback) {

        override fun handleAnimation(currentView: View?, newView: View, animator: TransitionAnimator?,
                                     callback: Triad.Callback): Boolean {
            return animator != null && animator.backward(currentView, newView, callback)
        }
    }

    /**
     * Observes when the new [View] is about to be drawn, so a replacement animation can be executed.
     */
    private abstract inner class TransitionPreDrawListener

    /**
     * @param currentView The [View] to execute an exit animation for.
     * *
     * @param newView     The [View] to execute an entering animation for.
     */
    internal constructor(private val mCurrentView: View?,
                         private val mNewView: View,
                         private val mAnimator: TransitionAnimator?,
                         private val mCallback: Triad.Callback) : OnPreDrawListener {

        override fun onPreDraw(): Boolean {
            mNewView.viewTreeObserver.removeOnPreDrawListener(this)

            val handled = handleAnimation(mCurrentView, mNewView, mAnimator, mCallback)
            if (!handled) {
                animateViewReplacing()
            }

            return true
        }

        protected abstract fun handleAnimation(currentView: View?, newView: View, animator: TransitionAnimator?,
                                               callback: Triad.Callback): Boolean

        /**
         * Executes an exit animation for the old [View], and an entering animation for the new [View].
         * The old [View] will be removed from its parent after the animation.
         */
        private fun animateViewReplacing() {
            val anim = ValueAnimator.ofFloat(0f, 1f)
            anim.setDuration(mTransitionAnimationDurationMs)
            anim.addUpdateListener { animation -> animateAlphas(animation.animatedValue as Float) }
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    finishAnimation()
                }
            })
            anim.start()
        }

        private fun animateAlphas(value: Float) {
            if (mCurrentView != null) {
                mCurrentView.alpha = 1 - value
            }
            mNewView.alpha = value
        }

        private fun finishAnimation() {
            if (mCurrentView != null) {
                (mCurrentView.parent as ViewManager).removeView(mCurrentView)
            }

            mCallback.onComplete()
        }
    }
}
