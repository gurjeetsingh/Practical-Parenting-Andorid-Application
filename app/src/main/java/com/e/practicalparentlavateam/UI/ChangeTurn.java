package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ChangeTurn extends AppCompatActivity {
    private TaskManager task_manager;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_turn);

        getPosition();
        setText();
        doneButton();
        setupButtonCancel();
    }

    private void getPosition() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position_index",0);
    }

    private void setText() {
        task_manager = TaskManager.getInstance();
        TextView taskName = findViewById(R.id.task_name);
        taskName.setText(task_manager.getTasks(position));
        TextView childName = findViewById(R.id.TurnName);
        childName.setText(task_manager.getName(position));
    }

    private void doneButton() {
        Button done = findViewById(R.id.done);
        if(task_manager.getName(position).equals("No Child"))
            done.setVisibility(View.INVISIBLE);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("childPrefs", null);
                Type type = new TypeToken<List<Children>>() {}.getType();
                List<Children> childList = gson.fromJson(json, type);
                int i = 0;
                int num = 0;
                while (i < childList.size()) {
                    if (childList.get(i).getName().equals(task_manager.getName(position))) {
                        num = (i + 1) % childList.size();
                        break;
                    }
                    i++;
                }
                if (i == childList.size()) {
                    num = 0;
                }
                task_manager.setName(position,childList.get(num).getName());
                saveNameList(task_manager.getName());
                Intent intent = WhoseTurn.makeIntent(ChangeTurn.this);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupButtonCancel() {
        Button btn = findViewById(R.id.canelChangeTurn);
        if(task_manager.getName(position).equals("No Child"))
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

    public void saveNameList(List<String> n){
        SharedPreferences prefs = this.getSharedPreferences("nameList", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(n);
        editor.putString("nameList", json);
        System.out.println(json);
        editor.commit();
    }

    public static Intent makeChangeIntent(Context context, int position) {
        Intent intent = new Intent(context, ChangeTurn.class);
        intent.putExtra("position_index",position);
        return intent;
    }
}