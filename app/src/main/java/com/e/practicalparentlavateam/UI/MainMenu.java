/*This is the main menu of the whole app*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //This will be for our configuring the children activity.
        Button configbtn=(Button)findViewById(R.id.configchild);
        configbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Following activity returns the help menu's intent
                Intent configintent=ConfigureChildren.makeIntent(MainMenu.this);
                startActivity(configintent);
            }
        });

        //This will be for flipping the coin activity
        Button flipbtn=(Button)findViewById(R.id.coinflip);
        flipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("childPrefs", null);
                Type type = new TypeToken<List<String>>() {}.getType();
                List<String> childList = gson.fromJson(json, type);
                if(childList==null || childList.size() == 0){
                    Intent intent = CoinFlipActivity.makeLaunch1(MainMenu.this);
                    startActivity(intent);
                }
                else {
                    //Following activity returns the help menu's intent
                    Intent flipintent = SelectChildren.makeIntent(MainMenu.this);
                    startActivity(flipintent);
                }
            }
        });


        //This will be for the timeout timer activity
        Button timeoutbtn=(Button)findViewById(R.id.timeout);
        timeoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent timeoutint=TimeoutActivity.makeIntent(MainMenu.this);
                startActivity(timeoutint);
            }
        });

    }
    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent menuintent = new Intent(context, MainMenu.class);
        return menuintent;
    }

}