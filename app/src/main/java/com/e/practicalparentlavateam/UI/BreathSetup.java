//finite state machine in java example: https://docs.google.com/presentation/d/1nzW8FvMm1hcI-41SE-rJpL07OYlMCX64UPgwWvPvIXY/htmlpresent


package com.e.practicalparentlavateam.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.practicalparentlavateam.R;

public class BreathSetup extends AppCompatActivity {
    private static EditText editNumBreaths;
    private int numBreaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breath_setup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editNumBreaths = findViewById(R.id.editTextNumber);

        setupBegin();

    }

    public void setupBegin(){
        Button beginButton = findViewById(R.id.beginButton);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extract the number entered
                numBreaths = Integer.parseInt(editNumBreaths.getText().toString());
                Intent intent=DeepBreath.makeDeepBreathIntent(BreathSetup.this, numBreaths);
                Toast.makeText(BreathSetup.this, getString(R.string.hint_for_breath), Toast.LENGTH_LONG)
                        .show();
                startActivity(intent);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, BreathSetup.class);
    }
}