/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samnetworks.smoother;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionValues;
import androidx.transition.Visibility;

public class TranslateAnimation extends Visibility {
    private int translationDistance;
    private Direction direction = Direction.BOTTTOM;
    /**
     * Constructs a Fade transition that will fade targets in and out.
     *
     * @param translationDistance is in DP
     */
    public TranslateAnimation(int translationDistance, Direction direction) {
        this.translationDistance = translationDistance;
        this.direction = direction;
    }

    private static float getStartAlpha(TransitionValues startValues, float fallbackValue) {
        return fallbackValue;
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
    }

    /**
     * Utility method to handle creating and running the Animator.
     */
    private Animator createAnimation(final View view, float startAlpha, float endAlpha) {
        if (startAlpha == endAlpha) {
            return null;
        }
        ObjectAnimator anim = null;
        if(view.getTag()!=null && view.getTag().equals("scale")){
            view.setScaleX(0f);
            view.setScaleY(0f);
            anim = ObjectAnimator.ofPropertyValuesHolder(view,
                    PropertyValuesHolder.ofFloat("scaleX", 1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1f));
            FadeAnimatorListener listener = new FadeAnimatorListener(view);
            anim.addListener(listener);
            addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                    transition.removeListener(this);
                }
            });
        } else {
            if (direction == Direction.BOTTTOM) {
                view.setTranslationY(DimensionUtils.dpToPx(translationDistance));
                anim = ObjectAnimator.ofFloat(view, "translationY",
                        0);
                if (true) {
                    Log.d("debug_translate", "Created animator ");
                }
                FadeAnimatorListener listener = new FadeAnimatorListener(view);
                anim.addListener(listener);
                addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
                        view.setTranslationY(0);
                        transition.removeListener(this);
                    }
                });
            } else if (direction == Direction.RIGHT) {
                view.setTranslationX(DimensionUtils.dpToPx(translationDistance));
                anim = ObjectAnimator.ofFloat(view, "translationX",
                        0);
                if (true) {
                    Log.d("debug_translate", "Created animator ");
                }
                FadeAnimatorListener listener = new FadeAnimatorListener(view);
                anim.addListener(listener);
                addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionEnd(@NonNull Transition transition) {
                        view.setTranslationX(0);
                        transition.removeListener(this);
                    }
                });
            }
        }
        return anim;
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view,
                             TransitionValues startValues,
                             TransitionValues endValues) {
        float startAlpha = getStartAlpha(startValues, 0);
        if (startAlpha == 1) {
            startAlpha = 0;
        }
        return createAnimation(view, startAlpha, 1);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, final View view, TransitionValues startValues,
                                TransitionValues endValues) {
        float startAlpha = getStartAlpha(startValues, 1);
        return createAnimation(view, startAlpha, 0);
    }

    public enum Direction {
        BOTTTOM, RIGHT
    }

    private static class FadeAnimatorListener extends AnimatorListenerAdapter {

        private final View mView;
        private boolean mLayerTypeChanged = false;

        FadeAnimatorListener(View view) {
            mView = view;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (ViewCompat.hasOverlappingRendering(mView)
                    && mView.getLayerType() == View.LAYER_TYPE_NONE) {
                mLayerTypeChanged = true;
                mView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mLayerTypeChanged) {
                mView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }

    }

}
