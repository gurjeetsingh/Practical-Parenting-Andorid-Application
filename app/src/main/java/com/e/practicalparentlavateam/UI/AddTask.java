package com.e.practicalparentlavateam.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.Task;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AddTask extends AppCompatActivity {
    private EditText task_name;
    private TaskManager task_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.AddTaskToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        task_manager = TaskManager.getInstance();
        setupButtonCancel();
        setupButtonOk();
    }

    private void setupButtonCancel() {
        Button btn = findViewById(R.id.CancelAddTask);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddTask.this.finish();
                    }
                }
        );
    }

    private void setupButtonOk() {
        task_name = findViewById(R.id.EnterTastName);
        Button btn = findViewById(R.id.AddTaskOk);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String child_name;
                        String name = task_name.getText().toString();
                        SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = prefs.getString("childPrefs", null);
                        Type type = new TypeToken<List<Children>>() {}.getType();
                        List<Children> child_list = gson.fromJson(json, type);
                        if(child_list == null || child_list.size() == 0) {
                            child_name = "No Child";
                        }
                        else {
                            child_name = child_list.get(0).getName();
                        }

                        // Create new data object
                        task_manager = TaskManager.getInstance();
                        task_manager.add(name, child_name);
                        saveNewTask(task_manager);
                        Intent intent = EditTasksList.makeLaunch(AddTask.this);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

    public void saveNewTask(TaskManager t){
        SharedPreferences prefs = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(t);
        editor.putString("taskPrefs", json);
        System.out.println(json);
        editor.commit();
    }

    public static Intent makeIntentForAdd(Context context) {
        return new Intent(context, AddTask.class);
    }
}