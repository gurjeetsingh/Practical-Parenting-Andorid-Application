package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class CoinFlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

    }

    public static Intent makeIntent(Context context) {
        Intent coinflipintent = new Intent(context, CoinFlipActivity.class);
        return coinflipintent;
    }
}