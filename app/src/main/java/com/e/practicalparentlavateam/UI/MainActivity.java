package com.e.practicalparentlavateam.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.e.practicalparentlavateam.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mainmenuintent=MainMenu.makeIntent(MainActivity.this);
        startActivity(mainmenuintent);
        finish();

    }
}