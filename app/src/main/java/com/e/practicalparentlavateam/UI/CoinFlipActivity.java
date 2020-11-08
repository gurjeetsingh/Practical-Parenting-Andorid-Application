/*This is the activity for flipping coin, and it include Flipping history*/

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

import com.e.practicalparentlavateam.Model.ChildrenManager;
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
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";

    private String name = "";
    private HistoryManager manager;
    private String choise;
    private int win = R.drawable.win;
    private int lose = R.drawable.lose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.flipCoinToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getName();
        loadLastTimeChild();
        chooseChild();
        getHistory();
        history();
        individualHistory();
        head();
        tails();
        flipCoin();
        deleteHistory();
    }

    private void individualHistory() {
        Button indHisbt = findViewById(R.id.childHistory);
        if(name == null)
            indHisbt.setVisibility(View.INVISIBLE);
        else {
            indHisbt.setVisibility(View.VISIBLE);
            indHisbt.setText(name + "'s");
        }
        indHisbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = individual_history.makeIntent(CoinFlipActivity.this, name);
                startActivity(intent);
            }
        });
    }

    /*Get the child who flipped last time and choose the child this time to flip*/
    private void loadLastTimeChild() {
        SharedPreferences sp = getSharedPreferences("Save name",MODE_PRIVATE);
        String LastTimeName = sp.getString("name",null);
        System.out.println(LastTimeName);
        TextView lastTimeChild = findViewById(R.id.LastTimeChild);
        if(LastTimeName == null){
            lastTimeChild.setText("None");
        }
        else{
            lastTimeChild.setText(LastTimeName);
            SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("childPrefs", null);
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> childList = gson.fromJson(json, type);

            for(int i=0; i<childList.size(); i++){
                if(childList.get(i).equals(LastTimeName)){
                    int num = (i+1)%childList.size();
                    if(name == null) {
                        name = childList.get(num);
                        TextView text = (TextView) findViewById(R.id.name);
                        text.setText(name);
                    }
                    break;
                }
            }
        }
    }

    private void chooseChild() {
        TextView text = (TextView) findViewById(R.id.name);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Choose_children.makeIntent2(CoinFlipActivity.this);
                startActivity(intent);
            }
        });
    }

    private void deleteHistory() {
        Button delete = (Button) findViewById(R.id.delethistory);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager = new HistoryManager();
                HistoryManager.setInstance(new HistoryManager());
                Save(manager);
                Toast.makeText(CoinFlipActivity.this,"You Delete Flipping History!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tails() {
        Button tails = (Button) findViewById(R.id.tails);
        tails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choise = "Tails";
                TextView showChoise = (TextView) findViewById(R.id.choise);
                showChoise.setText(choise);
            }
        });
    }

    private void head() {
        Button head = (Button) findViewById(R.id.head);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choise = "Head";
                TextView showChoise = (TextView) findViewById(R.id.choise);
                showChoise.setText(choise);
            }
        });
    }

    private void flipCoin() {
        //https://stackoverflow.com/questions/46111262/card-flip-animation-in-android
        final Random random = new Random();
        final ImageView image1 = (ImageView) findViewById(R.id.front);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This is the animation part
                MediaPlayer sound = MediaPlayer.create(CoinFlipActivity.this, R.raw.coin);
                sound.start();
                final ObjectAnimator front = ObjectAnimator.ofFloat(image1, "scaleX", 1f, 0f);
                final ObjectAnimator back = ObjectAnimator.ofFloat(image1, "scaleX", 0f, 1f);
                front.setInterpolator(new DecelerateInterpolator());
                back.setInterpolator(new AccelerateDecelerateInterpolator());
                final int num = random.nextInt(2);
                front.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
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

                //Judge if the child win the Flipping and push into the history
                //If no choice of head or tails, no record of history
                int c = 2;
                if(choise == null)
                    return;
                else if(choise.equals("Tails"))
                    c = 0;
                else if(choise.equals("Head"))
                    c = 1;
                int image;
                if(c == num) {
                    image = win;
                    Date currentTime = new Date();
                    manager.add(new HistoryItem(currentTime.toString(), name, choise, image));
                    HistoryManager.setInstance(manager);
                    SaveName(name);
                    Save(manager);
                }
                else {
                    image = lose;
                    Date currentTime = new Date();
                    manager.add(new HistoryItem(currentTime.toString(),name, choise, image));
                    HistoryManager.setInstance(manager);
                    SaveName(name);
                    Save(manager);
                }
            }
        });
    }

    private HistoryManager getData(){
        SharedPreferences sp = getSharedPreferences("Save history",MODE_PRIVATE);
        Gson g = new Gson();
        String temp = sp.getString("history",null);
        return g.fromJson(temp,HistoryManager.class);
    }

    private void getHistory() {
        HistoryManager.setInstance(getData());
        manager = HistoryManager.getInstance();
    }

    private void SaveName(String n){
        SharedPreferences sp = getSharedPreferences("Save name",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("name",n);
        edit.apply();
    }

    private void Save(HistoryManager m){
        SharedPreferences sp = getSharedPreferences("Save history",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        Gson g = new Gson();
        String temp = g.toJson(m);
        edit.putString("history",temp);
        edit.apply();
    }

    private void history() {
        Button his = (Button) findViewById(R.id.history);
        his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FlippingHistory.makeLaunch(CoinFlipActivity.this);
                startActivity(intent);
            }
        });
    }

    private void getName() {
        Intent i = getIntent();
        name = i.getStringExtra(EXTRA_NAME);
        TextView text = (TextView) findViewById(R.id.name);
        if(name != null)
            text.setText(name);
    }

    public static Intent makeIntent2(Context context, String name) {
        Intent intent = new Intent(context, CoinFlipActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, CoinFlipActivity.class);
    }
}