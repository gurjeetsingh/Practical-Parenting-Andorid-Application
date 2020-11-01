package com.e.practicalparentlavateam.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

public class ChildDetailsActivity {
    private EditText etName;

    public static Intent makeIntentForAdd(Context context) {
        return new Intent(context, ChildDetailsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lens_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etMake = findViewById(R.id.etMake);
        etAperture = findViewById(R.id.etAperture);
        etFocalLength = findViewById(R.id.etFocalLength);

        setupButtonCancel();
        setupButtonOk();
    }
}
