package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.e.practicalparentlavateam.R;

public class DeepBreath extends AppCompatActivity {
    private ImageView circle;
    private Button enlarge;
    private Button shrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);

        //https://commons.wikimedia.org/wiki/File:Small-dark-green-circle.svg
        circle = findViewById(R.id.circle);
        enlarge = findViewById(R.id.enlarge);
        shrink = findViewById(R.id.shrink);

        enlargeCircle();
        shrinkCircle();
    }

    private void enlargeCircle() {
        enlarge.setOnLongClickListener(new View.OnLongClickListener() {
            Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
            private Handler handler = new Handler();
            @Override
            public boolean onLongClick(View v) {
                final Runnable actionIn = new Runnable() {
                    @Override
                    public void run() {
                        circle.startAnimation(animationIn);
                    }
                };
                final Runnable actionOut = new Runnable() {
                    @Override
                    public void run() {
                        circle.startAnimation(animationOut);
                        enlarge.setText("Out");
                    }
                };
                handler.postDelayed(actionIn,0);
                enlarge.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            handler.postDelayed(actionIn, 0);
                        }
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            if(event.getEventTime() - event.getDownTime() < 3000) {
                                circle.clearAnimation();
                            }
                            else{
                                enlarge.setText("Out");
                                circle.startAnimation(animationOut);
                            }
                            return true;
                        }
                        return true;
                    }
                });
                return true;
            }
        });
    }

    private void shrinkCircle() {
        shrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                circle.startAnimation(animation);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DeepBreath.class);
    }
}