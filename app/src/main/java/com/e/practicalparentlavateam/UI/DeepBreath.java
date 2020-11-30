package com.e.practicalparentlavateam.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class DeepBreath extends AppCompatActivity {

    //Enum for all the states in state machine
    public enum State {
        WAITING_TO_INHALE, CONTINUE, INHALING, EXHALE, DONE,
    }
    private State breathState = State.WAITING_TO_INHALE;

    private ImageView circle;
    private MediaPlayer soundIn = new MediaPlayer();
    private MediaPlayer soundOut = new MediaPlayer();

    //state machine vars
    private static final String EXTRA_NUM_BREATHS = "Extra - Num breaths";
    private static int numBreaths;
    private TextView breathDisplay;
    //for testing
    //TODO: delete later, only for testing cycling through state machine
    private TextView currentStateView;
    private Button beginFSM;
    private Button more;
    public Handler fsmHandler = new Handler();
    private Runnable soundInControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);

        soundInControl = new Runnable() {
            @Override
            public void run() {
                soundIn.stop();
            }
        };

        //https://commons.wikimedia.org/wiki/File:Small-dark-green-circle.svg
        circle = findViewById(R.id.circle);

        //initialize display of breaths
        breathDisplay = findViewById(R.id.num_breaths_rem);

        //TODO: delete after testing of state machine complete
        //display of state machine
        currentStateView = findViewById(R.id.state);
        //fsm testing begin
        beginFSM = findViewById(R.id.breath);

        more = (Button) findViewById(R.id.more);
        more.setVisibility(View.INVISIBLE);
        back();

        //extract number of breaths form setup
        breathSetUp();

        beginBreathing();
        //Set the current state
        //changeState();
    }


    private void breathSetUp(){
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

        final Runnable releaseHint = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DeepBreath.this, getString(R.string.hint_for_release), Toast.LENGTH_LONG)
                        .show();
            }
        };

        beginFSM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathState == State.WAITING_TO_INHALE || breathState == State.CONTINUE) {
                        changeState(State.INHALING);
                        //currentStateView.setText("INHALING");
                        handler.postDelayed(releaseHint,10000);
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getEventTime() - event.getDownTime() < 3000) {
                        if (breathState == State.INHALING) {
                            changeState(State.WAITING_TO_INHALE);
                            soundIn.stop();
                            handler.removeCallbacks(releaseHint);
                            //currentStateView.setText("INHALING");
                        }
                    }
                    else {
                        if (breathState == State.INHALING) {
                            changeState(State.EXHALE);
                            //currentStateView.setText("INHALING");
                            handler.postDelayed(afterExhaling,3000);
                            handler.removeCallbacks(releaseHint);
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
                beginFSM.setText(R.string.good_job);
                more.setVisibility(View.VISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = BreathSetup.makeIntent(DeepBreath.this);
                        soundOut.stop();
                        startActivity(intent);
                        finish();
                    }
                });
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
        beginFSM.setText(R.string.in);
        Toast.makeText(DeepBreath.this, getString(R.string.hint_for_breath), Toast.LENGTH_LONG)
                .show();
    }

    private void inhaling() {
        //TODO: Begin animation
        //TODO: Begin sound
        //TODO: handler for stopping after 10 seconds
        beginFSM.setText(R.string.in);
        Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        circle.startAnimation(animationIn);
        if(!soundOut.equals(null))
            soundOut.stop();
        soundIn = MediaPlayer.create(DeepBreath.this, R.raw.sound_in);
        soundIn.start();
    }

    private void exhale() {
        //TODO: Start exhale animation
        //TODO: Start exhale sound
        Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        circle.startAnimation(animationOut);
        if(!soundIn.equals(null))
            soundIn.stop();
        soundOut = MediaPlayer.create(DeepBreath.this, R.raw.sound_out);
        soundOut.start();


        beginFSM.setText(R.string.out);
        numBreaths--;
        breathDisplay.setText(""+ numBreaths);
    }

    private void back() {
        Button button = (Button) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MainMenu.makeIntent(DeepBreath.this);
                soundOut.stop();
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        soundOut.stop();
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