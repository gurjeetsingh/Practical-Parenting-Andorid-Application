/*Choose side*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.e.practicalparentlavateam.R;

public class chooseSide extends AppCompatActivity {
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";

    private String choice;
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_side);

        getName();
        headsButton();
        tailsButton();
    }

    private void tailsButton() {
        Button tails = (Button) findViewById(R.id.tails);
        tails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Tails";
                Intent intent = CoinFlipActivity.makeIntent3(chooseSide.this, choice, name);
                startActivity(intent);
                finish();
            }
        });
    }

    private void headsButton() {
        Button heads = (Button) findViewById(R.id.heads);
        heads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = "Heads";
                Intent intent = CoinFlipActivity.makeIntent3(chooseSide.this, choice, name);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getName() {
        Intent i = getIntent();
        name = i.getStringExtra(EXTRA_NAME);
    }

    public static Intent makeLaunch(Context context, String name){
        Intent intent = new Intent(context, chooseSide.class);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }
}