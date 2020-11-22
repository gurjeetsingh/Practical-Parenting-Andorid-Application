package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.Task;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditTasksList extends AppCompatActivity {
    TaskManager taskManager;
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tasks_list);

        Toolbar toolbar = findViewById(R.id.EditTaskToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupFloatingActionButton();
        populateListView();
        registerClickCallback();
    }

    private void setupFloatingActionButton() {
        FloatingActionButton floatActionButton = findViewById(R.id.AddTask);
        floatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddTask.makeIntentForAdd(EditTasksList.this);
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        taskManager = TaskManager.getInstance();
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.edit_task_list_view);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.edit_task_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = EditTask.makeEditIntent(EditTasksList.this, position);
                startActivity(intent);
                finish();
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Task> {
        public MyListAdapter() {
            super(EditTasksList.this, R.layout.edit_tasks_list, taskManager.getTasks());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.edit_tasks_list, parent, false);
            }

            String currentTask = taskManager.getTasks(position).getTask();

            TextView makeView = (TextView) itemView.findViewById(R.id.task_name2);
            makeView.setText(currentTask);

            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept: https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainIntent=WhoseTurn.makeIntent(EditTasksList.this);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        EditTasksList.this.finish();
    }

    public static Intent makeLaunch(Context context) {
        return new Intent(context, EditTasksList.class);
    }
}