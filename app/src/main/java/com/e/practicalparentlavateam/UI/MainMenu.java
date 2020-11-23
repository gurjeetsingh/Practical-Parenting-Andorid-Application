/*This is the main menu of the whole app*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
        helpScreenButton();
    }

    private void configureChildrenButton() {
        //This will be for our configuring the children activity.
        Button configButton=(Button)findViewById(R.id.configchild);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Following activity returns the help menu's intent
                Intent configIntent=ConfigureChildren.makeIntent(MainMenu.this);
                startActivity(configIntent);
            }
        });
    }

    private void flipCoinButton() {
        //This will be for flipping the coin activity
        Button flipButton = (Button)findViewById(R.id.coinflip);
        flipButton.setOnClickListener(new View.OnClickListener() {
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
                    Intent flipIntent = SelectChildren.makeIntent(MainMenu.this);
                    startActivity(flipIntent);
                }
            }
        });
    }

    private void timeoutTimerButton() {
        //This will be for the timeout timer activity
        Button timeoutButton=(Button)findViewById(R.id.timeout);
        timeoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent timeoutIntent=TimeoutActivity.makeIntent(MainMenu.this);
                startActivity(timeoutIntent);
            }
        });
    }

    private void whoseTurnButton() {
        //This will be for the whose turn activity
        Button whoseTurnButton = findViewById(R.id.tasks);
        whoseTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tasksIntent = WhoseTurn.makeIntent(MainMenu.this);
                startActivity(tasksIntent);
            }
        });
    }

    private void helpScreenButton() {
        //This will be for the helpscreen activity
        ImageButton helpButton=findViewById(R.id.helpbtn);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpIntent=HelpActivity.makeIntent(MainMenu.this);
                startActivity(helpIntent);
            }
        });
    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent menuIntent = new Intent(context, MainMenu.class);
        return menuIntent;
    }

}