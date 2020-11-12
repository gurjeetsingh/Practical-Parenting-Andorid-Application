/*This is the activity for flipping coin, and it include Flipping history*/
//merged with master

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.Model.HistoryItem;
import com.e.practicalparentlavateam.Model.HistoryManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity {
    private static final String EXTRA_CHOICE = "com.e.practicalparentlavateam.UI - the choice";
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";

    private String name = "";
    private HistoryManager history_manager;
    private String choice = "";
    private int win = R.drawable.win;
    private int lose = R.drawable.lose;
    private int coin_front = R.drawable.coin_front;
    private int coin_back = R.drawable.coin_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flipCoinToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getName();
        getChoice();
        getHistory();
        historyButton();
        individualHistoryButton();
        flipCoinButton();
        deleteHistoryButton();
    }

    private void getName() {
        Intent i = getIntent();
        name = i.getStringExtra(EXTRA_NAME);
        TextView text = (TextView) findViewById(R.id.ChildName);
        if(name != null)
            text.setText(name);
    }

    private void getChoice() {
        Intent i = getIntent();
        choice = i.getStringExtra(EXTRA_CHOICE);
        TextView text = (TextView) findViewById(R.id.choice);
        if(choice != null)
            text.setText(choice);
    }

    private void getHistory() {
        HistoryManager.setInstance(getData());
        history_manager = HistoryManager.getInstance();
    }

    private void historyButton() {
        Button his = (Button) findViewById(R.id.history);
        his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FlippingHistory.makeLaunch(CoinFlipActivity.this);
                startActivity(intent);
            }
        });
    }

    private void individualHistoryButton() {
        Button indHisbt = findViewById(R.id.childHistory);
        if(name == null)
            indHisbt.setVisibility(View.INVISIBLE);
        else {
            indHisbt.setVisibility(View.VISIBLE);
            indHisbt.setText(name + "'s history");
        }
        indHisbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = individual_history.makeIntent(CoinFlipActivity.this, name);
                startActivity(intent);
            }
        });
    }

    private void flipCoinButton() {
        final TextView result = findViewById(R.id.resultOfFlipping);
        result.setVisibility(View.INVISIBLE);
        //https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        final Random random = new Random();
        final ImageView coinImage = (ImageView) findViewById(R.id.front);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This is the animation part
                MediaPlayer sound = MediaPlayer.create(CoinFlipActivity.this, R.raw.coin);
                sound.start();
                final ObjectAnimator front = ObjectAnimator.ofFloat(coinImage, "scaleX", 1f, 0f);
                final ObjectAnimator back = ObjectAnimator.ofFloat(coinImage, "scaleX", 0f, 1f);
                front.setInterpolator(new DecelerateInterpolator());
                back.setInterpolator(new AccelerateDecelerateInterpolator());
                final int num = random.nextInt(2);
                front.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if(num == 1){
                            //https://www.mint.ca/store/coins/10-oz.-pure-silver-gold-plated-coin---robert-batemans-eminto-the-light---lionem---mintage-700-2019-prod3550023
                            coinImage.setImageResource(R.drawable.coin_front);
                            result.setText("Heads");
                            result.setVisibility(View.VISIBLE);
                        }
                        else{
                            coinImage.setImageResource(R.drawable.coin_back);
                            result.setText("Tails");
                            result.setVisibility(View.VISIBLE);
                        }
                        back.start();
                    }
                });
                front.start();

                //Judge if the child win the Flipping and push into the history
                //If no choice of heads or tails, no record of history
                int c = 2;
                if(choice == null)
                    return;
                else if(choice.equals("Tails"))
                    c = 0;
                else if(choice.equals("Heads"))
                    c = 1;
                int image;
                int coinID;
                if(num == 1)
                    coinID = coin_front;
                else
                    coinID = coin_back;
                if(c == num) {
                    image = win;
                    Date currentTime = new Date();
                    history_manager.add(new HistoryItem(currentTime.toString(), name, choice, image, coinID));
                    HistoryManager.setInstance(history_manager);
                    SaveName(name);
                    SaveHistory(history_manager);
                }
                else {
                    image = lose;
                    Date currentTime = new Date();
                    history_manager.add(new HistoryItem(currentTime.toString(),name, choice, image, coinID));
                    HistoryManager.setInstance(history_manager);
                    SaveName(name);
                    SaveHistory(history_manager);
                }
            }
        });
    }

    private void deleteHistoryButton() {
        Button delete = (Button) findViewById(R.id.delethistory);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                history_manager = new HistoryManager();
                HistoryManager.setInstance(new HistoryManager());
                SaveHistory(history_manager);
                Toast.makeText(CoinFlipActivity.this,"You Delete Flipping History!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HistoryManager getData(){
        SharedPreferences sp = getSharedPreferences("Save history",MODE_PRIVATE);
        Gson g = new Gson();
        String temp = sp.getString("history",null);
        return g.fromJson(temp,HistoryManager.class);
    }

    private void SaveName(String n){
        SharedPreferences sp = getSharedPreferences("Save name",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("name",n);
        edit.apply();
    }

    private void SaveHistory(HistoryManager m){
        SharedPreferences sp = getSharedPreferences("Save history",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        Gson g = new Gson();
        String temp = g.toJson(m);
        edit.putString("history",temp);
        edit.apply();
    }

    public static Intent makeLaunch1(Context context) {
        return new Intent(context, CoinFlipActivity.class);
    }

    public static Intent makeIntent3(Context context, String choice, String name){
        Intent intent = new Intent(context, CoinFlipActivity.class);
        intent.putExtra(EXTRA_CHOICE, choice);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept: https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainintent=MainMenu.makeIntent(CoinFlipActivity.this);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainintent);
        CoinFlipActivity.this.finish();
    }

}