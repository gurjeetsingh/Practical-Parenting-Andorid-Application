package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class DeepBreath extends AppCompatActivity {

    //Enum for all the states in state machine
    public enum State {
        WAITING_TO_INHALE, CONTINUE, INHALING, EXHALE, DONE,
    }
    private State breathState = State.WAITING_TO_INHALE;
    //state machine vars
    private static final String EXTRA_NUM_BREATHS = "Extra - Num breaths";
    private static int numBreaths;
    private TextView breathDisplay;
    private TextView currentStateView;
    private Button begin;

    //animation and sound vars
    private ImageView circle;
    private MediaPlayer soundIn = new MediaPlayer();
    private MediaPlayer soundOut = new MediaPlayer();
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
        //begin button
        begin = findViewById(R.id.breath);

        //setup breaths spinner
        createBreathSpinner();
        //begin exercise/state machine
        beginBreathing();

    }

    //spinner to select number of breaths
    private void createBreathSpinner() {
        Spinner breathSpinner = (Spinner) findViewById(R.id.breath_spinner);
        //To get the string array from the Strings.XML
        Resources res = this.getResources();
        String[] breathOptions = res.getStringArray(R.array.breaths_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.breath_num_spinner, breathOptions);
        adapter.setDropDownViewResource(R.layout.breath_num_spinner_dropdown);
        breathSpinner.setAdapter(adapter);

        //set default value/load previously selected value
        if(getNumBreaths() == 0) {
            breathSpinner.setSelection(2);
        }
        else {
            breathSpinner.setSelection(getNumBreaths() - 1);
        }

        breathSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    numBreaths = 1;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 1) {
                    numBreaths = 2;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);

                }
                if (position == 2) {
                    numBreaths = 3;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 3) {
                    numBreaths = 4;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 4) {
                    numBreaths = 5;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 5) {
                    numBreaths = 6;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 6) {
                    numBreaths = 7;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 7) {
                    numBreaths = 8;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 8) {
                    numBreaths = 9;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }
                if (position == 9) {
                    numBreaths = 10;
                    saveTimes();
                    breathDisplay.setText(""+ numBreaths);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numBreaths = 3;
                saveTimes();
            }
        });
    }

    private int getNumBreaths(){
        SharedPreferences prefs = getSharedPreferences("last times", MODE_PRIVATE);
        return prefs.getInt("last times",0);
    }

    private void saveTimes(){
        SharedPreferences prefs = getSharedPreferences("last times", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("last times", numBreaths);
        editor.commit();
        //TODO: is the placement of this ok? @yha181
        Toast.makeText(DeepBreath.this, getString(R.string.hint_for_breath), Toast.LENGTH_LONG)
                .show();
    }


    private void beginBreathing() {
        final Handler handler = new Handler();
        final Runnable afterExhaling = new Runnable() {
            @Override
            public void run() {
                if(numBreaths > 0){
                    if (breathState == State.EXHALE) {
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

        begin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathState == State.WAITING_TO_INHALE || breathState == State.CONTINUE) {
                        changeState(State.INHALING);
                        handler.postDelayed(releaseHint,10000);
                    }
                }

                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getEventTime() - event.getDownTime() < 3000) {
                        if (breathState == State.INHALING) {
                            changeState(State.WAITING_TO_INHALE);
                            soundIn.stop();
                            handler.removeCallbacks(releaseHint);
                        }
                    }
                    else {
                        if (breathState == State.INHALING) {
                            changeState(State.EXHALE);
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
                begin.setText(R.string.good_job);
                //done();
                break;
        }
        breathState = newState;
    }

    private void waitingToInhale() {
        circle.clearAnimation();
        breathDisplay.setText(""+ numBreaths);
    }

    private void continueInhaling(){
        begin.setText(R.string.in);
        Toast.makeText(DeepBreath.this, getString(R.string.hint_for_breath), Toast.LENGTH_LONG)
                .show();
    }

    private void inhaling() {

        begin.setText(R.string.in);
        Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        circle.startAnimation(animationIn);
        if(!soundOut.equals(null))
            soundOut.stop();
        soundIn = MediaPlayer.create(DeepBreath.this, R.raw.sound_in);
        soundIn.start();
    }

    private void exhale() {
        Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        circle.startAnimation(animationOut);
        if(!soundIn.equals(null))
            soundIn.stop();
        soundOut = MediaPlayer.create(DeepBreath.this, R.raw.sound_out);
        soundOut.start();

        begin.setText(R.string.out);
        numBreaths--;
        breathDisplay.setText(""+ numBreaths);
    }

    @Override
    public void onBackPressed() {
        soundOut.stop();
        Intent mainIntent=MainMenu.makeIntent(DeepBreath.this);
        startActivity(mainIntent);
        DeepBreath.this.finish();
    }


    public static Intent makeDeepBreathIntent(Context context) {
        Intent intent = new Intent(context, DeepBreath.class);
        //intent.putExtra(EXTRA_NUM_BREATHS, numBreaths);
        return intent;
    }
}