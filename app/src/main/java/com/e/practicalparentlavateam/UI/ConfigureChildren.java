package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class ConfigureChildren extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_children);
    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent configintent = new Intent(context,ConfigureChildren.class);
        return configintent;
    }
}