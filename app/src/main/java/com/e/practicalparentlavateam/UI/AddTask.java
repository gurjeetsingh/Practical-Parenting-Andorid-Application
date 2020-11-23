/*This is the activity to add the task*/

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
import android.widget.Toast;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AddTask extends AppCompatActivity {
    private EditText taskName;
    private TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Toolbar toolbar = findViewById(R.id.AddTaskToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        taskManager = TaskManager.getInstance();
        setupButtonCancel();
        setupButtonOk();
    }

    private void setupButtonCancel() {
        Button button = findViewById(R.id.cancel_add_task);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AddTask.this.finish();
                    }
                }
        );
    }

    private void setupButtonOk() {
        taskName = findViewById(R.id.enter_tast_name);
        Button button = findViewById(R.id.add_task_ok);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String childName;
                        String name = taskName.getText().toString();
                        if(name.equals("")){
                            Toast.makeText(AddTask.this,R.string.hint_for_name,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = prefs.getString("childPrefs", null);
                        Type type = new TypeToken<ChildrenManager>() {}.getType();
                        ChildrenManager childList = gson.fromJson(json, type);
                        if(childList == null || childList.getChildren().size() == 0) {
                            childName = "No Child";
                        }
                        else {
                            childName = childList.get(0).getName();
                        }

                        // Create new data object
                        taskManager = TaskManager.getInstance();
                        taskManager.add(name, childName);
                        saveNewTask(taskManager);
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