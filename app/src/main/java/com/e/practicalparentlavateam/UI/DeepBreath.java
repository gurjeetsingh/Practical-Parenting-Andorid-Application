/*
IT is the activity for the deep breath
*/
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

    //https://stackoverflow.com/questions/25581176/java-enum-based-state-machine-fsm-passing-in-events
    //Enum for states in state machine
    public enum State {
        WAITING_TO_INHALE, INHALING, EXHALE, DONE,
    }
    private State breathState = State.WAITING_TO_INHALE;

    //state machine vars
    private static final String EXTRA_NUM_BREATHS = "Extra - Num breaths";
    private static int numBreaths;
    private TextView breathDisplay;
    private TextView currentStateView;
    private Button breath;
    private Button begin;
    private Spinner breathSpinner;

    //animation and sound vars
    private ImageView circleIn;
    private ImageView circleOut;
    private MediaPlayer soundIn = new MediaPlayer();
    private MediaPlayer soundOut = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_breath);

        //https://commons.wikimedia.org/wiki/File:Small-dark-green-circle.svg
        circleIn = findViewById(R.id.circleIn);
        circleOut = findViewById(R.id.circleOut);
        circleOut.setVisibility(View.INVISIBLE);

        //initialize display of breaths
        breathDisplay = findViewById(R.id.num_breaths_rem);

        //display of state machine
        currentStateView = findViewById(R.id.state);
        //breath button
        breath = findViewById(R.id.breath);
        //begin button
        begin = findViewById(R.id.begin);

        //begin breath process
        readyToBreath();
        //setup breaths spinner
        createBreathSpinner();
        //begin exercise/state machine
        breathing();
    }

    private void readyToBreath() {
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeepBreath.this, getString(R.string.hint_for_breath), Toast.LENGTH_SHORT)
                        .show();
                begin.setVisibility(View.INVISIBLE);
                breathSpinner.setVisibility(View.INVISIBLE);
            }
        });
    }

    //spinner to select number of breaths
    private void createBreathSpinner() {
        breathSpinner = (Spinner) findViewById(R.id.breath_spinner);
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
    }

    //This is the main part to do the breath activity///////////////////////////////
    private void breathing() {
        final Handler handler = new Handler();

        //This is the function to judge the state should be after exhaling for 3s
        final Runnable afterExhaling = new Runnable() {
            @Override
            public void run() {
                numBreaths--;
                breathDisplay.setText(""+ numBreaths);
                if(numBreaths > 0){
                    if (breathState == State.EXHALE) {
                        changeState(State.WAITING_TO_INHALE);

                    }
                }
                else if(numBreaths == 0){
                    if (breathState == State.EXHALE) {
                        changeState(State.DONE);
                    }
                }
            }
        };

        //hint for if breath for 10s
        final Runnable releaseHint = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DeepBreath.this, getString(R.string.hint_for_release), Toast.LENGTH_SHORT)
                        .show();
            }
        };

        //change the button message if inhale for 3s
        final Runnable changeButton = new Runnable() {
            @Override
            public void run() {
                breath.setText(R.string.out);
            }
        };

        breath.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (breathState == State.WAITING_TO_INHALE) {
                        changeState(State.INHALING);
                        //hint for if breath for 10s
                        handler.postDelayed(releaseHint,10000);
                        //change the button message if inhale for 3s
                        handler.postDelayed(changeButton,3000);
                    }
                }

                //Release the button
                if(event.getAction() == MotionEvent.ACTION_UP){

                    //if not hold for 3s
                    if(event.getEventTime() - event.getDownTime() < 3000) {
                        if (breathState == State.INHALING) {
                            changeState(State.WAITING_TO_INHALE);
                            soundIn.stop();
                            handler.removeCallbacks(releaseHint);
                            handler.removeCallbacks(changeButton);
                        }
                    }

                    //start exhaling
                    else {
                        if (breathState == State.INHALING) {
                            changeState(State.EXHALE);

                            //After exhaling 3s change the state
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

    //This is the function to change the state of the breathing
    private void changeState(State newState) {
        currentStateView.setText(newState.name());

        switch (newState) {
            case WAITING_TO_INHALE:
                currentStateView.setText(R.string.waiting_to_inhale);
                waitingToInhale();
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
                //If all the # of breath is used then stop
                currentStateView.setText(R.string.finish);
                breath.setText(R.string.good_job);
                //done();
                break;
        }
        breathState = newState;
    }

    //Those are the function to use for the state of breath
    private void waitingToInhale() {
        breath.setText(R.string.in);
        circleIn.clearAnimation();
        breathDisplay.setText(""+ numBreaths);
        Toast.makeText(DeepBreath.this, getString(R.string.hint_for_breath), Toast.LENGTH_SHORT)
                .show();
    }

    private void inhaling() {
        breath.setText(R.string.in);
        circleIn.setVisibility(View.VISIBLE);
        //https://www.youtube.com/watch?v=jHT9QQf4_Pw&t=509s
        Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        circleOut.clearAnimation();
        circleOut.setVisibility(View.INVISIBLE);
        circleIn.startAnimation(animationIn);
        if(!soundOut.equals(null))
            soundOut.stop();
        soundIn = MediaPlayer.create(DeepBreath.this, R.raw.sound_in);
        soundIn.start();
    }

    private void exhale() {
        circleOut.setVisibility(View.VISIBLE);
        Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        circleIn.clearAnimation();
        circleIn.setVisibility(View.INVISIBLE);
        circleOut.startAnimation(animationOut);
        if(!soundIn.equals(null))
            soundIn.stop();
        soundOut = MediaPlayer.create(DeepBreath.this, R.raw.sound_out);
        soundOut.start();

        breath.setText(R.string.out);
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