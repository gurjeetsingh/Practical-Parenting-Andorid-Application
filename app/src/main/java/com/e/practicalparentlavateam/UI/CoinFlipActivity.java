package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flipCoinToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        flipCoin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coin_flip, menu);
        return true;
    }

    private void flipCoin() {
        //https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        final Random random = new Random();
        final ImageView image1 = (ImageView) findViewById(R.id.front);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //https://www.ear0.com/sound/show/soundid-10760
                MediaPlayer sound = MediaPlayer.create(CoinFlipActivity.this, R.raw.coin);
                sound.start();
                final ObjectAnimator front = ObjectAnimator.ofFloat(image1, "scaleX", 1f, 0f);
                final ObjectAnimator back = ObjectAnimator.ofFloat(image1, "scaleX", 0f, 1f);
                front.setInterpolator(new DecelerateInterpolator());
                back.setInterpolator(new AccelerateDecelerateInterpolator());
                front.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        int num = random.nextInt(2);
                        if(num == 1){
                            //https://www.mint.ca/store/coins/10-oz.-pure-silver-gold-plated-coin---robert-batemans-eminto-the-light---lionem---mintage-700-2019-prod3550023
                            image1.setImageResource(R.drawable.coin_front);
                        }
                        else{
                            image1.setImageResource(R.drawable.coin_back);
                        }
                        back.start();
                    }
                });
                front.start();
            }
        });
    }

    public static Intent makeIntent(Context context) {
        Intent coinflipintent = new Intent(context, CoinFlipActivity.class);
        return coinflipintent;
    }
}