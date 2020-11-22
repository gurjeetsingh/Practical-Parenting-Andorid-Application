package com.e.practicalparentlavateam.Model;

import android.content.Context;
import android.media.MediaPlayer;

/*
The following class universally declares a mediaplayer,
which carries our alarm, and can be called from anywhere
within the TimeoutActivity to call the start and stop function
to play or stop the alarm.
 */

public class AudioController {

    public static MediaPlayer mediaPlayer;
    public static void playAudio(Context context,int id){
        mediaPlayer = MediaPlayer.create(context,id);
        mediaPlayer.start();
    }

    public static void stopAudio(){
        mediaPlayer.stop();
    }

    public static void stopVibration()
    {
        stopVibration();
    }
}