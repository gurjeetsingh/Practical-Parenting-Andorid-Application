package com.e.practicalparentlavateam.Model;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.e.practicalparentlavateam.R;
/*
This is the underlying service which allows the timer to run in the background
even if the app is turned off, or if user navigates to different activities.
Resources used to learn how a service works:
https://developer.android.com/guide/components/services
https://stackoverflow.com/questions/4360074/creating-a-service-in-android
https://stackoverflow.com/questions/22496863/how-to-run-countdowntimer-in-a-service-in-android

TO understand broadcasts:
https://developer.android.com/reference/android/content/BroadcastReceiver

Understanding how to pass data from service to an intent, and how a service works:
https://stackoverflow.com/questions/3293243/pass-data-from-activity-to-service-using-an-intent
 */
public class TimeService extends Service {

    private Intent comIntent;
    private Intent progressIntent;
    public static final String TIME_BROADCAST = "TimeService";
    private Handler handler = new Handler();
    private long userSelectedTime;
    private long finalTime;
    private int timefactor;
    private int flag=0;
    private double progressnum=0;
    long timeLeftInMilliSeconds;
    long timeLeftforprogress;
    CountDownTimer ctimer;
    long elapsedtime;
    long originaltime;
    int iterator=1;


/*
The oncreate will have handlers; the remove
call backs will remove any outstanding handler actions on the
stack, and the postDelayed will delay update to the UI, just so that
the ticker moves down, subtracting a millisecond..
To understand how handlers work, which are integral to the application:
https://developer.android.com/reference/android/os/Handler#removeCallbacks(java.lang.Runnable,%2520java.lang.Object)
https://developer.android.com/reference/android/os/Handler
 */
    @Override
    public void onCreate() {
        super.onCreate();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 0); // 1 second
        setoriginaltimeclock();


    }

    private void setoriginaltimeclock() {
        originaltime=SystemClock.elapsedRealtime();
    }
    public long getoriginaltimeclock()
    {
        return  originaltime;
    }

    public void setelapsedtimeclock()
    {
        elapsedtime=SystemClock.elapsedRealtime();
    }
    public long getelapsedtimeclock()
    {
        return  elapsedtime;
    }

    /*
    The onstartcommand is a crucial function for the counterservice,
    as it is accessed everytime we come back to the app. When we arrive into
    the onstartcommand, we get the number of milliseconds left, and start the timer.


    We are using a flag here to determine whether the timer is already running or not.
    If the timer is not running, flag=0, so it will enter the if clause and start the timer.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        comIntent = new Intent(TIME_BROADCAST);
        long usertime = intent.getLongExtra("mills",0);
        int timefactorintent=intent.getIntExtra("factor",0);
        System.out.println(usertime);

        if(flag<1) {
            userSelectedTime = usertime;
            finalTime = userSelectedTime + System.currentTimeMillis();
            setFinalTime(finalTime);
            setTimefactor(timefactorintent);
            flag++;
        }
        return START_STICKY;
    }

    private void setTimefactor(int factor)
    {
        timefactor=factor;
    }


    /*
    This sets the final time. The final time is basically the current
    time + the user selected time. This allows us to figure out how much time
    is elapsing, by subtracting the current time from the final time.
     */
  private void setFinalTime(long end)
  {
      finalTime =end;
  }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            serviceUIUpdate();
            setelapsedtimeclock();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };




    /*
    The following function sends back updates to the TimeoutActivity,
    which used the time sent by the service ticker to the TimeoutActivity's
    UI to update it readily.
    If the time goes below 0, the timer stops
    the service, and starts the alarm.

    Here, we use a technique to make the ticker go down.
    Suppose our endtime is 6000 milliseconds, which we have calculated.
    System time is at 4000 millseconds.
    thus, the time left in millis will be calculated, at a delay of 1 second,
    like this:
    1st Iteration: timeleftinmillseconds=6000-4000=2000
    as system time will increase, time left in millis will increase.
    2nd Iteration: timeleftinmillseconds=6000-4001=1999
    3rd Iteration: timeleftinmillseconds=6000-4002=1998
    and so on...
     */
    private void serviceUIUpdate() {
        System.out.println(timefactor);
        if(timefactor==1) {
            timeLeftInMilliSeconds = finalTime - System.currentTimeMillis();
            long systemtime = finalTime;
            int timer = (int) timeLeftInMilliSeconds;
            long endTime = (int) finalTime;
            // System.out.println("this is the real endtime" + endTime);
            if (timer < 0) {
                startAlarm();
                stopSelf();
            }
            comIntent.putExtra("time", timer);

            double elapsedSeconds = (double) ((getelapsedtimeclock() - getoriginaltimeclock()) / 1000.0);
            comIntent.putExtra("elap", elapsedSeconds);
            sendBroadcast(comIntent);
        }
        if(timefactor==2)
        {
            //System.out.println("kalllu");
            double elapsedSeconds = ((double) ((getelapsedtimeclock() - getoriginaltimeclock()) / 1000.0));
            System.out.println(elapsedSeconds);
            if(Math.floor(elapsedSeconds%4)==1)
            {
                timeLeftInMilliSeconds = userSelectedTime - 1000*iterator;
                iterator++;
                long systemtime = finalTime;
                int timer = (int) timeLeftInMilliSeconds;
                long endTime = (int) finalTime;
                // System.out.println("this is the real endtime" + endTime);
                if (timer < 0) {
                    startAlarm();
                    stopSelf();
                }
                comIntent.putExtra("time", timer);
                comIntent.putExtra("elap", Math.floor(elapsedSeconds/4));
                sendBroadcast(comIntent);
            }


        }

    }

    /*
    Using the audio controller to play the alarm sound.
     */
    public void startAlarm() {
       AudioController.playAudio(this,R.raw.alarm_sound);
    }

    /*
    to remove any outstanding calls to the UI i.e. remove outstanding
     messages on the handler when the app is destroyed.
     */
    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
    }

    /*
    Extra generated function, not required.
            */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}