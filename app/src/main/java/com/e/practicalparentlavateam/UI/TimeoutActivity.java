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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
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


import com.e.practicalparentlavateam.Model.AudioController;
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
    double timeo=0;

    int endTimeFlag=0;
    long endTime;
    long systemEndTime;
    double progresstimepercent;
    boolean ispaused=false;
    int timeFactor=1;
    int selectedTimeForPause;
    double totalelapsed=0;

    Context context = this;



    private TextView timerValue;
    Intent pauseIntent;
    Intent resetIntent;

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
        SharedPreferences settings = getSharedPreferences("resetpref", 0);
        int newtime = settings.getInt("resettime", 0);
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
        endTime = intent.getLongExtra("endtime", 0);
        int mins = (int) (time / (double) 1000) / 60;
        int secs = (int) (time / (double) 1000) % 60;
        if (time == 0 || time < 0)

        {
            isTimerRunning = false;
            totalelapsed=0;
            alarmOffButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            endTimeFlag = 0;
            setLatestEndTime(0);
            setLatestResetTime(0);
            setTimeFactor(1);
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

        //To keep variables updating even if app is closed/sidelined
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
        timerValue.setText(timeLeftFormatted);
        pauseIntent = intent;
        pauseButton.setVisibility(View.VISIBLE);
        resetButton.setVisibility(View.VISIBLE);
        settimespeedtext();


        if (time == 0 || time < 0 ||(((systemEndTime / 1000) - elapsedtime-totalelapsed) / (systemEndTime / 1000)) * 100==0) {
            progressBar.setProgress(0);
            progressText.setText("0");
        } else {
            progresstimepercent = (((systemEndTime / 1000) - elapsedtime-totalelapsed) / (systemEndTime / 1000)) * 100;
            System.out.println("system end time is"+systemEndTime);
            Math.ceil(progresstimepercent);

            String totper = Integer.toString((int) progresstimepercent);
            progressBar.setProgress((int) progresstimepercent);
            if (elapsedtime < 0) {
                progressText.setText("100");
            } else {
                progressText.setText(totper);
            }
        }

    }

    private void settimespeedtext()
    {
        int timeFacCheck=getTimeFactor();
        if(timeFacCheck==1)
        {
            timeFactorText.setText("Time Speed@100%");
        }
        if(timeFacCheck==2)
        {
            timeFactorText.setText("Time Speed@25%");
        }
        if(timeFacCheck==3)
        {
            timeFactorText.setText("Time Speed@50%");
        }
        if(timeFacCheck==4)
        {
            timeFactorText.setText("Time Speed@75%");
        }
        if(timeFacCheck==5)
        {
            timeFactorText.setText("Time Speed@200%");
        }
        if(timeFacCheck==6)
        {
            timeFactorText.setText("Time Speed@300%");
        }
        if(timeFacCheck==7)
        {
            timeFactorText.setText("Time Speed@400%");
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
                    selectedTimeForPause = 60000;
                    setLatestResetTime(60000);
                    //millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 1) {
                    timeLeftInMilliSeconds = 60000;
                    selectedTime = 60000;
                    selectedTimeForPause = 60000;
                    setLatestResetTime(60000);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 2) {
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    timeLeftInMilliSeconds = 120000;
                    selectedTime = 120000;
                    selectedTimeForPause = 120000;
                    setLatestResetTime(120000);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);

                }
                if (position == 3) {
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceIntent);
                    timeLeftInMilliSeconds = 180000;
                    selectedTime = 180000;
                    selectedTimeForPause = 180000;
                    setLatestResetTime(180000);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 4) {
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceIntent);
                    timeLeftInMilliSeconds = 300000;
                    selectedTime = 300000;
                    selectedTimeForPause = 300000;
                    setLatestResetTime(300000);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
                if (position == 5) {
                    Intent serviceIntent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceIntent);
                    timeLeftInMilliSeconds = 600000;
                    selectedTime = 600000;
                    selectedTimeForPause = 600000;
                    setLatestResetTime(600000);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                }
                if (position == 1) {

                    timeFactor=1;
                    timeFactorText.setText("Time@100%");
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    setTimeFactor(1);
                    progressBar.setProgress(100);
                    progressText.setText("100");
                }
                if (position == 2) {

                    timeFactor=2;
                    timeFactorText.setText("Time@25%");
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    setTimeFactor(2);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    progressBar.setProgress(100);
                    progressText.setText("100");
                }
                if (position == 3) {
                    timeFactor=3;
                    timeFactorText.setText("Time@50%");
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    setTimeFactor(3);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    progressBar.setProgress(100);
                    progressText.setText("100");}
                if (position == 4) {
                    timeFactor=4;
                    timeFactorText.setText("Time@75%");
                    setTimeFactor(4);
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    progressBar.setProgress(100);
                    progressText.setText("100");
                }
                if (position == 5) {
                    timeFactor = 5;
                    timeFactorText.setText("Time@200%");
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    setTimeFactor(5);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    progressBar.setProgress(100);
                    progressText.setText("100");
                }
                if(position==6){
                    timeFactor=6;
                    timeFactorText.setText("Time@300%");
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    setTimeFactor(6);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    progressBar.setProgress(100);
                    progressText.setText("100");
                }
                if(position==7){
                    timeFactor=7;
                    timeFactorText.setText("Time@400%");
                    Intent serviceintent = new Intent(TimeoutActivity.this, TimeService.class);
                    stopService(serviceintent);
                    setTimeFactor(7);
                    millisecondConverterAndTimerUIupdate(selectedTime,timerValue);
                    progressBar.setProgress(100);
                    progressText.setText("100");
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

                serviceIntent.putExtra("factor",timeFactor);
                if(getLatestResetTime()==0)
                {
                    setLatestResetTime((int)timeLeftInMilliSeconds);
                }
                serviceIntent.putExtra("mills", timeLeftInMilliSeconds);
                selectedTime=timeLeftInMilliSeconds;
                isTimerRunning = true;
                //ispaused=false;
                startService(serviceIntent);
                //System.out.println("Time left for start" + mTimeLeftInMillis);
                registerReceiver(broadCastReceiver, new IntentFilter(TimeService.TIME_BROADCAST));
                pauseButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.VISIBLE);
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
                totalelapsed=totalelapsed+pauseIntent.getDoubleExtra("elap", 0);
                timeLeftInMilliSeconds = time;
                ispaused=true;
                isTimerRunning = false;
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
                int requiredtime=getLatestResetTime();
                timeLeftInMilliSeconds = requiredtime;
                pauseButton.setVisibility(View.INVISIBLE);
                isTimerRunning = false;
                totalelapsed=0;
                //ispaused=false;
                progressBar.setProgress(100);
                progressText.setText("100");
                endTimeFlag=0;
                setLatestEndTime(0);
                //  System.out.println(selectedTime);
                millisecondConverterAndTimerUIupdate(requiredtime,timerValue);
                setLatestResetTime(0);
                resetButton.setVisibility(View.INVISIBLE);
            }


        });
    }

    private void setLatestResetTime(int restime){
        SharedPreferences settings = getSharedPreferences("resetpref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("resettime",restime);
        editor.commit();

    }
    private int getLatestResetTime()
    {
        SharedPreferences settings = getSharedPreferences("resetpref", 0);
        int newtime = settings.getInt("resettime", 0);
        return newtime;
    }
    private void setTimeFactor(int restime){
        SharedPreferences settings = getSharedPreferences("timepref", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("factime",restime);
        editor.commit();

    }
    private int getTimeFactor()
    {
        SharedPreferences settings = getSharedPreferences("timepref", 0);
        int newtime = settings.getInt("factime", 0);
        return newtime;
    }



    /*
    For making the intent
     */
    public static Intent makeIntent(Context context) {
        Intent timeoutIntent = new Intent(context, TimeoutActivity.class);
        return timeoutIntent;
    }

}
