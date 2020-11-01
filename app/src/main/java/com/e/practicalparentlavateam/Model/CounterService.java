package com.e.practicalparentlavateam.Model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import com.e.practicalparentlavateam.R;

public class CounterService  extends Service {

    private Intent comintent;
    private Intent mainintent;
    public static final String BROADCAST_ACTION = "com.javacodegeeks.android.androidtimerexample.MainActivity";

    private Handler handler = new Handler();
    private long initial_time;
    private long mEndtime;
    private int flag=0;
    long timeInMilliseconds = 0L;
    long  mTimeLeftInMillis;



    @Override
    public void onCreate() {
        super.onCreate();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        comintent = new Intent(BROADCAST_ACTION);

        long intime = intent.getLongExtra("mills",0);
        if(flag<1) {
            initial_time = intime;
            mEndtime = initial_time + System.currentTimeMillis();
            setmEndtime(mEndtime);
            flag++;
        }
        return START_STICKY;
    }

  private void setmEndtime(long end)
  {
      mEndtime=end;
  }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    private void DisplayLoggingInfo() {
        //System.out.println("init:"+initial_time);
        mTimeLeftInMillis = mEndtime - System.currentTimeMillis();
        //System.out.println("seconds remaining:"+mTimeLeftInMillis/1000);
        int timer = (int) mTimeLeftInMillis;
        System.out.println(timer);
        if(timer<0)
        {
            startalarm();
            stopSelf();
        }
        comintent.putExtra("time", timer);
        sendBroadcast(comintent);



    }

    public void startalarm() {
       AudioPlay.playAudio(this,R.raw.alarm_sound);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}