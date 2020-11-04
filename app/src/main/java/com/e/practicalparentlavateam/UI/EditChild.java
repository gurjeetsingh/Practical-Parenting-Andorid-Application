package com.e.practicalparentlavateam.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditChild extends AppCompatActivity {
    private EditText etName2;
    private String childEditing;
    private int childEditingIndex;
    private ChildrenManager children;
    private static final String EXTRA_CHILD_INDEX = "Extra - Child Index";

    public static Intent makeEditIntent(Context c, int childIndex) {
        Intent intent = new Intent(c, EditChild.class);
        intent.putExtra(EXTRA_CHILD_INDEX, childIndex);
        return intent;
    }

    public void extractIndexFromIntent(){
        Intent intent = getIntent();
        childEditingIndex = intent.getIntExtra(EXTRA_CHILD_INDEX, 0);
        children = ChildrenManager.getInstance();
        childEditing = children.get(childEditingIndex);
        etName2.setHint(children.get(childEditingIndex));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        children = ChildrenManager.getInstance();
        etName2 = findViewById(R.id.etName2);

        //initalize values
        extractIndexFromIntent();
        //etName2.setHint(children.get(childEditingIndex));

        setupButtonDelete();
        setupButtonOk();


    }

    private void setupButtonDelete() {
        Button btn = findViewById(R.id.btnDelete);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        children.remove(childEditingIndex);
                        EditChild.this.finish();
                    }
                }
        );
    }

    private void setupButtonOk() {
        Button btn = findViewById(R.id.btnSaveEdit);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = etName2.getText().toString();


                        // Create new data object
                        children = ChildrenManager.getInstance();
                        children.add(name);
                        saveChildDetails();
                        finish();
                    }
                }
        );
    }

}