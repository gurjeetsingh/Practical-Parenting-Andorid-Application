package com.e.practicalparentlavateam.Model;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.e.practicalparentlavateam.UI.TimeoutActivity;
/*
This class recieves the clickback on the notification, and when clicked, stops the audio.
 */

public class NotificationReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioController.stopAudio();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(97);
    }
}
