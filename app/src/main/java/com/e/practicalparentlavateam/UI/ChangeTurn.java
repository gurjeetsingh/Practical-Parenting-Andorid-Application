package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.Task;
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
    private TaskManager task_manager;
    private int position;
    private ChildrenManager children;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_turn);

        Toolbar toolbar = findViewById(R.id.ChangeTurnToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

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
        children = ChildrenManager.getInstance();
        ChildrenManager temp = gson.fromJson(json, type);
        if(temp != null)
            children.setInstance(temp);
    }

    private void setText() {
        task_manager = TaskManager.getInstance();
        TextView task_name = findViewById(R.id.task_name);
        task_name.setText(task_manager.getTasks(position).getTask());
        TextView child_name = findViewById(R.id.TurnName);
        child_name.setText(task_manager.getTasks(position).getName());
    }

    private void setImage() {
        children = ChildrenManager.getInstance();
        ImageView image = findViewById(R.id.task_image);
        try {
            File f = new File(children.getPath(), task_manager.getTasks(position).getName() + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            image.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void doneButton() {
        Button done = findViewById(R.id.done);
        if(task_manager.getTasks(position).getName().equals("No Child"))
            done.setVisibility(View.INVISIBLE);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Children> child_list = children.getChildren();
                if(child_list.size() == 0){
                    task_manager.setName(position,"No Child");
                }
                else {
                    int i = 0;
                    int num = 0;
                    while (i < child_list.size()) {
                        if (child_list.get(i).getName().equals(task_manager.getTasks(position).getName())) {
                            num = (i + 1) % child_list.size();
                            break;
                        }
                        i++;
                    }
                    if (i == child_list.size()) {
                        num = 0;
                    }
                    task_manager.setName(position, child_list.get(num).getName());
                }
                saveNewTask(task_manager);
                Intent intent = WhoseTurn.makeIntent(ChangeTurn.this);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupButtonCancel() {
        Button btn = findViewById(R.id.canelChangeTurn);
        if(task_manager.getTasks(position).getName().equals("No Child"))
            btn.setVisibility(View.INVISIBLE);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChangeTurn.this.finish();
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

    public static Intent makeChangeIntent(Context context, int position) {
        Intent intent = new Intent(context, ChangeTurn.class);
        intent.putExtra("position_index",position);
        return intent;
    }
}