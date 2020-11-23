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

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SelectChildren extends AppCompatActivity {
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";
    private String name = null;
    private ChildrenManager childList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_children);

        Toolbar toolbar = (Toolbar) findViewById(R.id.select_child_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getName();
        loadLastTimeChild();
        chooseChild();
        skip();
        chooseSide();
    }

    private void getName() {
        Intent intent = getIntent();
        name = intent.getStringExtra(EXTRA_NAME);
        if(name != null){
            TextView text = (TextView) findViewById(R.id.name);
            text.setText(name);
        }
    }

    /*Get the child who flipped last time and choose the child this time to flip*/
    private void loadLastTimeChild() {
        //Default name
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<ChildrenManager>() {}.getType();
        childList = gson.fromJson(json, type);
        if(childList != null)
            ChildrenManager.setInstance(childList);
        if(childList != null && name == null){
            TextView text = (TextView) findViewById(R.id.name);
            text.setText(R.string.first_child);
        }

        SharedPreferences preferences = getSharedPreferences("Save name",MODE_PRIVATE);
        String lastTimeName = preferences.getString("name",null);
        TextView lastTimeChild = findViewById(R.id.last_time_child);
        if(lastTimeName == null){
            lastTimeChild.setText("None");
        }
        else{
            lastTimeChild.setText(lastTimeName);

            int index = 0;
            while (index < childList.getChildren().size()) {
                if (childList.get(index).getName().equals(lastTimeName)) {
                    int num = (index + 1) % childList.getChildren().size();
                    if (name == null) {
                        name = childList.get(num).getName();
                        System.out.println(name);
                        TextView text = (TextView) findViewById(R.id.name);
                        text.setText(name);
                    }
                    break;
                }
                index++;
            }
            if (index == childList.getChildren().size()) {
                if (name == null) {
                    name = childList.get(0).getName();
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
                Intent intent = ChooseChildren.makeIntent2(SelectChildren.this, name);
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

    private void chooseSide() {
        Button choose = findViewById(R.id.choose_side);
        if(name == null)
            choose.setVisibility(View.INVISIBLE);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(name);
                Intent intent = ChooseSide.makeLaunch(SelectChildren.this, name);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept:
        // https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainIntent = MainMenu.makeIntent(SelectChildren.this);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        SelectChildren.this.finish();
    }

    public static Intent makeLaunch(Context context, String name) {
        Intent intent = new Intent(context, SelectChildren.class);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectChildren.class);
    }
}