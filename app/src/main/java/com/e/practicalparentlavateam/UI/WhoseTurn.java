package com.e.practicalparentlavateam.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.Model.Task;
import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class WhoseTurn extends AppCompatActivity {
    TaskManager taskManager;
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whose_turn);

        Toolbar toolbar = findViewById(R.id.whose_turn_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getTasks();
        populateListView();
        registerClickCallback();
    }

    private void getTasks() {
        SharedPreferences prefs = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("taskPrefs", null);
        taskManager = TaskManager.getInstance();
        Type type = new TypeToken<TaskManager>() {}.getType();
        TaskManager temp = gson.fromJson(json, type);
        if(temp != null)
            taskManager = temp;
        TaskManager.setInstance(taskManager);
    }

    private void populateListView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.task_list_view);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.task_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = ChangeTurn.makeChangeIntent(WhoseTurn.this, position);
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<Task> {
        public MyListAdapter() {
            super(WhoseTurn.this, R.layout.tasks_list, taskManager.getTasks());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.tasks_list, parent, false);
            }

            String currentTask = taskManager.getTasks().get(position).getTask();
            String currentName = taskManager.getTasks().get(position).getName();

            TextView taskView = (TextView) itemView.findViewById(R.id.task_name);
            taskView.setText(currentTask);

            SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("childPrefs", null);
            Type type = new TypeToken<ChildrenManager>() {}.getType();
            ChildrenManager child_list = gson.fromJson(json, type);
            TextView nameView = (TextView) itemView.findViewById(R.id.NameOfChild);
            if(child_list == null || child_list.getChildren().size() == 0){
                taskManager.setName(position,"No Child");
                saveNewTask(taskManager);
                nameView.setText("No Child");
            }
            else {
                if (currentName.equals("No Child")) {
                    taskManager.setName(position, child_list.get(0).getName());
                    saveNewTask(taskManager);
                    nameView.setText(child_list.get(0).getName());
                } else {
                    nameView.setText(currentName);
                }
            }

            return itemView;
        }
    }

    public void saveNewTask(TaskManager t){
        SharedPreferences preferences = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(t);
        editor.putString("taskPrefs", json);
        System.out.println(json);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_whose_turn,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.action_edit:
                //edit the tasks list
                Intent intent = EditTasksList.makeLaunch(WhoseTurn.this);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept: https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainIntent = MainMenu.makeIntent(WhoseTurn.this);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        WhoseTurn.this.finish();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, WhoseTurn.class);
    }
}