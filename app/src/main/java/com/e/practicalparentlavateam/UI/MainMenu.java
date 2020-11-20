/*This is the main menu of the whole app*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        configureChildrenButton();
        flipCoinButton();
        timeoutTimerButton();
        whoseTurnButton();
    }

    private void configureChildrenButton() {
        //This will be for our configuring the children activity.
        Button config_btn=(Button)findViewById(R.id.configchild);
        config_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Following activity returns the help menu's intent
                Intent config_intent=ConfigureChildren.makeIntent(MainMenu.this);
                startActivity(config_intent);
            }
        });
    }

    private void flipCoinButton() {
        //This will be for flipping the coin activity
        Button flip_btn=(Button)findViewById(R.id.coinflip);
        flip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("childPrefs", null);
                Type type = new TypeToken<ChildrenManager>() {}.getType();
                ChildrenManager childList = gson.fromJson(json, type);
                if(childList==null || childList.getChildren().size() == 0){
                    Intent intent = CoinFlipActivity.makeLaunch1(MainMenu.this);
                    startActivity(intent);
                }
                else {
                    //Following activity returns the help menu's intent
                    Intent flip_intent = SelectChildren.makeIntent(MainMenu.this);
                    startActivity(flip_intent);
                }
            }
        });
    }

    private void timeoutTimerButton() {
        //This will be for the timeout timer activity
        Button timeout_btn=(Button)findViewById(R.id.timeout);
        timeout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timeout_int=TimeoutActivity.makeIntent(MainMenu.this);
                startActivity(timeout_int);
            }
        });
    }

    private void whoseTurnButton() {
        //This will be for the whose turn activity
        Button whose_turn_btn = findViewById(R.id.tasks);
        whose_turn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tasks_intent = WhoseTurn.makeIntent(MainMenu.this);
                startActivity(tasks_intent);
            }
        });
    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent menuintent = new Intent(context, MainMenu.class);
        return menuintent;
    }

}