package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class EditTasksList extends AppCompatActivity {
    TaskManager task_manager;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tasks_list);

        Toolbar toolbar = findViewById(R.id.EditTaskToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        setupFloatingActionButton();
        populateListView();
        registerClickCallback();
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.AddTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddTask.makeIntentForAdd(EditTasksList.this);
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        task_manager = TaskManager.getInstance();
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.EditTaskListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.EditTaskListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                String clickedTask = task_manager.getTasks().get(position);
                Intent intent = EditTask.makeEditIntent(EditTasksList.this, position);
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        public MyListAdapter() {
            super(EditTasksList.this, R.layout.edit_tasks_list, task_manager.getTasks());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.edit_tasks_list, parent, false);
            }

            String current_task = task_manager.getTasks().get(position);

            TextView makeView = (TextView) itemView.findViewById(R.id.TaskName);
            makeView.setText(current_task);

            return itemView;
        }
    }

    public static Intent makeLaunch(Context context) {
        return new Intent(context, EditTasksList.class);
    }
}