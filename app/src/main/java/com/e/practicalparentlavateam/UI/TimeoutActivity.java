package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.e.practicalparentlavateam.Model.AudioController;
import com.e.practicalparentlavateam.Model.CounterService;
import com.e.practicalparentlavateam.R;

import java.util.Locale;

public class TimeoutActivity extends AppCompatActivity {


    private Button startButton;
    private Button pauseButton;
    private Button resetbutton;
    private Button custombtn;
    private Button alrmoffbtn;
    private long START_TIME_IN_MILLIS = 10000;
    EditText usertime;
    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_1 = "action_1";
    private CountDownTimer mCountDownTimer;
    private boolean istimerrunning = false;
    private long mTimeLeftInMillis;
    private long selectedtime;
    Context context = this;
    private final static String TAG = "BroadcastService";
    String[] timepiece = new String[]{"Select Duration", "Set Time: 1 Minute", "Set Time: 2 Minutes", "Set Time: 3 Minutes", "Set Time: 5 Minutes", "Set Time: 10 Minutes"};


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

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
            }
            else if (extras.getBoolean("StopAlarm"))
            {
                AudioController.stopAudio();
                finish();
            }

        }


        if (istimerrunning == false) {
            int mins = (int) (selectedtime / (double) 1000) / 60;
            int secs = (int) (selectedtime / (double) 1000) % 60;
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
            timerValue.setText(timeLeftFormatted);
        }


        startButton = (Button) findViewById(R.id.timepushbtn);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                serviceintent.putExtra("mills", mTimeLeftInMillis);
                istimerrunning = true;
                startService(serviceintent);
                System.out.println("Time left for start" + mTimeLeftInMillis);
                registerReceiver(broadcastReceiver, new IntentFilter(CounterService.BROADCAST_ACTION));
                pauseButton.setVisibility(View.VISIBLE);
            }
        });

        pauseButton = (Button) findViewById(R.id.pausebtn);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                int time = pauseintent.getIntExtra("time", 0);
                mTimeLeftInMillis = time;
                istimerrunning = false;
                //System.out.println("Time left for pause"+mTimeLeftInMillis);
                Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                stopService(serviceintent);
                // unregisterReceiver(broadcastReceiver);
                pauseButton.setVisibility(View.INVISIBLE);

            }
        });

        custombtn = (Button) findViewById(R.id.customtime);
        custombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                builder.setTitle("Please Enter Custom Minute:");
                usertime = new EditText(context);
                builder.setView(usertime);
                builder.setIcon(R.drawable.babyclock);
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newtime = usertime.getText().toString();
                        long customtime = Long.parseLong(newtime);
                        mTimeLeftInMillis = customtime * 60000;
                        selectedtime = mTimeLeftInMillis;

                        int mins = (int) (selectedtime / (double) 1000) / 60;
                        int secs = (int) (selectedtime / (double) 1000) % 60;
                        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                        timerValue.setText(timeLeftFormatted);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
        });

        alrmoffbtn = (Button) findViewById(R.id.alarmoff);
        alrmoffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.stopAudio();
            }
        });

        resetbutton = (Button) findViewById(R.id.resetbtn);
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                stopService(serviceintent);
                mTimeLeftInMillis = selectedtime;
                pauseButton.setVisibility(View.INVISIBLE);
                istimerrunning = false;
                int mins = (int) (mTimeLeftInMillis / (double) 1000) / 60;
                int secs = (int) (mTimeLeftInMillis / (double) 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                timerValue.setText(timeLeftFormatted);
            }


        });



    }

    private void createtimedurationspinner() {
        Spinner timefieldspinner = (Spinner) findViewById(R.id.timespinner);

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
                    Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                    stopService(serviceintent);
                    mTimeLeftInMillis = 5000;
                    selectedtime = 5000;
                    int mins = (int) (selectedtime / (double) 1000) / 60;
                    int secs = (int) (selectedtime / (double) 1000) % 60;
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                    timerValue.setText(timeLeftFormatted);
                }
                if (position == 2) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                    stopService(serviceintent);
                    mTimeLeftInMillis = 120000;
                    selectedtime = 120000;
                    int mins = (int) (selectedtime / (double) 1000) / 60;
                    int secs = (int) (selectedtime / (double) 1000) % 60;
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                    timerValue.setText(timeLeftFormatted);
                }
                if (position == 3) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                    stopService(serviceintent);
                    mTimeLeftInMillis = 180000;
                    selectedtime = 180000;
                    int mins = (int) (selectedtime / (double) 1000) / 60;
                    int secs = (int) (selectedtime / (double) 1000) % 60;
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                    timerValue.setText(timeLeftFormatted);
                }
                if (position == 4) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                    stopService(serviceintent);
                    mTimeLeftInMillis = 300000;
                    selectedtime = 300000;
                    int mins = (int) (selectedtime / (double) 1000) / 60;
                    int secs = (int) (selectedtime / (double) 1000) % 60;
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                    timerValue.setText(timeLeftFormatted);
                }
                if (position == 5) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, CounterService.class);
                    stopService(serviceintent);
                    mTimeLeftInMillis = 600000;
                    selectedtime = 600000;
                    int mins = (int) (selectedtime / (double) 1000) / 60;
                    int secs = (int) (selectedtime / (double) 1000) % 60;
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
                    timerValue.setText(timeLeftFormatted);
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

    /*
    The broadcast reciever, whenever it recieves information from the service,which is the
    time remaining in milliseconds, it immediately updates the UI.
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateUI(intent);
        }
    };

    /*
    This updates the UI whenever user is viewing the activity. It gets the information
    from the counterservice. When the time goes to 0 or below 0, a notification is sent
    to the phone immediately.
     */
    private void updateUI(Intent intent) {
        int time = intent.getIntExtra("time", 0);
        int mins = (int) (time / (double) 1000) / 60;
        int secs = (int) (time / (double) 1000) % 60;
        if (time == 0 || time < 0) {
            istimerrunning = false;
            notif();
        }

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        timerValue.setText(timeLeftFormatted);
        pauseintent = intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeout, menu);
        return true;
    }


    /*
    For making the intent
     */
    public static Intent makeIntent(Context context) {
        Intent timeoutintent = new Intent(context, TimeoutActivity.class);
        return timeoutintent;
    }


    /*
    The following method implements our notification, which takes sends an intent to the TimeoutActivity
    and calls the AudioManager class to stop audio, once the notification box is clicked.
     */
    public void notif()
    {
        Intent intent = new Intent(this, TimeoutActivity.class);
        intent.putExtra("StopAlarm",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification AlarmNotify;
        AlarmNotify = new Notification.Builder(this)
                .setContentTitle("TIME'S UP!")
                .setContentText("Please Click Box To Turn Off Alarm.")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.babyclock))
                .setContentIntent(pIntent)
                .setColor(getResources().getColor(R.color.appcolr))
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, AlarmNotify);
    }

    }
