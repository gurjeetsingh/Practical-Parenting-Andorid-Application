//finite state machine in java example: https://docs.google.com/presentation/d/1nzW8FvMm1hcI-41SE-rJpL07OYlMCX64UPgwWvPvIXY/htmlpresent


package com.e.practicalparentlavateam.UI;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.e.practicalparentlavateam.Model.TimeService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

        createBreathSpinner();

        setupBegin();

    }

    /* spinner to select number of breaths */
    private void createBreathSpinner() {
        Spinner breathSpinner = (Spinner) findViewById(R.id.breath_spinner);
        //To get the string array from the Strings.XML
        Resources res = this.getResources();
        String[] breathOptions = res.getStringArray(R.array.breaths_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, breathOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breathSpinner.setAdapter(adapter);
        breathSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    numBreaths = 1;
                }
                if (position == 1) {
                    numBreaths = 2;

                }
                if (position == 2) {
                    numBreaths = 3;
                }
                if (position == 3) {
                    numBreaths = 4;
                }
                if (position == 4) {
                    numBreaths = 5;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numBreaths = 3;
            }
        });
    }

    public void setupBegin(){
        Button beginButton = findViewById(R.id.beginButton);
        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //extract the number entered
                //numBreaths = Integer.parseInt(editNumBreaths.getText().toString());
                Intent intent=DeepBreath.makeDeepBreathIntent(BreathSetup.this, numBreaths);
                startActivity(intent);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, BreathSetup.class);
    }
}