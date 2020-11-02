package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.view.Menu;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.e.practicalparentlavateam.Model.AudioPlay;
import com.e.practicalparentlavateam.Model.CounterService;
import com.e.practicalparentlavateam.R;

import java.util.Locale;

public class TimeoutActivity extends AppCompatActivity {


    private TextView mTextViewCountDown;
    private Button startButton;
    private Button pauseButton;
    private Button resetbutton;
    private Button custombtn;
    private Button alrmoffbtn;
    private long START_TIME_IN_MILLIS = 10000;
    EditText usertime;
    private CountDownTimer mCountDownTimer;
    private boolean istimerrunning=false;
    private long mTimeLeftInMillis;
    private long selectedtime;
    Context context = this;
    private final static String TAG = "BroadcastService";
    String[] timepiece = new String[]{"Select Duration", "Set Time: 1 Minute", "Set Time: 2 Minutes","Set Time: 3 Minutes","Set Time: 5 Minutes","Set Time: 10 Minutes"};




    private TextView timerValue;

    Intent intent;
    Intent pauseintent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.timeoutToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        createtimedurationspinner();

        timerValue = (TextView) findViewById(R.id.timertext);
        timerValue.setBackgroundResource(R.color.stopg);


        if(istimerrunning==false)
        {
            int mins = (int) (selectedtime/ (double)1000) / 60;
            int secs = (int) (selectedtime/ (double) 1000) % 60;
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
            timerValue.setText(timeLeftFormatted);
        }


        startButton = (Button) findViewById(R.id.timepushbtn);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                serviceintent.putExtra("mills", mTimeLeftInMillis);
                istimerrunning=true;
                startService(serviceintent);
                System.out.println("Time left for start"+mTimeLeftInMillis);
                registerReceiver(broadcastReceiver, new IntentFilter(CounterService.BROADCAST_ACTION));
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        pauseButton = (Button) findViewById(R.id.pausebtn);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                    int time = pauseintent.getIntExtra("time", 0);
                    mTimeLeftInMillis = time;
                    istimerrunning=false;
                //System.out.println("Time left for pause"+mTimeLeftInMillis);
                    Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                    stopService(serviceintent);
                   // unregisterReceiver(broadcastReceiver);
                pauseButton.setVisibility(View.INVISIBLE);

            }
        });

        custombtn= (Button) findViewById(R.id.customtime);
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

        alrmoffbtn=(Button) findViewById(R.id.alarmoff);
        alrmoffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioPlay.stopAudio();
            }
        });

        resetbutton=(Button) findViewById(R.id.resetbtn);
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                stopService(serviceintent);
                mTimeLeftInMillis = selectedtime;
                istimerrunning=false;
                int mins = (int) (mTimeLeftInMillis/ (double)1000) / 60;
                int secs = (int) (mTimeLeftInMillis / (double) 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                timerValue.setText(timeLeftFormatted);
            }
        });


    }

    private void createtimedurationspinner() {
        Spinner timefieldspinner= (Spinner) findViewById(R.id.timespinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timepiece);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timefieldspinner.setAdapter(adapter);
        timefieldspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mTimeLeftInMillis = 60000;
                    selectedtime = 60000;
                    // Toast.makeText(TimeoutActivity.this, "Please Reset Timer First.", Toast.LENGTH_SHORT).show();
                }
                if (position == 1) {
                    mTimeLeftInMillis = 60000;
                    selectedtime = 60000;
                }
                if (position == 2) {
                    mTimeLeftInMillis = 120000;
                    selectedtime = 120000;
                }
                if (position == 3) {
                    mTimeLeftInMillis = 180000;
                    selectedtime = 180000;
                }
                if (position == 4) {
                    mTimeLeftInMillis = 300000;
                    selectedtime = 300000;
                }
                if (position == 5) {
                    mTimeLeftInMillis = 600000;
                    selectedtime = 600000;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTimeLeftInMillis = selectedtime;
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        //Intent serviceIntent = new Intent(this, CounterService.class);
        //startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(CounterService.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        int time = intent.getIntExtra("time", 0);
        int mins = (int) (time/ (double)1000) / 60;
        int secs = (int) (time / (double) 1000) % 60;
        if(time==0||time<0)
        {
            istimerrunning=false;
        }

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        timerValue.setText(timeLeftFormatted);
        pauseintent=intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeout, menu);
        return true;
    }




    public static Intent makeIntent(Context context) {
        Intent timeoutintent = new Intent(context, TimeoutActivity.class);
        return timeoutintent;
    }
}