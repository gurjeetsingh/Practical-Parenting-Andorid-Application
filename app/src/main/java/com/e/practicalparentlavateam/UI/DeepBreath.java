package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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

    //Enum for all the states in state machine
    public enum State {
        WAITING_TO_INHALE, INHALING, DONE_INHALE, EXHALE, DONE,
    }
    private State breathState = State.WAITING_TO_INHALE;

    private ImageView circle;
    private Button enlarge;
    private MediaPlayer sound;

    //state machine vars
    private static final String EXTRA_NUM_BREATHS = "Extra - Num breaths";
    private static int numBreaths;
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
                        sound = MediaPlayer.create(DeepBreath.this, R.raw.music);
                        sound.start();
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
                                sound.stop();
                                enlarge.setText(R.string.begin);
                            }
                            else{
                                enlarge.setText(R.string.out);
                                circle.startAnimation(animationOut);
                                sound.stop();
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
                currentStateView.setText("WAITING_TO_INHALE");
                waitngToInhale();
                break;
            case INHALING:
                currentStateView.setText("INHALING");
                inhaling();
                break;
            case DONE_INHALE:
                currentStateView.setText("DONE_INHALE");
                doneInhale();
                break;
            case EXHALE:
                currentStateView.setText("EXHALE");
                exhale();
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
                            changeState(State.EXHALE);
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
        beginFSM.setText("IN");

    }

    private void inhaling() {
        //TODO: Begin animation
        //TODO: Begin sound
        //TODO: handler for stopping after 10 seconds
    }


    private void doneInhale() {
        //TODO: Stop inhale animation
        //TODO: stop inhale sound
        if (breathState == State.DONE_INHALE) {
            changeState(State.EXHALE);
            //currentStateView.setText("INHALING");
        }
        beginFSM.setText("OUT");

    }

    private void exhale() {
        //TODO: Start exhale animation
        //TODO: Start exhale sound

        numBreaths--;
        breathDisplay.setText(""+ numBreaths);

        if(numBreaths > 0){
            if (breathState == State.EXHALE) {
                //TODO: reset ontouch listner
                //beginBreathing();
                changeState(State.WAITING_TO_INHALE);

            }
        }
        else if(numBreaths == 0){
            if (breathState == State.EXHALE) {
                changeState(State.DONE);
            }
        }

    }


    public static Intent makeDeepBreathIntent(Context context, int numBreaths) {
        Intent intent = new Intent(context, DeepBreath.class);
        intent.putExtra(EXTRA_NUM_BREATHS, numBreaths);
        return intent;
    }
}