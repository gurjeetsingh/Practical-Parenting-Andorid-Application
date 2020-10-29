package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.os.Bundle;
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

        flipCoin();
    }

    private void flipCoin() {
        /*final Random random = new Random();
        Button button = (Button) findViewById(R.id.button);
        final ImageView image1 = (ImageView) findViewById(R.id.front);
        final ImageView image2 = (ImageView) findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = random.nextInt(2);
                if(num == 1){
                    image1.setAlpha((float) 0.0);
                    image2.setAlpha((float) 1.0);
                }
                else{
                    image1.setAlpha((float) 1.0);
                    image2.setAlpha((float) 0.0);
                }
            }
        });*/
        //https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        final Random random = new Random();
        final ImageView image1 = (ImageView) findViewById(R.id.front);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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