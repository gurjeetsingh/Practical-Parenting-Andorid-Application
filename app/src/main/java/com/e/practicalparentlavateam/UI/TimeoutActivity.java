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
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import com.e.practicalparentlavateam.Model.AudioController;
import com.e.practicalparentlavateam.Model.TimeService;
import com.e.practicalparentlavateam.R;

import org.w3c.dom.Text;

import java.util.Locale;

import static java.lang.Math.round;

public class TimeoutActivity extends AppCompatActivity {


    private Button startButton;
    private Button pauseButton;
    private Button resetButton;
    private Button customButton;
    private Button alarmOffButton;
    EditText userTime;
    private boolean isTimerRunning = false;
    private long timeLeftInMilliSeconds;
    private long selectedTime;
    private ProgressBar progressBar;
    private TextView progressText;
    double timeo=0;

    int endTimeFlag=0;
    long endTime;
    long systime;
    double progresstimepercent;
    boolean ispaused=false;
    int timefactor=1;

    Context context = this;



    private TextView timerValue;
    Intent pauseIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        alarmOffButton = (Button) findViewById(R.id.alarm_off);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timeout_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        createTimeDurationSpinner();
        createTimeFactorSpinner();


        timerValue = (TextView) findViewById(R.id.timer_text);
        timerValue.setBackgroundResource(R.color.stopg);
        //For the progress piechart
        progressBar=findViewById(R.id.circular_progress_bar);
        progressText=findViewById(R.id.progressText);


        /*
        This allows us to use the clickback from the notification box to stop the alarm.
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
            }
            else if (extras.getBoolean("StopAlarm")) {
                AudioController.stopAudio();
                finish();
            }

        }


        if (isTimerRunning == false) {
            millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
        }

        setupStartButton();
        setupPauseButton();
        setupCustomButton();
        setupAlarmOffButton();
        setupResetButton();

    }



    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadCastReceiver, new IntentFilter(TimeService.TIME_BROADCAST));
    }

    /*
    The broadcast reciever, whenever it recieves information from the service,which is the
    time remaining in milliseconds, it immediately updates the UI.
     */
    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
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

        if(endTimeFlag==0)
        {
            final int time = intent.getIntExtra("time", 0);
            systime=selectedTime;
            endTimeFlag++;
        }
            final int time = intent.getIntExtra("time", 0);
            double elapsedtime = intent.getDoubleExtra("elap", 0);
            endTime = intent.getLongExtra("endtime", 0);
            int mins = (int) (time / (double) 1000) / 60;
            int secs = (int) (time / (double) 1000) % 60;
            if (time == 0 || time < 0) {
                isTimerRunning = false;
                alarmOffButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.INVISIBLE);
                endTimeFlag = 0;
                notIf();
                //Added vibrator
                Vibrator alarm = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                alarm.vibrate(5000);

                //This handler is for removing the alarmoff button after a period of time
                Handler cancelNotificiaton = new Handler();
                long delay = 12000;
                cancelNotificiaton.postDelayed(new Runnable() {
                    public void run() {
                        alarmOffButton.setVisibility(View.INVISIBLE);
                    }
                }, delay);

            }

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
            timerValue.setText(timeLeftFormatted);
            pauseIntent = intent;


            if (time == 0 || time < 0) {
                progressBar.setProgress(0);
                progressText.setText("0");
            } else {
                System.out.println("This is the selected time:" + systime);
                System.out.println("This is the elapsed time" + elapsedtime);
                progresstimepercent = (((systime / 1000) - elapsedtime) / (systime / 1000)) * 100;
                Math.ceil(progresstimepercent);
                System.out.println(progresstimepercent);
                //totalpercent= (int) (progresstimepercent+totalpercent);
                String totper = Integer.toString((int) progresstimepercent);
                progressBar.setProgress((int) progresstimepercent);
                if (elapsedtime < 0) {
                    progressText.setText("100");
                } else {
                    progressText.setText(totper);
                }
            }
        }



    /*
    The following method implements our notification, which takes sends an intent to the TimeoutActivity
    and calls the AudioManager class to stop audio, once the notification box is clicked.

    Resources used to learn make a notification:https://developer.android.com/training/notify-user/build-notification
    Another resource: https://developer.android.com/guide/topics/ui/notifiers/notifications
     */
    public void notIf()
    {
                Intent intent = new Intent(this, TimeoutActivity.class);
        intent.putExtra("StopAlarm",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification AlarmNotify;
        AlarmNotify = new Notification.Builder(this)
                .setContentTitle(getString(R.string.time_up))
                .setContentText(getString(R.string.hint_for_turn_off))
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

    @Override
    protected void onPause() {
        super.onPause();
    }


    /*
    The following method creates a spinner, where it takes an array of data, and allows you to choose options
    quickly to set a duration of time. Each position you select is going to be displayed on the timer
    until you click on start timer. Whenever you click on an option, the service will be stopped first,and the
    time will be sent to the UI updater. Then, you can START TIMER to restart the timer with the set duration.
     */
    private void createTimeDurationSpinner() {
        Spinner timeFieldSpinner = (Spinner) findViewById(R.id.time_spinner);
        //To get the string array from the Strings.XML
        Resources res = this.getResources();
        String[] timePiece = res.getStringArray(R.array.minutes_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, timePiece);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFieldSpinner.setAdapter(adapter);
        timeFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    timeLeftInMilliSeconds = 60000;
                    selectedTime = 60000;
                    //millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 1) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeLeftInMilliSeconds = 120000;
                    selectedTime = 120000;
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);

                }
                if (position == 2) {
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceIntent);
                    timeLeftInMilliSeconds = 180000;
                    selectedTime = 180000;
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 3) {
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceIntent);
                    timeLeftInMilliSeconds = 300000;
                    selectedTime = 300000;
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 4) {
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceIntent);
                    timeLeftInMilliSeconds = 600000;
                    selectedTime = 600000;
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timeLeftInMilliSeconds = selectedTime;
            }
        });
    }

    private void createTimeFactorSpinner() {
        final Spinner timeFieldSpinner = (Spinner) findViewById(R.id.factorspinner);
        //To get the string array from the Strings.XML
        Resources res = this.getResources();
        String[] timePiece = res.getStringArray(R.array.timefactor);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, timePiece);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFieldSpinner.setAdapter(adapter);
        timeFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                  timefactor=1;
                }
                if (position == 1) {
                   timefactor=2;
                    System.out.println("25%");
                }
                if (position == 2) {
                    timefactor=3;
                }
                if (position == 3) {
                    timefactor=4;
                }
                if (position == 4) {
                    timefactor = 5;
                }
                if(position==5){
                    timefactor=6;
                }
                if(position==6){
                    timefactor=7;
                }
                }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timefactor=1;
            }
        });
    }


    //The following function streamlines our updating the textview to easily convert
    //from seconds to milliseconds.
    public void millisecondConverterAndTimerUIupdate(long selectedtime, TextView usertext)
    {
        int mins = (int) (selectedtime / (double) 1000) / 60;
        int secs = (int) (selectedtime / (double) 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        usertext.setText(timeLeftFormatted);
    }

    /*
  The start button starts the background service by calling from counterservice, to run time.
   */
    private void setupStartButton() {

        startButton = (Button) findViewById(R.id.time_push_button);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                serviceIntent.putExtra("mills", timeLeftInMilliSeconds);
                serviceIntent.putExtra("factor",timefactor);
                selectedTime=timeLeftInMilliSeconds;
                isTimerRunning = true;
                //ispaused=false;
                startService(serviceIntent);
                //System.out.println("Time left for start" + mTimeLeftInMillis);
                registerReceiver(broadCastReceiver, new IntentFilter(TimeService.TIME_BROADCAST));
                pauseButton.setVisibility(View.VISIBLE);
            }
        });
    }

    /*
 Pause button stops the service temporarily storing the time left in milliseconds.
  */
    private void setupPauseButton() {

        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int time = pauseIntent.getIntExtra("time", 0);
                timeLeftInMilliSeconds = time;
                endTimeFlag=0;
                isTimerRunning = false;
               // ispaused=true;

                Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceIntent);
                // unregisterReceiver(broadcastReceiver);
                pauseButton.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeout, menu);
        return true;
    }

    private void setupCustomButton() {
                /*
        The CUSTOMBUTTON uses an alertdialog to quickly allow us to set a custom time.
         */
        customButton = (Button) findViewById(R.id.custom_time);
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceIntent);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
                builder.setTitle(R.string.enter_custom_minute);
                userTime = new EditText(context);
                builder.setView(userTime);
                builder.setIcon(R.drawable.babyclock);
                builder.setPositiveButton(R.string.finish_set_time, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTime = userTime.getText().toString();
                        double time = Double.parseDouble(newTime);
                        long customTime = Long.parseLong("" + Math.round(time));
                        timeLeftInMilliSeconds = customTime * 60000;
                        selectedTime = timeLeftInMilliSeconds;
                        millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /*
      To turn off alarm at the push of a button using the AudioController function.
       */
    private void setupAlarmOffButton() {

        alarmOffButton = (Button) findViewById(R.id.alarm_off);
        alarmOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.stopAudio();
                alarmOffButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupResetButton() {
                /*
        The RESETBUTTON stops our service, and reverts back to our last chosen time. Also
        makes our pause button invisible as the timer is automatically paused.
         */

        resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceIntent);
                timeLeftInMilliSeconds = selectedTime;
                pauseButton.setVisibility(View.INVISIBLE);
                isTimerRunning = false;
                //ispaused=false;
                progressBar.setProgress(100);
                progressText.setText("100");
                endTimeFlag=0;


                millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
            }


        });
    }


    /*
    For making the intent
     */
    public static Intent makeIntent(Context context) {
        Intent timeoutIntent = new Intent(context, TimeoutActivity.class);
        return timeoutIntent;
    }

}
