package com.samnetworks.smoother;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.transition.CircularPropagation;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionPropagation;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Sourabh Gupta on 13/4/20.
 */
public class SmoothAnimator {
    private ViewGroup containerView;
    private View startingView;
    private Transition transition;
    private int duration = 600;
    private Interpolator interpolator;
    private TransitionPropagation propagation;
    HashMap<ViewGroup, Transition> transitionHashMap = new HashMap<>();

    public SmoothAnimator(ViewGroup containerView,
                          View startingView,
                          Transition transition,
                          int duration,
                          Interpolator interpolator,
                          TransitionPropagation propagation) {
        this.containerView = containerView;
        this.startingView = startingView;
        this.transition = transition;
        this.duration = duration;
        this.interpolator = interpolator;
        this.propagation = propagation;
        if (transition == null) {
            this.transition = new Fade(Fade.IN);
        }
        if (interpolator == null) {
            this.interpolator = new LinearOutSlowInInterpolator();
        }
        if (propagation == null) {
            CircularPropagation circularPropagation = new CircularPropagation();
            circularPropagation.setPropagationSpeed(2f);
            this.propagation = circularPropagation;
        }
        this.transition.setInterpolator(interpolator);
        this.transition.setDuration(duration);
        this.transition.setPropagation(this.propagation);

    }

    public void prepare() {
        if (startingView == null && containerView.getChildCount() > 0) {
            startingView = containerView.getChildAt(0);
        }
        transition.setEpicenterCallback(makeAnchor(startingView));
        transitionHashMap.put(containerView,transition);
    }

    public void startAnimation(){
        for(final ViewGroup containerView:transitionHashMap.keySet()) {
            for (int i = 0; i < containerView.getChildCount(); i++) {
                containerView.getChildAt(i).setVisibility(View.INVISIBLE);
            }
            Objects.requireNonNull(transitionHashMap.get(containerView)).addListener(new TransitionListenerAdapter(){
                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    super.onTransitionEnd(transition);
                    transitionHashMap.remove(containerView);
                }
            });
            TransitionManager.beginDelayedTransition(containerView,transitionHashMap.get(containerView));
            for (int i = 0; i < containerView.getChildCount(); i++) {
                containerView.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
    }

    private Transition.EpicenterCallback makeAnchor(View view) {
        final Rect viewRect = new Rect();
        view.getGlobalVisibleRect(viewRect);
        return new Transition.EpicenterCallback() {
            @Override
            public Rect onGetEpicenter(@NonNull Transition transition) {
                return viewRect;
            }
        };
    }

    public void addNewSmootherForChildViewGroup(ViewGroup viewGroup,Transition transition) {
        this.transition.excludeTarget(viewGroup,true);
        View startingView = viewGroup.getChildAt(0);
        if(startingView==null)
            return;
        transition.setEpicenterCallback(makeAnchor(startingView));
        transitionHashMap.put(viewGroup,transition);
    }
}
