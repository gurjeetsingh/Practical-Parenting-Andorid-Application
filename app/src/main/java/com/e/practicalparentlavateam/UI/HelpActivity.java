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

    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent helpIntent = new Intent(context, HelpActivity.class);
        return helpIntent;
    }
}