package com.e.practicalparentlavateam.Model;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.e.practicalparentlavateam.R;
/*
This is the underlying service which allows the timer to run in the background
even if the app is turned off, or if user navigates to different activities.
 */
public class TimeService extends Service {

    private Intent comintent;
    private Intent mainintent;
    public static final String TIME_BROADCAST = "TimeService";
    private Handler handler = new Handler();
    private long userselectedtime;
    private long finaltime;
    private int flag=0;
    long timeInMilliseconds = 0L;
    long  mTimeLeftInMillis;



/*
The oncreate will have handlers; the remove
call backs will remove any outstanding handler actions on the
stack, and the postDelayed will delay update to the UI, just so that
the ticker moves down, subtracting a millisecond..
 */
    @Override
    public void onCreate() {
        super.onCreate();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 0); // 1 second

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
        comintent = new Intent(TIME_BROADCAST);

        long usertime = intent.getLongExtra("mills",0);
        if(flag<1) {
            userselectedtime = usertime;
            finaltime = userselectedtime + System.currentTimeMillis();
            setFinaltime(finaltime);
            flag++;
        }
        return START_STICKY;
    }

    /*
    This sets the final time. The final time is basically the current
    time + the user selected time. This allows us to figure out how much time
    is elapsing, by subtracting the current time from the final time.
     */
  private void setFinaltime(long end)
  {
      finaltime =end;
  }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            ServiceUIUpdate();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

  

  /*
  The following function sends back updates to the TimeoutActivity,
  which used the time sent by the service ticker to the TimeoutActivity's
  UI to update it readily.
  If the time goes below 0, the timer stops
  the service, and starts the alarm.
   */
    private void ServiceUIUpdate() {
        //2000
        //+4000
        //6000-4000
        //6000-4001
        mTimeLeftInMillis = finaltime - System.currentTimeMillis();
        int timer = (int) mTimeLeftInMillis;
        if(timer<0)
        {
            startalarm();
            stopSelf();
        }
        comintent.putExtra("time", timer);
        sendBroadcast(comintent);

    }

    /*
    Using the audio controller to play the alarm sound.
     */
    public void startalarm() {
       AudioController.playAudio(this,R.raw.alarm_sound);
    }

    /*
    to remove any outstanding calls to the UI when the app is destroyed.
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