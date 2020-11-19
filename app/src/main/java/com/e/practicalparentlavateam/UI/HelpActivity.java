package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

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
        TextView getapp =(TextView) findViewById(R.id.projectid);
        getapp.setMovementMethod(LinkMovementMethod.getInstance());
    }
    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent helpintent = new Intent(context, HelpActivity.class);
        return helpintent;
    }
}