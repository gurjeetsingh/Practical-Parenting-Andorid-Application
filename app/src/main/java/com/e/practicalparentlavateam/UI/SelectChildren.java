/*Select child or just skip to flip*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SelectChildren extends AppCompatActivity {
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";
    private String name = null;

    public static Intent makeLaunch(Context context, String name) {
        Intent intent = new Intent(context, SelectChildren.class);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectChildren.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_children);

        Toolbar toolbar = (Toolbar) findViewById(R.id.selectChildToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getName();
        loadLastTimeChild();
        chooseChild();
        skip();
        chooseside();
    }

    private void chooseside() {
        Button choose = findViewById(R.id.chooseSide);
        if(name == null || name.equals("nobody"))
            choose.setVisibility(View.INVISIBLE);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = chooseSide.makeLaunch(SelectChildren.this, name);
                startActivity(intent);
            }
        });
    }

    private void skip() {
        Button skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CoinFlipActivity.makeLaunch1(SelectChildren.this);
                startActivity(intent);
                finish();
            }
        });
    }

    /*Get the child who flipped last time and choose the child this time to flip*/
    private void loadLastTimeChild() {
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> childList = gson.fromJson(json, type);

        SharedPreferences sp = getSharedPreferences("Save name",MODE_PRIVATE);
        String LastTimeName = sp.getString("name",null);
        TextView lastTimeChild = findViewById(R.id.LastTimeChild);
        if(LastTimeName == null){
            lastTimeChild.setText("None");

        }
        else{
            lastTimeChild.setText(LastTimeName);

            int i = 0;
            while (i < childList.size()) {
                if (childList.get(i).equals(LastTimeName)) {
                    int num = (i + 1) % childList.size();
                    if (name == null) {
                        name = childList.get(num);
                        System.out.println(name);
                        TextView text = (TextView) findViewById(R.id.name);
                        text.setText(name);
                    }
                    break;
                }
                i++;
            }
            if (i == childList.size()) {
                if (name == null) {
                    name = childList.get(0);
                    System.out.println(name);
                    TextView text = (TextView) findViewById(R.id.name);
                    text.setText(name);
                }
            }
        }
    }

    private void chooseChild() {
        TextView text = (TextView) findViewById(R.id.name);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Choose_children.makeIntent2(SelectChildren.this);
                startActivity(intent);
            }
        });
    }

    private void getName() {
        Intent i = getIntent();
        name = i.getStringExtra(EXTRA_NAME);
        if(name != null) {
            TextView text = (TextView) findViewById(R.id.name);
            text.setText(name);
        }
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept: https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainintent=MainMenu.makeIntent(SelectChildren.this);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainintent);
        SelectChildren.this.finish();
    }
}