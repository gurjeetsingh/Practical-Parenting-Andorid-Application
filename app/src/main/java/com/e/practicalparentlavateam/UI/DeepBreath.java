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
        WAITING_TO_INHALE, CONTINUE, INHALING, EXHALE, DONE,
    }
    private State breathState = State.WAITING_TO_INHALE;

    private ImageView circle;
    private MediaPlayer sound = new MediaPlayer();

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

        //initialize display of breaths
        breathDisplay = findViewById(R.id.num_breaths_rem);

        //TODO: delete after testing of state machine complete
        //display of state machine
        currentStateView = findViewById(R.id.state);
        //fsm testing begin
        beginFSM = findViewById(R.id.breath);

        //enlargeCircle();
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

    private void beginBreathing() {
        final Handler handler = new Handler();
        final Runnable afterExhaling = new Runnable() {
            @Override
            public void run() {
                if(numBreaths > 0){
                    if (breathState == State.EXHALE) {
                        //TODO: reset ontouch listner
                        //beginBreathing();
                        changeState(State.CONTINUE);

                    }
                }
                else if(numBreaths == 0){
                    if (breathState == State.EXHALE) {
                        changeState(State.DONE);
                    }
                }
            }
        };
        handler.postDelayed(afterExhaling,3000);
        beginFSM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathState == State.WAITING_TO_INHALE || breathState == State.CONTINUE) {
                        changeState(State.INHALING);
                        //currentStateView.setText("INHALING");
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getEventTime() - event.getDownTime() < 3000) {
                        if (breathState == State.INHALING) {
                            changeState(State.WAITING_TO_INHALE);
                            sound.stop();
                            //currentStateView.setText("INHALING");
                        }
                    }
                    else {
                        if (breathState == State.INHALING) {
                            changeState(State.EXHALE);
                            //currentStateView.setText("INHALING");
                            handler.postDelayed(afterExhaling,3000);
                        }
                    }
                    return true;
                }
                return true;
            }
        });
    }

    private void changeState(State newState) {
        currentStateView.setText(newState.name());

        switch (newState) {
            case WAITING_TO_INHALE:
                currentStateView.setText(R.string.waiting_to_inhale);
                waitingToInhale();
                break;
            case CONTINUE:
                currentStateView.setText(R.string.continue_breath);
                continueInhaling();
                break;
            case INHALING:
                currentStateView.setText(R.string.inhaling);
                inhaling();
                break;
            case EXHALE:
                currentStateView.setText(R.string.exhaling);
                exhale();
                break;
            case DONE:
                currentStateView.setText(R.string.finish);
                beginFSM.setText(R.string.finish);
                //done();
                break;
        }
        breathState = newState;
    }

    private void waitingToInhale() {
        //TODO: Stop animation
        //TODO: Stop sound
        circle.clearAnimation();
    }

    private void continueInhaling(){
        beginFSM.setText(R.string.continue_breath);
    }

    private void inhaling() {
        //TODO: Begin animation
        //TODO: Begin sound
        //TODO: handler for stopping after 10 seconds
        beginFSM.setText(R.string.in);
        Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        circle.startAnimation(animationIn);
        if(!sound.equals(null))
            sound.stop();
        sound = MediaPlayer.create(DeepBreath.this, R.raw.music);
        sound.start();
    }

    private void exhale() {
        //TODO: Start exhale animation
        //TODO: Start exhale sound
        Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        circle.startAnimation(animationOut);
        if(!sound.equals(null))
            sound.stop();

        sound = MediaPlayer.create(DeepBreath.this, R.raw.out);
        sound.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sound.stop();
            }
        }, 1000*10);


        beginFSM.setText(R.string.out);
        numBreaths--;
        breathDisplay.setText(""+ numBreaths);
    }

    @Override
    public void onBackPressed() {
        sound.stop();
        Intent mainIntent=MainMenu.makeIntent(DeepBreath.this);
        startActivity(mainIntent);
        DeepBreath.this.finish();
    }


    public static Intent makeDeepBreathIntent(Context context, int numBreaths) {
        Intent intent = new Intent(context, DeepBreath.class);
        intent.putExtra(EXTRA_NUM_BREATHS, numBreaths);
        return intent;
    }
}