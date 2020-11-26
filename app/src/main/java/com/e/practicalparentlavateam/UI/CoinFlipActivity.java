/*This is the activity for flipping coin, and it include Flipping history*/
//merged with master

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.Model.HistoryItem;
import com.e.practicalparentlavateam.Model.HistoryManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity {
    private static final String EXTRA_CHOICE = "com.e.practicalparentlavateam.UI - the choice";
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";

    private String name = "";
    private HistoryManager historyManager;
    private String choice = "";
    private int win = R.drawable.win;
    private int lose = R.drawable.lose;
    private int coinFront = R.drawable.coin_front;
    private int coinBack = R.drawable.coin_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flip_coin_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getChild();
        getChoice();
        getHistory();
        historyButton();
        individualHistoryButton();
        flipCoinButton();
        deleteHistoryButton();
    }

    private void getChild() {
        Intent intent = getIntent();
        name = intent.getStringExtra(EXTRA_NAME);
        TextView text = (TextView) findViewById(R.id.the_child_name);
        ImageView imageView = (ImageView) findViewById(R.id.child_coin_image);
        if(name != null){
            text.setText(name);
            if(!name.equals(getString(R.string.nobody))) {
                try {
                    File file = new File(ChildrenManager.getInstance().getPath(), name + ".jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void getChoice() {
        Intent intent = getIntent();
        choice = intent.getStringExtra(EXTRA_CHOICE);
        TextView text = (TextView) findViewById(R.id.choice);
        if(choice != null)
            text.setText(choice);
    }

    private void getHistory() {
        HistoryManager.setInstance(getData());
        historyManager = HistoryManager.getInstance();
    }

    private void historyButton() {
        Button history = (Button) findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FlippingHistory.makeLaunch(CoinFlipActivity.this);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void individualHistoryButton() {
        Button individualHistory = findViewById(R.id.child_history);
        if(name == null || name.equals(getString(R.string.nobody)))
            individualHistory.setVisibility(View.INVISIBLE);
        else {
            individualHistory.setVisibility(View.VISIBLE);
            individualHistory.setText(name + getString(R.string.whose_history));
        }
        individualHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = IndividualHistory.makeIntent(CoinFlipActivity.this, name);
                startActivity(intent);
            }
        });
    }

    private void flipCoinButton() {
        final TextView result = findViewById(R.id.result_of_flipping);
        result.setVisibility(View.INVISIBLE);
        //https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        final Random random = new Random();
        final ImageView coinImage = (ImageView) findViewById(R.id.front);
        Button button = (Button) findViewById(R.id.flip);
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
                            //https://www.mint.ca/store/coins/10-oz.-pure-silver-gold-plated-coin---
                            // robert-batemans-eminto-the-light---lionem---mintage-700-2019-prod3550023
                            coinImage.setImageResource(R.drawable.coin_front);
                            result.setText(R.string.head);
                        }
                        else{
                            coinImage.setImageResource(R.drawable.coin_back);
                            result.setText(R.string.tails);
                        }
                        result.setVisibility(View.VISIBLE);
                        back.start();
                    }
                });
                front.start();

                //Judge if the child win the Flipping and push into the history
                //If no choice of heads or tails, no record of history
                int choiceNum = 2;
                if(choice == null)
                    return;
                else if(choice.equals(getString(R.string.tails)))
                    choiceNum = 0;
                else if(choice.equals(getString(R.string.head)))
                    choiceNum = 1;
                int image;
                int coinID;
                if(num == 1)
                    coinID = coinFront;
                else
                    coinID = coinBack;
                if(choiceNum == num) {
                    image = win;
                    Date currentTime = new Date();
                    historyManager.add(new HistoryItem(currentTime.toString(), name, choice, image, coinID));
                }
                else {
                    image = lose;
                    Date currentTime = new Date();
                    historyManager.add(new HistoryItem(currentTime.toString(),name, choice, image, coinID));
                }
                HistoryManager.setInstance(historyManager);
                if(!name.equals(getString(R.string.nobody))) {
                    saveName(name);
                }
                saveHistory(historyManager);
            }
        });
    }

    private void deleteHistoryButton() {
        Button delete = (Button) findViewById(R.id.delet_history);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyManager = new HistoryManager();
                HistoryManager.setInstance(new HistoryManager());
                saveHistory(historyManager);
                Toast.makeText(CoinFlipActivity.this,
                        R.string.hint_for_delete_history,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private HistoryManager getData(){
        SharedPreferences prefs = getSharedPreferences("Save history",MODE_PRIVATE);
        Gson gson = new Gson();
        String temp = prefs.getString("history",null);
        return gson.fromJson(temp,HistoryManager.class);
    }

    private void saveName(String n){
        SharedPreferences prefs = getSharedPreferences("Save name",MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("name",n);
        edit.apply();
    }

    private void saveHistory(HistoryManager m){
        SharedPreferences prefs = getSharedPreferences("Save history",MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        Gson gson = new Gson();
        String temp = gson.toJson(m);
        edit.putString("history",temp);
        edit.apply();
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept:
        // https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainIntent=MainMenu.makeIntent(CoinFlipActivity.this);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        CoinFlipActivity.this.finish();
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

}