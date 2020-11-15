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

import com.e.practicalparentlavateam.Model.TaskManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class WhoseTurn extends AppCompatActivity {
    TaskManager taskManager;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whose_turn);

        Toolbar toolbar = findViewById(R.id.WhoseTurnToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        getTasks();
        getName();
        populateListView();
        registerClickCallback();
    }

    private void getTasks() {
        SharedPreferences prefs = this.getSharedPreferences("taskPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("taskPrefs", null);
        taskManager = TaskManager.getInstance();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> temp = gson.fromJson(json, type);
        if(temp != null)
            taskManager.setTasks(temp);
    }

    private void getName() {
        SharedPreferences prefs = this.getSharedPreferences("nameList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("nameList", null);
        taskManager = TaskManager.getInstance();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> temp = gson.fromJson(json, type);
        if(temp != null)
            taskManager.setName(temp);
    }

    private void populateListView() {
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.TaskListView);
        list.setAdapter(adapter);
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.TaskListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = ChangeTurn.makeChangeIntent(WhoseTurn.this, position);
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        public MyListAdapter() {
            super(WhoseTurn.this, R.layout.tasks_list, taskManager.getTasks());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.tasks_list, parent, false);
            }

            String currentTask = taskManager.getTasks().get(position);
            String currentName = taskManager.getName().get(position);

            TextView taskView = (TextView) itemView.findViewById(R.id.TaskName);
            taskView.setText(currentTask);

            TextView nameView = (TextView) itemView.findViewById(R.id.NameOfChild);
            if(currentName.equals("No Child")) {
                SharedPreferences prefs = getSharedPreferences("childPrefs", MODE_PRIVATE);
                Gson gson = new Gson();
                String json = prefs.getString("childPrefs", null);
                Type type = new TypeToken<List<String>>() {}.getType();
                List<String> childList = gson.fromJson(json, type);
                if(childList != null && childList.size() != 0) {
                    taskManager.setName(position,childList.get(0));
                    nameView.setText(childList.get(0));
                }
                else{
                    nameView.setText("No Child");
                }
            }
            else{
                nameView.setText(currentName);
            }

            return itemView;
        }
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

    public static Intent makeIntent(Context context) {
        return new Intent(context, WhoseTurn.class);
    }

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept: https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainintent=MainMenu.makeIntent(WhoseTurn.this);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainintent);
        WhoseTurn.this.finish();
    }
}