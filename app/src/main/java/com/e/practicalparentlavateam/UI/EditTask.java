package com.e.practicalparentlavateam.UI;

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

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;

import java.util.List;

public class EditTask extends AppCompatActivity {
    private TaskManager task_manager;
    private int position;
    private EditText enter_edit_task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Toolbar toolbar = findViewById(R.id.EditTaskToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getPosition();
        setHint();
        setUpButtonOk();
        setUpButtonDelete();
    }

    private void getPosition() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position_index",0);
    }

    private void setHint() {
        task_manager = TaskManager.getInstance();
        enter_edit_task = findViewById(R.id.EnterEditTastName);
        enter_edit_task.setHint(task_manager.getTasks().get(position));
    }

    private void setUpButtonOk() {
        enter_edit_task = findViewById(R.id.EnterEditTastName);
        Button btn = findViewById(R.id.EditTaskOk);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = enter_edit_task.getText().toString();

                        // Create new data object
                        task_manager.setTasks(position, name);
                        saveNewTask(task_manager.getTasks());
                        Intent intent = EditTasksList.makeLaunch(EditTask.this);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

    private void setUpButtonDelete() {
        Button btn = findViewById(R.id.DeleteEditTask);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        task_manager.remove(position);
                        saveNewTask(task_manager.getTasks());
                        saveNameList(task_manager.getName());
                        Intent intent = EditTasksList.makeLaunch(EditTask.this);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

    public void saveNewTask(List<String> t){
        SharedPreferences prefs = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(t);
        editor.putString("taskPrefs", json);
        System.out.println(json);
        editor.commit();
    }

    public void saveNameList(List<String> n){
        SharedPreferences prefs = this.getSharedPreferences("nameList", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(n);
        editor.putString("nameList", json);
        System.out.println(json);
        editor.commit();
    }

    public static Intent makeEditIntent(Context context, int position) {
        Intent intent = new Intent(context, EditTask.class);
        intent.putExtra("position_index",position);
        return intent;
    }
}