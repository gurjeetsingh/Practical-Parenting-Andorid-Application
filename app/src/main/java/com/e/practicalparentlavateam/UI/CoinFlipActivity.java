package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.UI.model.HistoryItem;
import com.e.practicalparentlavateam.UI.model.HistoryManager;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Random;

public class CoinFlipActivity extends AppCompatActivity {
    private HistoryManager manager;
    private String choise;
    private int win = R.drawable.win;
    private int lose = R.drawable.lose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        getHistory();
        history();
        flip();
        head();
        tails();
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

    private void Save(HistoryManager m){
        SharedPreferences sp = getSharedPreferences("Save history",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        Gson g = new Gson();
        String temp = g.toJson(m);
        edit.putString("history",temp);
        edit.apply();
    }

    private void flip() {
        Button flip = (Button) findViewById(R.id.save);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = 2;
                if(choise == null)
                    return;
                else if(choise.equals("Tails"))
                    c = 1;
                else if(choise.equals("Head"))
                    c = 0;
                Random r = new Random();
                int num = r.nextInt(2);
                int image;
                TextView result = (TextView) findViewById(R.id.result);
                if(c == num) {
                    image = win;
                    result.setText("YOU WIN");
                    Date currentTime = new Date();
                    manager.add(new HistoryItem(currentTime.toString(), choise, image));
                    Save(manager);
                }
                else {
                    image = lose;
                    result.setText("YOU LOSE");
                    Date currentTime = new Date();
                    manager.add(new HistoryItem(currentTime.toString(), choise, image));
                    Save(manager);
                }
            }
        });
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
    public static Intent makeIntent(Context context) {
        Intent coinflipintent = new Intent(context, CoinFlipActivity.class);
        return coinflipintent;
    }
}