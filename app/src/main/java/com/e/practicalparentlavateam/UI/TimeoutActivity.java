package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class TimeoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout);
        final TextView timertext=findViewById(R.id.timertext);
        int usertime=30000;


        Button timeoutbtn=(Button)  findViewById(R.id.timepushbtn);

        timeoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        timertext.setText("Time Left " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        timertext.setText("done!");
                    }
                }.start();
            }
        });




    }
    public static Intent makeIntent(Context context) {
        Intent timeoutintent = new Intent(context, TimeoutActivity.class);
        return timeoutintent;
    }

}