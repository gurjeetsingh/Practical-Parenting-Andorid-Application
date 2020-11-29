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
import android.widget.TextView;

import com.e.practicalparentlavateam.R;

public class DeepBreath extends AppCompatActivity {

    //Enu for all the states in state machine
    public enum State {
        WAITING_TO_INHALE, INHALING, INHALED_FOR_3S, INHALES_FOR_10S, DONE_INHALE, READY_TO_EXHALE, EXHALING, EXHALE_3S, DONE_EXHALE, MORE, DONE,
    }
    private State breathState = State.WAITING_TO_INHALE;

    private ImageView circle;
    private Button enlarge;
    private Button shrink;

    //state machine vars
    private static final String EXTRA_NUM_BREATHS = "Extra - Num breaths";
    private int numBreaths;
    private TextView breathDisplay;
    //for testing
    //TODO: delete later, only for testing cycling through state machine
    private TextView currentStateView;
    private Button beginFSM;
    public Handler fsmHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);

        //https://commons.wikimedia.org/wiki/File:Small-dark-green-circle.svg
        circle = findViewById(R.id.circle);
        enlarge = findViewById(R.id.enlarge);

        //initialize display of breaths
        breathDisplay = findViewById(R.id.num_breaths_rem);

        //TODO: delete after testing of state machine complete
        //display of state machine
        currentStateView = findViewById(R.id.state);
        //fsm testing begin
        beginFSM = findViewById(R.id.button_fsm);

        enlargeCircle();
        //extract number of breaths form setup
        breathSetup();
        
        beginBreathing();

        //Set the current state
        //changeState();
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

    private void changeState(State newState) {
        currentStateView.setText(newState.name());

        switch (newState) {
            case WAITING_TO_INHALE:
                waitngToInhale();
                break;
            case INHALING:
                currentStateView.setText("INHALING");
                inhaling();
                break;
            case INHALED_FOR_3S:
                currentStateView.setText("INHALED_FOR_3S");
                inhaled3s();
                break;
            case INHALES_FOR_10S:
                currentStateView.setText("INHALES_FOR_10S");
                inhaled10s();
                break;
            case DONE_INHALE:
                currentStateView.setText("DONE_INHALE");
                doneInhale();
                break;
            case READY_TO_EXHALE: //changed to ready to exhale
                currentStateView.setText("READY_TO_EXHALE");
                readyExhale();
                break;
            case EXHALING:
                currentStateView.setText("EXHALING");
                exhaling();
                break;
            case EXHALE_3S:
                currentStateView.setText("EXHALE_3S");
                exhale3s();
                break;
            case DONE_EXHALE:
                currentStateView.setText("DONE_EXHALE");
                break;
                //doneExhale();
            case MORE:
                currentStateView.setText("MORE");
                //moreBreaths();
                break;
            case DONE:
                currentStateView.setText("DONE");
                //done();
                break;
        }
        breathState = newState;
    }

    private void beginBreathing() {
        beginFSM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathState == State.WAITING_TO_INHALE) {
                        changeState(State.INHALING);
                        //currentStateView.setText("INHALING");
                    }


                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getEventTime() - event.getDownTime() < 3000) {
                        if (breathState == State.INHALING) {
                            changeState(State.WAITING_TO_INHALE);
                            //currentStateView.setText("INHALING");
                        }
                    }
                    if(event.getEventTime() - event.getDownTime() > 3000) {
                        if (breathState == State.INHALING) {
                            changeState(State.INHALED_FOR_3S);
                            //currentStateView.setText("INHALING");
                        }
                    }
                    //TODO: is this condition ever checked?
                    if(event.getEventTime() - event.getDownTime() > 10000) {
                        if (breathState == State.INHALING) {
                            changeState(State.INHALES_FOR_10S);
                            //currentStateView.setText("INHALING");
                        }
                    }
                    else{
                        enlarge.setText(R.string.out);
                    }
                    return true;
                }
                return true;
            }
        });
    }


    private void waitngToInhale() {
        //TODO: Stop animation
        //TODO: Stop sound

    }

    private void inhaling() {
        //TODO: Begin animation
        //TODO: Begin sound
    }

    private void inhaled3s() {

        if (breathState == State.INHALED_FOR_3S) {
            changeState(State.DONE_INHALE);
            //currentStateView.setText("INHALING");
        }
        beginFSM.setText("OUT");

    }

    private void inhaled10s() {
        //TODO: Display help: Release the button
        if (breathState == State.INHALES_FOR_10S) {
            changeState(State.DONE_INHALE);
            //currentStateView.setText("INHALING");
        }
        beginFSM.setText("OUT");
    }

    private void doneInhale() {
        //TODO: Stop inhale animation
        //TODO: stop inhale sound
        if (breathState == State.DONE_INHALE) {
            changeState(State.READY_TO_EXHALE);
            //currentStateView.setText("INHALING");
        }
        beginFSM.setText("OUT");

    }

    private void readyExhale() {
        //TODO: Start exhale animation
        //TODO: Start exhale sound
        endBreathing();
    }

    private void exhaling() {
        //TODO: Play exhale animation
        //TODO: Start exhale sound
    }

    private void exhale3s() {
        //TODO: Update count of breaths left
        //TODO: Last breath logic
        beginFSM.setText("IN");

    }

//correct logic for breathing out, but not working
    private void endBreathing() {

        beginFSM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathState == State.READY_TO_EXHALE) {
                        changeState(State.EXHALING);

                    }


                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getEventTime() - event.getDownTime() < 3000) {
                        if (breathState == State.EXHALING) {
                            changeState(State.READY_TO_EXHALE);

                        }
                    }
                    if(event.getEventTime() - event.getDownTime() > 3000) {
                        if (breathState == State.EXHALING) {
                            changeState(State.EXHALE_3S);

                        }
                    }
                    //TODO: this condition is never checked?
                    if(event.getEventTime() - event.getDownTime() > 7000) {
                        if (breathState == State.EXHALING) {
                            changeState(State.DONE_EXHALE);

                        }
                    }
                    else{
                        enlarge.setText(R.string.out);
                    }
                    return true;
                }
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