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

import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;

public class EditTask extends AppCompatActivity {
    private TaskManager taskManager;
    private int position;
    private EditText enterEditTask;

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
        taskManager = TaskManager.getInstance();
        enterEditTask = findViewById(R.id.EnterEditTastName);
        enterEditTask.setHint(taskManager.getTasks().get(position).getTask());
    }

    private void setUpButtonOk() {
        enterEditTask = findViewById(R.id.EnterEditTastName);
        Button btn = findViewById(R.id.EditTaskOk);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = enterEditTask.getText().toString();

                        // Create new data object
                        taskManager.setTask(position, name);
                        saveNewTask(taskManager);
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
                        taskManager.remove(position);
                        saveNewTask(taskManager);
                        Intent intent = EditTasksList.makeLaunch(EditTask.this);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }

    public void saveNewTask(TaskManager task){
        SharedPreferences prefs = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(task);
        editor.putString("taskPrefs", json);
        System.out.println(json);
        editor.commit();
    }

    public static Intent makeEditIntent(Context context, int position) {
        Intent intent = new Intent(context, EditTask.class);
        intent.putExtra("position_index",position);
        return intent;
    }
}