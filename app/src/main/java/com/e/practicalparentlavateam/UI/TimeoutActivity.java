package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

import java.util.Locale;

public class TimeoutActivity extends AppCompatActivity {

    private long START_TIME_IN_MILLIS = 600000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long selectedtime;
    String[] timepiece = new String[]{"Select Duration", "1", "2","3","5","10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        mTextViewCountDown = findViewById(R.id.timertext);
        mTextViewCountDown.setBackgroundResource(R.color.stopg);
        mButtonStartPause = findViewById(R.id.timepushbtn);
        createtimedurationspinner();
        mButtonReset = findViewById(R.id.resetbtn);
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        updateCountDownText();
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                resetTimer();
               // mButtonStartPause.setVisibility(View.INVISIBLE);
               // mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();
        mTimerRunning = true;
        mButtonStartPause.setText("pause");
       // mButtonReset.setVisibility(View.INVISIBLE);
    }

    public static Intent makeIntent(Context context) {
        Intent timeoutintent = new Intent(context, TimeoutActivity.class);
        return timeoutintent;
    }


    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Resume");
        mButtonReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mTimeLeftInMillis = selectedtime;
        mButtonStartPause.setText("Start");
        updateCountDownText();
       // mButtonReset.setVisibility(View.INVISIBLE);
        //mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / (double)1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / (double) 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);


    }


    private void createtimedurationspinner() {
        Spinner timefieldspinner= (Spinner) findViewById(R.id.timespinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timepiece);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timefieldspinner.setAdapter(adapter);
        timefieldspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    mTimeLeftInMillis=60000;
                    selectedtime=60000;
                    updateCountDownText();
                }
                if(position==1)
                {
                    mTimeLeftInMillis=60000;
                    selectedtime=60000;
                    updateCountDownText();
                }
                if(position==2)
                {
                    mTimeLeftInMillis=120000;
                    selectedtime=120000;
                    updateCountDownText();
                }
                if(position==3)
                { mTimeLeftInMillis=180000;
                    selectedtime=180000;
                    updateCountDownText();}
                if(position==4)
                { mTimeLeftInMillis=300000;
                    selectedtime=300000;
                    updateCountDownText();}
                if(position==5)
                { mTimeLeftInMillis=600000;
                    selectedtime=600000;
                    updateCountDownText();}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTimeLeftInMillis=60000;
            }
        });


    }

}