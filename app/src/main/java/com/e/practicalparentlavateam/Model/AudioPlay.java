package com.e.practicalparentlavateam.Model;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class AudioPlay {

    public static MediaPlayer mediaPlayer;
    public static void playAudio(Context c,int id){
        mediaPlayer = MediaPlayer.create(c,id);
        mediaPlayer.start();
    }
    public static void stopAudio(){
        mediaPlayer.stop();
    }
}