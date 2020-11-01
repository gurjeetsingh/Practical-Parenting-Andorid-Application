package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private Button custombtn;
    EditText usertime;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private long mEndTime;
    private long selectedtime;
    final static int RQS_1 = 1;
    final Context context=this;
    String[] timepiece = new String[]{"Select Duration", "Set Time: 1 Minute", "Set Time: 2 Minutes","Set Time: 3 Minutes","Set Time: 5 Minutes","Set Time: 10 Minutes"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        mTextViewCountDown = findViewById(R.id.timertext);
        mTextViewCountDown.setBackgroundResource(R.color.stopg);
        mButtonStartPause = findViewById(R.id.timepushbtn);
        custombtn=findViewById(R.id.customtime);
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


        custombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.MyDialogTheme);
                builder.setTitle("Please Enter Custom Minute:");
                usertime=new EditText(context);
                builder.setView(usertime);
                builder.setIcon(R.drawable.babyclock);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newtime=usertime.getText().toString();
                        long customtime = Long.parseLong(newtime);
                        mTimeLeftInMillis=customtime*60000;
                        selectedtime=mTimeLeftInMillis;
                        updateCountDownText();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog ad=builder.create();
                ad.show();
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

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", selectedtime);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateButtons();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateButtons();
            } else {
                startTimer();
            }
        }
    }


    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
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

    private void updateButtons() {
        if (mTimerRunning) {
            //mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        } else {
            mButtonStartPause.setText("Start");
            /*
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);


            }
            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
             */
        }
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
                    if(mTimerRunning==true)
                    {
                       // Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mTimeLeftInMillis = 60000;
                        selectedtime = 60000;
                        updateCountDownText();
                    }
                }
                if(position==1)
                {
                    if(mTimerRunning==true)
                    {
                        Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mTimeLeftInMillis = 60000;
                        selectedtime = 60000;
                        updateCountDownText();
                    }
                }
                if(position==2)
                {
                    if(mTimerRunning==true)
                    {
                        Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mTimeLeftInMillis = 120000;
                        selectedtime = 120000;
                        updateCountDownText();
                    }
                }
                if(position==3) {
                    if (mTimerRunning == true) {
                        Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                    } else {
                        mTimeLeftInMillis = 180000;
                        selectedtime = 180000;
                        updateCountDownText();
                    }
                }
                if(position==4)
                {
                    if(mTimerRunning==true)
                    {
                        Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mTimeLeftInMillis=300000;
                        selectedtime=300000;
                        updateCountDownText();
                    }

                    }
                if(position==5) {
                    if (mTimerRunning == true) {
                        Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mTimeLeftInMillis = 600000;
                        selectedtime = 600000;
                        updateCountDownText();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTimeLeftInMillis=60000;
            }
        });


    }

}