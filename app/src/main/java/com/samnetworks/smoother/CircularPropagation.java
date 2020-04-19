/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.transition.VisibilityPropagation;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class CircularPropagation extends VisibilityPropagation {

    private float mPropagationSpeed = 1.0f;
    public void setPropagationSpeed(float propagationSpeed) {
        Log.e("propagationSpeed","dsadas");
        if (propagationSpeed == 0) {
            throw new IllegalArgumentException("propagationSpeed may not be 0");
        }
        mPropagationSpeed = propagationSpeed;
    }

    /**
     * change get start delay logic for various animations like staggered or jumble
     * /
     * @param sceneRoot
     * @param transition
     * @param startValues
     * @param endValues
     * @return
     */
    @Override
    public long getStartDelay(ViewGroup sceneRoot, Transition transition,
                              TransitionValues startValues, TransitionValues endValues) {
        Log.d("start_delay",""+1000);
        if (startValues == null && endValues == null) {
            return 0;
        }
        int directionMultiplier = 1;
        TransitionValues positionValues;
        if (endValues == null || getViewVisibility(startValues) == View.VISIBLE) {
            positionValues = startValues;
            directionMultiplier = -1;
        } else {
            positionValues = endValues;
        }

        int viewCenterX = getViewX(positionValues);
        int viewCenterY = getViewY(positionValues);

        Rect epicenter = transition.getEpicenter();
        int epicenterX;
        int epicenterY;
        if (epicenter != null) {
            epicenterX = epicenter.centerX();
            epicenterY = epicenter.centerY();
        } else {
            int[] loc = new int[2];
            sceneRoot.getLocationOnScreen(loc);
            epicenterX = Math.round(loc[0] + (sceneRoot.getWidth() / 2)
                    + sceneRoot.getTranslationX());
            epicenterY = Math.round(loc[1] + (sceneRoot.getHeight() / 2)
                    + sceneRoot.getTranslationY());
        }
        float distance = distance(viewCenterX, viewCenterY, epicenterX, epicenterY);
        float maxDistance = distance(0, 0, sceneRoot.getWidth(), sceneRoot.getHeight());
        float distanceFraction = distance / maxDistance;

        long duration = transition.getDuration();
        if (duration < 0) {
            duration = 300;
        }
        Log.d("start_delay",""+1000 * distanceFraction);
        return Math.round(1000 * distanceFraction);
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        Log.e("distance",x2+"");
        float x = x2 - x1;
        float y = y2 - y1;
        return (float) Math.sqrt((x * x) + (y * y));
    }

}
