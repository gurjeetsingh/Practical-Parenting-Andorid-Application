package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;

public class EditTask extends AppCompatActivity {
    private TaskManager taskManager;
    private int position;
    private EditText enterEditTask;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Toolbar toolbar = findViewById(R.id.EditTaskToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getPosition();
        setText();
        setUpButtonOk();
        setUpButtonDelete();
    }

    private void getPosition() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position_index",0);
    }

    private void setText() {
        taskManager = TaskManager.getInstance();
        enterEditTask = findViewById(R.id.EnterEditTastName);
        enterEditTask.setText(taskManager.getTasks().get(position).getTask());
    }

    private void setUpButtonOk() {
        enterEditTask = findViewById(R.id.EnterEditTastName);
        Button button = findViewById(R.id.EditTaskOk);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Extract data from screen
                        String name = enterEditTask.getText().toString();
                        if(name.equals("")){
                            Toast.makeText(EditTask.this,"Please Input A Name",Toast.LENGTH_SHORT).show();
                            return;
                        }

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogThemeEditTask);
                        builder.setTitle("Are you sure you want to delete?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                taskManager.remove(position);
                                saveNewTask(taskManager);
                                Intent intent = EditTasksList.makeLaunch(EditTask.this);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                }
        );
    }

    public void saveNewTask(TaskManager task){
        SharedPreferences preferences = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
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