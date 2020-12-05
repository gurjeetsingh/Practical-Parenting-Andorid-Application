/*Show the name of the team which created the app.
Show list the names of the developers who created it.
Show me any copyright information*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.e.practicalparentlavateam.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.help_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView getApp =(TextView) findViewById(R.id.project_id);
        getApp.setMovementMethod(LinkMovementMethod.getInstance());
        setHyperlink();


    }

    private void setHyperlink() {
        TextView getApp =(TextView) findViewById(R.id.project_id);
        getApp.setMovementMethod(LinkMovementMethod.getInstance());

        TextView coin =(TextView) findViewById(R.id.coin_image_copyright);
        coin.setMovementMethod(LinkMovementMethod.getInstance());

        TextView coinSound =(TextView) findViewById(R.id.coin_sound);
        coinSound.setMovementMethod(LinkMovementMethod.getInstance());

        TextView winImage =(TextView) findViewById(R.id.win_image_copyright);
        winImage.setMovementMethod(LinkMovementMethod.getInstance());

        TextView loseImage =(TextView) findViewById(R.id.lose_image_copyright);
        loseImage.setMovementMethod(LinkMovementMethod.getInstance());

        TextView alarmSound =(TextView) findViewById(R.id.alarm_sound_copyright);
        alarmSound.setMovementMethod(LinkMovementMethod.getInstance());

        TextView sunImage =(TextView) findViewById(R.id.sun_image_copyright);
        sunImage.setMovementMethod(LinkMovementMethod.getInstance());

        TextView defultImage =(TextView) findViewById(R.id.defult_image_copyright);
        defultImage.setMovementMethod(LinkMovementMethod.getInstance());

        TextView circleImage =(TextView) findViewById(R.id.circle_image_copyright);
        circleImage.setMovementMethod(LinkMovementMethod.getInstance());

        TextView music1 =(TextView) findViewById(R.id.music1_copyright);
        music1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView music2 =(TextView) findViewById(R.id.music2_copyright);
        music2.setMovementMethod(LinkMovementMethod.getInstance());

    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent helpIntent = new Intent(context, HelpActivity.class);
        return helpIntent;
    }
}