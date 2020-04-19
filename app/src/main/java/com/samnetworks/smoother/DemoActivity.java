package com.samnetworks.smoother;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.CircularPropagation;
import androidx.transition.Fade;
import androidx.transition.TransitionSet;

/**
 * Created by Sourabh Gupta on 13/4/20.
 */
public class DemoActivity extends AppCompatActivity {
    LinearLayout frame_layout;
    RecyclerView recyclerview;
    TextView demo_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        frame_layout = findViewById(R.id.frame_layout);
        recyclerview = findViewById(R.id.recyclerview);
        demo_text = findViewById(R.id.demo_text);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerview.setAdapter(new RecyclerViewAdapter());
        frame_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                SmoothAnimator propagationTransition = new SmoothAnimator(frame_layout,
                        demo_text,
                        new TransitionSet()
                                .addTransition(new Fade(Fade.IN)).setInterpolator(new Interpolator() {
                            @Override
                            public float getInterpolation(float input) {
                                return (input - 0.5f) * 2;
                            }
                        }).addTransition(new TranslateAnimation(100, TranslateAnimation.Direction.BOTTTOM))
                        , 300, null, null);
                propagationTransition.prepare();
                propagationTransition.startAnimation();
                frame_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void replay(View view) {
        SmoothAnimator propagationTransition = new SmoothAnimator(frame_layout,
                demo_text,
                new TransitionSet()
                        .addTransition(new Fade(Fade.IN)).setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return (input - 0.5f) * 2;
                    }
                }).addTransition(new TranslateAnimation(100, TranslateAnimation.Direction.BOTTTOM))
                , 300, null, null);
        TransitionSet transitionSet = new TransitionSet()
                .addTransition(new Fade(Fade.IN)).addTransition(new TranslateAnimation(100, TranslateAnimation.Direction.RIGHT));
        transitionSet.setStartDelay(300);
        CircularPropagation circularPropagation = new CircularPropagation();
        circularPropagation.setPropagationSpeed(1f);
        transitionSet.setPropagation(circularPropagation);
        transitionSet.setInterpolator(new LinearOutSlowInInterpolator());
        transitionSet.setDuration(400);
        propagationTransition.addNewSmootherForChildViewGroup(recyclerview,transitionSet);
        propagationTransition.prepare();
        propagationTransition.startAnimation();
    }
}
