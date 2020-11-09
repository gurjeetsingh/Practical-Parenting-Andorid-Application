/*This activity is to choose the child who will choose the head or tails*/

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Choose_children extends AppCompatActivity {
    private ChildrenManager children;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_children);

        Toolbar toolbar = (Toolbar) findViewById(R.id.choose_child);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //setupChildrenView();
        populateListView();
        registClickCallback();
    }

    /*Show the children list to choose*/
    private void populateListView() {
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        children = ChildrenManager.getInstance();
        List<String> tempList = gson.fromJson(json, type);
        if(tempList != null)
            children.setChildren(tempList);
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listOfChildren);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        public MyListAdapter(){
            super(Choose_children.this, R.layout.children_view_for_list, children.getChildren());
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.children_view_for_list, parent, false);
            }

            String currentChild = children.getChildren().get(position);
            TextView makeView = (TextView)itemView.findViewById(R.id.childList);
            makeView.setText(currentChild);
            return itemView;
        }
    }

    /*Click to choose children*/
    private void registClickCallback(){
        ListView list = (ListView) findViewById(R.id.listOfChildren);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                String clickedName = children.getChildren().get(position);
                /*TextView text = (TextView)findViewById(R.id.name);
                text.setText(clickedName);*/

                Intent intent = CoinFlipActivity.makeIntent2(Choose_children.this, clickedName);
                startActivity(intent);
                finish();
            }
        });
    }

    public static Intent makeIntent2(Context context){
        Intent intent = new Intent(context,Choose_children.class);
        return intent;
    }
}