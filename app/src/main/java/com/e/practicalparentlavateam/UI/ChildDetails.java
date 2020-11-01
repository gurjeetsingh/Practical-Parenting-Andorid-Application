package com.e.practicalparentlavateam.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.e.practicalparentlavateam.Model.Child;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChildDetails extends AppCompatActivity {
    private EditText etName;

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
                        ChildrenManager children = ChildrenManager.getInstance();
                        children.add(child);
                        ChildDetails.this.finish();
                    }
                }
        );
    }
}