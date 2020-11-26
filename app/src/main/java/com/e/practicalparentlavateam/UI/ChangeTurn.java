/*
This is the activity to confirm the child have done the task
and change the turn of the child to the task
*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

public class ChangeTurn extends AppCompatActivity {
    private TaskManager taskManager;
    private int position;
    private ChildrenManager children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_turn);

        Toolbar toolbar = findViewById(R.id.change_turn_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getPosition();
        getChildrenData();
        setText();
        setImage();
        doneButton();
        setupButtonCancel();
    }

    private void getPosition() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position_index",0);
    }

    private void getChildrenData() {
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<ChildrenManager>() {}.getType();
        ChildrenManager temp = gson.fromJson(json, type);
        if(temp != null)
            ChildrenManager.setInstance(temp);
        children = ChildrenManager.getInstance();
    }

    private void setText() {
        taskManager = TaskManager.getInstance();
        TextView taskName = findViewById(R.id.task_name);
        taskName.setText(taskManager.getTasks(position).getTask());
        TextView childName = findViewById(R.id.turn_name);
        childName.setText(taskManager.getTasks(position).getName());
    }

    private void setImage() {
        children = ChildrenManager.getInstance();
        if(children == null || children.getNumChildren()==0)
            return;
        ImageView image = findViewById(R.id.task_image);
        try {
            File file = new File(children.getPath(),
                    taskManager.getTasks(position).getName() + ".jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            image.setImageBitmap(bitmap);
        }
        catch (FileNotFoundException exception)
        {
            exception.printStackTrace();
        }
    }

    private void doneButton() {
        Button done = findViewById(R.id.done);
        if(taskManager.getTasks(position).getName().equals(getString(R.string.no_child)))
            done.setVisibility(View.INVISIBLE);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Children> childrenList = children.getChildren();
                if(childrenList.size() == 0){
                    taskManager.setName(position,getString(R.string.no_child));
                }
                else {
                    int index = 0;
                    int num = 0;
                    while (index < childrenList.size()) {
                        if (childrenList.get(index).getName()
                                .equals(taskManager.getTasks(position).getName())) {
                            num = (index + 1) % childrenList.size();
                            break;
                        }
                        index++;
                    }
                    if (index == childrenList.size()) {
                        num = 0;
                    }
                    taskManager.setName(position, childrenList.get(num).getName());
                }
                saveNewTask(taskManager);
                Intent intent = WhoseTurn.makeIntent(ChangeTurn.this);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupButtonCancel() {
        Button button = findViewById(R.id.canel_change_turn);
        if(taskManager.getTasks(position).getName().equals(getString(R.string.no_child)))
            button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChangeTurn.this.finish();
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

    public static Intent makeChangeIntent(Context context, int position) {
        Intent intent = new Intent(context, ChangeTurn.class);
        intent.putExtra("position_index",position);
        return intent;
    }
}