package com.e.practicalparentlavateam.Model;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/*
The following class universally declares a mediaplayer,
which carries our alarm, and can be called from anywhere
within the TimeoutActivity to call the start and stop function
to play or stop the alarm.
 */

public class AudioController {

    public static MediaPlayer mediaPlayer;
    public static void playAudio(Context c,int id){
        mediaPlayer = MediaPlayer.create(c,id);
        mediaPlayer.start();
    }
    public static void stopAudio(){
        mediaPlayer.stop();
    }
}