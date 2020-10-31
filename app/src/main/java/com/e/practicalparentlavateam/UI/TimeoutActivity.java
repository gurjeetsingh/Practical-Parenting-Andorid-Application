package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class TimeoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timeoutToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeout, menu);
        return true;
    }

    public static Intent makeIntent(Context context) {
        Intent timeoutintent = new Intent(context, TimeoutActivity.class);
        return timeoutintent;
    }

}