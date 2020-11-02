package com.e.practicalparentlavateam.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.e.practicalparentlavateam.Model.Child;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ChildDetails extends AppCompatActivity {
    private EditText etName;
    private ChildrenManager children;

    public static Intent makeIntentForAdd(Context context) {
        return new Intent(context, ChildDetails.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etName = findViewById(R.id.etName);

        setupButtonCancel();
        setupButtonOk();

    }

    private void setupButtonCancel() {
        Button btn = findViewById(R.id.btnCancel);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChildDetails.this.finish();
                    }
                }
        );
    }

    private void setupButtonOk() {
        Button btn = findViewById(R.id.btnSave);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = etName.getText().toString();


                        // Create new data object
                        Child child = new Child(name);
                        children = ChildrenManager.getInstance();
                        children.add(child);
                        saveChildDetails();
                        ChildDetails.this.finish();
                    }
                }
        );
    }

//    private void saveChildDetails() {
//        //shared preferences object
//        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
//        //shared preferences editor
//        SharedPreferences.Editor editor = prefs.edit();
//        //edit item at key
//        editor.putString("Children", numPumpkins);
//        //apply the change
//
//        game.setPumpkinTotal(numPumpkins);
//        editor.commit();
//
//    }

    public void saveChildDetails(){
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(children);
        editor.putString("childPrefs", json);
        editor.apply();     // This line is IMPORTANT !!!
    }
}