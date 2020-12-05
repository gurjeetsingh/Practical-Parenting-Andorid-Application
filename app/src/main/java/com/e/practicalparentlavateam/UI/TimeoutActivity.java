/*This is the activity for the timer
 * Here, the timeout activity is  defined and
 * layout is used to make the timer run. It
 * is heavily reliant on the TimeService to
 * keep timer alive even if app is closed.
 * add the vis-bar and speed up
 * */

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;


import com.e.practicalparentlavateam.Model.AudioController;
import com.e.practicalparentlavateam.Model.NotificationReciever;
import com.e.practicalparentlavateam.Model.TimeService;
import com.e.practicalparentlavateam.R;

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
    private TextView timeFactorText;
    int endTimeFlag=0;
    long endTime;
    long systemEndTime;
    double progressTimePercent;
    boolean ispaused=false;
    int timeFactor=1;
    int selectedTimeForPause;
    double totalElapsed =0;
    Intent requiredIntent;
    Spinner timeFieldSpinner;
    Spinner timeDurationSpinner;
    private boolean timerForceReset;
    boolean resetChecker=false;

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
        timeFactorText=findViewById(R.id.timefactor2);
        createTimeFactorSpinner();


        timerValue = (TextView) findViewById(R.id.timer_text);
        timerValue.setBackgroundResource(R.color.stopg);
        //For the progress piechart
        progressBar=findViewById(R.id.circular_progress_bar);
        progressText=findViewById(R.id.progressText);
        createNotificationChannel();



        /*
        This allows us to use the clickback from the notification box to stop the alarm.
         */
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
            }
            else if (extras.getBoolean("StopAlarm")) {
                AudioController.stopAudio();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTimerRunning){
            pauseButton.setVisibility(View.VISIBLE);
        }

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

    private void setLatestEndTime(int restime){
        SharedPreferences settings = getSharedPreferences("endpref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("endtime",restime);
        editor.apply();

    }
    private int getLatestEndTime()
    {
        SharedPreferences settings = getSharedPreferences("endpref", 0);
        int newtime = settings.getInt("endtime", 0);
        return newtime;
    }

    /*
    This updates the UI whenever user is viewing the activity. It gets the information
    from the counterservice. When the time goes to 0 or below 0, a notification is sent
    to the phone immediately.
     */
    private void updateUI(Intent intent) {

        if(endTimeFlag==0)
        {
            final int time = intent.getIntExtra("time", 0);

            if(getLatestEndTime()!=0)
            {
                systemEndTime =getLatestEndTime();
            }
            else
            {
                setLatestEndTime(time);
                systemEndTime=getLatestEndTime();
            }

            endTimeFlag++;
        }
        final int time = intent.getIntExtra("time", 0);
        double elapsedtime = intent.getDoubleExtra("elap", 0);
       // setElapsedTime((int)elapsedtime);
        endTime = intent.getLongExtra("endtime", 0);
        int mins = (int) (time / (double) 1000) / 60;
        int secs = (int) (time / (double) 1000) % 60;
        if (time == 0 || time < 0)

        {
            isTimerRunning = false;
            createNotificationChannel();
            totalElapsed =0;
            setElapsedTime(0);
            alarmOffButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.INVISIBLE);
            timeFieldSpinner.setVisibility(View.INVISIBLE);
            timeDurationSpinner.setVisibility(View.VISIBLE);
            endTimeFlag = 0;
            setLatestEndTime(0);
            setTimeFactor(1);
            setTimeSpeedText();
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
            if(elapsedtime<0)
            {
                progressBar.setProgress(0);
            }

            progressText.setText("0");
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
            timerValue.setText(timeLeftFormatted);
            timerForceReset =false;
            return;

        }

        //To keep variables updating even if app is closed/sidelined
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        timerValue.setText(timeLeftFormatted);
        pauseIntent = intent;
        pauseButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        timeFieldSpinner.setVisibility(View.VISIBLE);
        timeDurationSpinner.setVisibility(View.INVISIBLE);
        timerForceReset =true;
        createNotificationChannel();
        setLatestResetTime(getLatestEndTime());
        setTimeSpeedText();
     //   requiredintent.putExtra("factor",getTimeFactor());
       // System.out.println("real time factor is"+getTimeFactor());
        intent.putExtra("factor",getTimeFactor());
        if(time==60000 || time==120000 || time==180000 || time== 300000 || time==600000)
        {
            setElapsedTime(0);
            totalElapsed=0;
        }
        else
        {
            totalElapsed=getElapsedTime();
        }

        System.out.println(totalElapsed);
        if (time == 0 || time < 0 ||(((systemEndTime / 1000) - elapsedtime- totalElapsed) / (systemEndTime / 1000)) * 100==0) {
            progressBar.setProgress(0);
            progressText.setText("0");
        } else {
            progressTimePercent = (((systemEndTime / 1000) - elapsedtime- totalElapsed) / (systemEndTime / 1000)) * 100;
           //System.out.println("system end time is"+systemEndTime);
            //System.out.println("progtimepers"+progresstimepercent);
            //System.out.println("elapsed time is"+elapsedtime);
            //System.out.println("total elapsed time is"+totalelapsed);
            Math.ceil(progressTimePercent);
            String totper;
            if(progressTimePercent <0 && time>0)
            {
                totper="1";
            }
            else {
                totper = Integer.toString((int) progressTimePercent);
            }
            progressBar.setProgress((int) progressTimePercent);
            if (elapsedtime < 0) {
                progressText.setText(R.string.hundred);
            } else {
                progressText.setText(totper);
            }
        }

    }

    /*
    The following methods implement our notification, which takes sends an intent to the TimeoutActivity
    and calls the AudioManager class to stop audio, once the notification box is clicked.

    Resources used to learn make a notification:https://developer.android.com/training/notify-user/build-notification
    Another resource: https://developer.android.com/guide/topics/ui/notifiers/notifications

    Create Notification Channel creates the channel on which our notification is passed upon, and gets the
    system service for notification.
     */

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channelName);
            String description = getString(R.string.channelDesc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("97", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
        }
        else if (extras.getBoolean("StopAlarm")) {
            AudioController.stopAudio();
        }

    }
    public void notIf()
    {
        Intent intent = new Intent(this, TimeoutActivity.class);
        intent.putExtra("StopAlarm",true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Intent broadcastIntent=new Intent(this, NotificationReciever.class);
        broadcastIntent.putExtra("StopAlarm",true);
        PendingIntent actionIntent=PendingIntent.getBroadcast(this,0,broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "97")
                .setSmallIcon(R.drawable.babyclock)
                .setContentTitle("ALARM")
                .setContentText("Time's Up! Touch Box To Turn Off Alarm!")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.babyclock))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setColor(getResources().getColor(R.color.appcolr))
                .addAction(R.mipmap.ic_launcher_round,"StopAlarm",actionIntent)
               .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(97, builder.build());


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
        timeDurationSpinner = (Spinner) findViewById(R.id.time_spinner);
        //To get the string array from the Strings.XML
        Resources res = this.getResources();
        String[] timePiece = res.getStringArray(R.array.minutes_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.time_duration_spinner, timePiece);
        adapter.setDropDownViewResource(R.layout.time_duration_spinner_dropdown);
        timeDurationSpinner.setAdapter(adapter);
        timeDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                        timeLeftInMilliSeconds = 60000;
                        selectedTime = 60000;
                        selectedTimeForPause = 60000;
                        setLatestResetTime(60000);
                        //millisecondConverterAndTimerUIupdate(selectedTime,timerValue);

                }
                if (position == 1) {
                    if(timerForceReset ==true)
                    {
                        Toast.makeText(getApplicationContext(),"Please Reset Timer First",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                        stopService(serviceintent);
                        timeLeftInMilliSeconds = 60000;
                        selectedTime = 60000;
                        selectedTimeForPause = 60000;
                        setLatestResetTime(60000);
                        progressBar.setProgress(100);
                        progressText.setText("100");
                        millisecondConverterAndTimerUIupdate(selectedTime, timerValue);
                    }
                }
                if (position == 2) {
                    System.out.println("yo!");
                    if(timerForceReset ==true)
                    {
                        Toast.makeText(getApplicationContext(),"Please Reset Timer First",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                        stopService(serviceintent);
                        timeLeftInMilliSeconds = 120000;
                        selectedTime = 120000;
                        selectedTimeForPause = 120000;
                        setLatestResetTime(120000);
                        progressBar.setProgress(100);
                        progressText.setText("100");
                        millisecondConverterAndTimerUIupdate(selectedTime, timerValue);
                    }

                }
                if (position == 3) {
                        if(timerForceReset ==true)
                        {
                            Toast.makeText(getApplicationContext(),"Please Reset Timer First",Toast.LENGTH_SHORT).show();
                        }
                        else {
                        Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                        stopService(serviceIntent);
                        timeLeftInMilliSeconds = 180000;
                        selectedTime = 180000;
                        selectedTimeForPause = 180000;
                        setLatestResetTime(180000);
                        progressBar.setProgress(100);
                        progressText.setText("100");
                        millisecondConverterAndTimerUIupdate(selectedTime, timerValue);
                    }
                }
                if (position == 4) {
                    if(timerForceReset ==true)
                    {
                        Toast.makeText(getApplicationContext(),"Please Reset Timer First",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                        stopService(serviceIntent);
                        timeLeftInMilliSeconds = 300000;
                        selectedTime = 300000;
                        selectedTimeForPause = 300000;
                        setLatestResetTime(300000);
                        progressBar.setProgress(100);
                        progressText.setText("100");
                        millisecondConverterAndTimerUIupdate(selectedTime, timerValue);
                    }
                }
                if (position == 5) {
                    if(timerForceReset ==true)
                    {
                        Toast.makeText(getApplicationContext(),R.string.resetplease,Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                        stopService(serviceIntent);
                        timeLeftInMilliSeconds = 600000;
                        selectedTime = 600000;
                        selectedTimeForPause = 600000;
                        setLatestResetTime(600000);
                        progressBar.setProgress(100);
                        progressText.setText("100");
                        millisecondConverterAndTimerUIupdate(selectedTime, timerValue);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*
    The following class creates a spinner which contains the "TimeFactor" which basically
    carries the rate at which time is consumed by the timer. This displays and sets
    the new time rate by sending an extra to the time service.
     */

    private void createTimeFactorSpinner() {
        timeFieldSpinner = (Spinner) findViewById(R.id.factorspinner);
        //To get the string array from the Strings.XML
        Resources res = this.getResources();
        String[] timePiece = res.getStringArray(R.array.timefactor);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.time_factor_spinner, timePiece);
        adapter.setDropDownViewResource(R.layout.time_factor_spinner_dropdown);
        timeFieldSpinner.setAdapter(adapter);
        timeFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                }
                if (position == 1) {

                    timeFactor=1;
                    timeFactorText.setText(R.string.time1);
                    setTimeFactor(1);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);
                }
                if (position == 2) {

                    timeFactor=2;
                    timeFactorText.setText(R.string.time2);
                    setTimeFactor(2);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);
                }
                if (position == 3) {
                    timeFactor=3;
                    timeFactorText.setText(R.string.time3);
                    setTimeFactor(3);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);}
                if (position == 4) {
                    timeFactor=4;
                    timeFactorText.setText(R.string.time4);
                    setTimeFactor(4);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);
                }
                if (position == 5) {
                    timeFactor = 5;
                    timeFactorText.setText(R.string.time5);
                    setTimeFactor(5);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);
                }
                if(position==6){
                    timeFactor=6;
                    timeFactorText.setText(R.string.time6);
                    setTimeFactor(6);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);
                }
                if(position==7){
                    timeFactor=7;
                    timeFactorText.setText(R.string.time7);
                    setTimeFactor(7);
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    serviceIntent.putExtra("factor",getTimeFactor());
                    startService(serviceIntent);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    //The following function streamlines our updating the textview to easily convert
    //from seconds to milliseconds.
    public void millisecondConverterAndTimerUIupdate(long selectedTime, TextView userText)
    {
        int mins = (int) (selectedTime / (double) 1000) / 60;
        int secs = (int) (selectedTime / (double) 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        userText.setText(timeLeftFormatted);
    }

    /*
  The start button starts the background service by calling from counterservice, to run time.
   */
    private void setupStartButton() {

        startButton = (Button) findViewById(R.id.time_push_button);
        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                setserviceIntent(serviceIntent);

                //This if clause finds and sets the reset time coordinating with the
                //time selected by the user while starting the timer.
                if(getLatestResetTime()==0)
                {
                    setLatestResetTime((int)timeLeftInMilliSeconds);
                    createTimeFactorSpinner();
                    setElapsedTime(0);
                    timeFieldSpinner.setSelection(1);
                }
                //Then, we recreate the timefactorspinner
                //and set our rate at 100% which corresponds to 1
                createTimeFactorSpinner();
                setTimeFactor(1);

                //The following variables are sent as extras to the service
                //The service recieves the rate of time speed, time selected by user
                //and a default boolean to check in future if the timer has been reset or not
                timeFieldSpinner.setSelection(1);
                serviceIntent.putExtra("factor",timeFactor);
                serviceIntent.putExtra("mills", timeLeftInMilliSeconds);
                serviceIntent.putExtra("reset",resetChecker);
                selectedTime=timeLeftInMilliSeconds;
                isTimerRunning = true;
                startService(serviceIntent);
                registerReceiver(broadCastReceiver, new IntentFilter(TimeService.TIME_BROADCAST));
                timerForceReset =false;


                //We set visibility of various buttons which will appear after timer has started.
                pauseButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                timeFieldSpinner.setVisibility(View.VISIBLE);
                timeDurationSpinner.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setserviceIntent(Intent serviceIntent) {
        requiredIntent =serviceIntent;
    }

    /*
 Pause button stops the service temporarily storing the time left in milliseconds.
  */
    private void setupPauseButton() {

        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int time = pauseIntent.getIntExtra("time", 0);
                totalElapsed = totalElapsed +pauseIntent.getDoubleExtra("elap", 0);
                setElapsedTime((int) totalElapsed);
                timeLeftInMilliSeconds = time;
                ispaused=true;
                isTimerRunning = false;
                Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                stopService(serviceIntent);
                // unregisterReceiver(broadcastReceiver);
                pauseButton.setVisibility(View.INVISIBLE);
                timeFieldSpinner.setVisibility(View.INVISIBLE);


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
                if (timerForceReset == true) {
                    Toast.makeText(getApplicationContext(), "Please Reset Timer First", Toast.LENGTH_SHORT).show();
                }
                else {
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
                            setLatestResetTime((int) timeLeftInMilliSeconds);
                            isTimerRunning = false;
                            totalElapsed = 0;
                            //ispaused=false;
                            progressBar.setProgress(100);
                            progressText.setText("100");
                            endTimeFlag = 0;
                            setLatestEndTime(0);
                            millisecondConverterAndTimerUIupdate(selectedTime, timerValue);
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
                int requiredtime=getLatestResetTime();
                timeLeftInMilliSeconds = requiredtime;
                pauseButton.setVisibility(View.INVISIBLE);
                isTimerRunning = false;
                timerForceReset =false;
                totalElapsed =0;
                setElapsedTime(0);
                //ispaused=false;
                progressBar.setProgress(100);
                progressText.setText("100");
                endTimeFlag=0;
                setLatestEndTime(0);
                setTimeFactor(1);
                //  System.out.println(selectedTime);
                millisecondConverterAndTimerUIupdate(requiredtime,timerValue);
                setLatestResetTime(0);
                resetButton.setVisibility(View.INVISIBLE);
                resetChecker=true;
                timeFieldSpinner.setVisibility(View.INVISIBLE);
                timeDurationSpinner.setVisibility(View.VISIBLE);
                setTimeSpeedText();

       }


        });
    }


    /*
    The following are all shared perferences which are utilized
    for saving the app's reset time, setting the rate of time, and the time elapsed
    by the realtime clock of the timer.
     */
    private void setLatestResetTime(int restime){
        SharedPreferences settings = getSharedPreferences("resetpref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("resettime",restime);
        editor.apply();

    }
    private int getLatestResetTime()
    {
        SharedPreferences settings = getSharedPreferences("resetpref", 0);
        int newTime = settings.getInt("resettime", 0);
        return newTime;
    }
    private void setTimeFactor(int restime){
        SharedPreferences settings = getSharedPreferences("timepref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("factime",restime);
        editor.apply();

    }
    private int getTimeFactor()
    {
        SharedPreferences settings = getSharedPreferences("timepref", 0);
        int newTime = settings.getInt("factime", 0);
        //System.out.println("newTime is"+newTime);
        return newTime;
    }

    private void setElapsedTime(int elapsedTime){
        SharedPreferences settings = getSharedPreferences("elappref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("elaptime",elapsedTime);
        editor.apply();

    }
    private int getElapsedTime()
    {
        SharedPreferences settings = getSharedPreferences("elappref", 0);
        int newTime = settings.getInt("elaptime", 0);
        //System.out.println("newTime is"+newTime);
        return newTime;
    }

    private void setTimeSpeedText()
    {
        int timeFacCheck=getTimeFactor();
        if(timeFacCheck==1)
        {
            timeFactorText.setText(R.string.time1);
        }
        if(timeFacCheck==2)
        {
            timeFactorText.setText(R.string.time2);
        }
        if(timeFacCheck==3)
        {
            timeFactorText.setText(R.string.time3);
        }
        if(timeFacCheck==4)
        {
            timeFactorText.setText(R.string.time4);
        }
        if(timeFacCheck==5)
        {
            timeFactorText.setText(R.string.time5);
        }
        if(timeFacCheck==6)
        {
            timeFactorText.setText(R.string.time6);
        }
        if(timeFacCheck==7)
        {
            timeFactorText.setText(R.string.time7);
        }
    }

    /*
    For making the intent
     */
    public static Intent makeIntent(Context context) {
        Intent timeoutIntent = new Intent(context, TimeoutActivity.class);
        return timeoutIntent;
    }

}
