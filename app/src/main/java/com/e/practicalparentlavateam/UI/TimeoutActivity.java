/*This is the activity for the timer
* Here, the timeout activity is  defined and
* layout is used to make the timer run. It
* is heavily reliant on the TimeService to
* keep timer alive even if app is closed.
* */

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
import android.os.Handler;
import android.os.Vibrator;
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
import com.e.practicalparentlavateam.Model.TimeService;
import com.e.practicalparentlavateam.R;

import java.util.Locale;

public class TimeoutActivity extends AppCompatActivity {


    private Button startButton;
    private Button pauseButton;
    private Button resetbutton;
    private Button custombtn;
    private Button alrmoffbtn;
    EditText usertime;
    private boolean istimerrunning = false;
    private long timeleftinmilliseconds;
    private long selectedtime;
    Context context = this;
    String[] timepiece = new String[]{"Select Duration", "Set Time: 1 Minute", "Set Time: 2 Minutes", "Set Time: 3 Minutes", "Set Time: 5 Minutes", "Set Time: 10 Minutes"};


    private TextView timerValue;
    Intent pauseintent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        alrmoffbtn = (Button) findViewById(R.id.alarmoff);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.timeoutToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        createtimedurationspinner();


        timerValue = (TextView) findViewById(R.id.timertext);
        timerValue.setBackgroundResource(R.color.stopg);

        /*
        This allows us to use the clickback from the notification box to stop the alarm.
         */
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
            millisecondconverterandTimerUIupdate(selectedtime,timerValue);
        }


        /*
        The start button starts the background service by calling from counterservice, to run time.
         */
        startButton = (Button) findViewById(R.id.timepushbtn);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                serviceintent.putExtra("mills", timeleftinmilliseconds);
                istimerrunning = true;
                startService(serviceintent);
                //System.out.println("Time left for start" + mTimeLeftInMillis);
                registerReceiver(broadcastReceiver, new IntentFilter(TimeService.TIME_BROADCAST));
                pauseButton.setVisibility(View.VISIBLE);
            }
        });



        /*
        Pause button stops the service temporarily storing the time left in milliseconds.
         */
        pauseButton = (Button) findViewById(R.id.pausebtn);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int time = pauseintent.getIntExtra("time", 0);
                timeleftinmilliseconds = time;
                istimerrunning = false;
                Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceintent);
                // unregisterReceiver(broadcastReceiver);
                pauseButton.setVisibility(View.INVISIBLE);

            }
        });



        /*
        The CUSTOMBUTTON uses an alertdialog to quickly allow us to set a custom time.
         */
        custombtn = (Button) findViewById(R.id.customtime);
        custombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceintent);
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
                        timeleftinmilliseconds = customtime * 60000;
                        selectedtime = timeleftinmilliseconds;
                        millisecondconverterandTimerUIupdate(selectedtime,timerValue);
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



        /*
        To turn off alarm at the push of a button using the AudioController function.
         */
        alrmoffbtn = (Button) findViewById(R.id.alarmoff);
        alrmoffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.stopAudio();
                alrmoffbtn.setVisibility(View.INVISIBLE);
            }
        });



        /*
        The RESETBUTTON stops our service, and reverts back to our last chosen time. Also
        makes our pause button invisible as the timer is automatically paused.
         */

        resetbutton = (Button) findViewById(R.id.resetbtn);
        resetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceintent);
                timeleftinmilliseconds = selectedtime;
                pauseButton.setVisibility(View.INVISIBLE);
                istimerrunning = false;
                millisecondconverterandTimerUIupdate(selectedtime,timerValue);
            }


        });



    }


    /*
    The following method creates a spinner, where it takes an array of data, and allows you to choose options
    quickly to set a duration of time. Each position you select is going to be displayed on the timer
    until you click on start timer. Whenever you click on an option, the service will be stopped first,and the
    time will be sent to the UI updater. Then, you can START TIMER to restart the timer with the set duration.
     */
    private void createtimedurationspinner() {
        Spinner timefieldspinner = (Spinner) findViewById(R.id.timespinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, timepiece);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timefieldspinner.setAdapter(adapter);
        timefieldspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    timeleftinmilliseconds = 60000;
                    selectedtime = 60000;
                }
                if (position == 1) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeleftinmilliseconds = 60000;
                    selectedtime = 60000;
                    millisecondconverterandTimerUIupdate(selectedtime,timerValue);

                }
                if (position == 2) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeleftinmilliseconds = 120000;
                    selectedtime = 120000;
                    millisecondconverterandTimerUIupdate(selectedtime,timerValue);
                }
                if (position == 3) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeleftinmilliseconds = 180000;
                    selectedtime = 180000;
                    millisecondconverterandTimerUIupdate(selectedtime,timerValue);
                }
                if (position == 4) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeleftinmilliseconds = 300000;
                    selectedtime = 300000;
                    millisecondconverterandTimerUIupdate(selectedtime,timerValue);
                }
                if (position == 5) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeleftinmilliseconds = 600000;
                    selectedtime = 600000;
                    millisecondconverterandTimerUIupdate(selectedtime,timerValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timeleftinmilliseconds = selectedtime;
            }
        });
    }
    


    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimeService.TIME_BROADCAST));
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
            alrmoffbtn.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.INVISIBLE);
            notif();

            //This handler is for removing the alarmoff button after a period of time
            Handler cancelnotificiaton = new Handler();
            long delay = 12000;
            cancelnotificiaton.postDelayed(new Runnable() {
                public void run() {
                    alrmoffbtn.setVisibility(View.INVISIBLE);
                }
            }, delay);

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

    Resources used to learn make a notification:https://developer.android.com/training/notify-user/build-notification
    Another resource: https://developer.android.com/guide/topics/ui/notifiers/notifications
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

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, AlarmNotify);


        //To turn off the notification bar after some time:
        Handler cancelnotificiaton = new Handler();
        long delay = 12000;
        cancelnotificiaton.postDelayed(new Runnable() {
            public void run() {
                notificationManager.cancel(0);
            }
        }, delay);
    }


    //The following function streamlines our updating the textview to easily convert
    //from seconds to milliseconds.
    public void millisecondconverterandTimerUIupdate(long selectedtime, TextView usertext)
    {
        int mins = (int) (selectedtime / (double) 1000) / 60;
        int secs = (int) (selectedtime / (double) 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        usertext.setText(timeLeftFormatted);
    }

    }
