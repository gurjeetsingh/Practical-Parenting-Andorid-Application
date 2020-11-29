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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.practicalparentlavateam.R;

public class DeepBreath extends AppCompatActivity {
    private ImageView circle;
    private Button enlarge;
    private Button shrink;

    private static final String EXTRA_NUM_BREATHS = "Extra - Num breaths";
    private int numBreaths;
    private TextView breathDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);

        //https://commons.wikimedia.org/wiki/File:Small-dark-green-circle.svg
        circle = findViewById(R.id.circle);
        enlarge = findViewById(R.id.enlarge);

        //initialize display of breaths
        breathDisplay = findViewById(R.id.num_breaths_rem);

        enlargeCircle();
        //extract number of breaths form setup
        breathSetup();
    }

    private void breathSetup(){
        Intent intent = getIntent();
        numBreaths = intent.getIntExtra(EXTRA_NUM_BREATHS, 0);
        //breaths selected displayed
        breathDisplay.setText(""+ numBreaths);


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
                final Runnable toInhale = new Runnable() {
                    @Override
                    public void run() {
                        enlarge.setText(R.string.in);
                    }
                };
                handler.postDelayed(actionIn,0);
                enlarge.setText(R.string.in);
                enlarge.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            enlarge.setText(R.string.in);
                            handler.postDelayed(actionIn, 0);
                        }
                        if(event.getAction() == MotionEvent.ACTION_UP){
                            if(event.getEventTime() - event.getDownTime() < 3000) {
                                circle.clearAnimation();
                                enlarge.setText(R.string.begin);
                            }
                            else{
                                enlarge.setText(R.string.out);
                                circle.startAnimation(animationOut);
                                handler.postDelayed(toInhale,3000);
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

    public static Intent makeDeepBreathIntent(Context context, int numBreaths) {
        Intent intent = new Intent(context, DeepBreath.class);
        intent.putExtra(EXTRA_NUM_BREATHS, numBreaths);
        return intent;
    }
}