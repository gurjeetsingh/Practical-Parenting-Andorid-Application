package com.e.practicalparentlavateam.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigureChildren extends AppCompatActivity {
    // Arbitrary numbers for startActivityForResult:
    private static final int ACTIVITY_RESULT_ADD = 101;
    private static final int ACTIVITY_RESULT_EDIT = 102;
    private static final int ACTIVITY_RESULT_CALCULATE = 103;

    private ChildrenManager children;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_children);

        Toolbar toolbar = (Toolbar) findViewById(R.id.configureToolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        children = ChildrenManager.getInstance();
        getChildList();
        //children.add("Gurjeet");



        setupFloatingActionButton();
        setupChildrenView();
    }

    private void setupChildrenView() {
        // SOURCE: https://developer.android.com/guide/topics/ui/layout/recyclerview
        ListView rv = findViewById(R.id.childListView);
        getChildList();

        // Could also use an ArrayAdapter (as in tutorial video)
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return children.getNumChildren();
            }

            @Override
            public String getItem(int position) {
                return children.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;   // unused
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.children_view_for_list, parent, false);
                }

                String name = getItem(position);
                ((TextView) convertView)
                        .setText(name);
                return convertView;
            }
        };
        rv.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configure, menu);
        return true;
    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent configintent = new Intent(context,ConfigureChildren.class);
        return configintent;
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ChildDetails.makeIntentForAdd(ConfigureChildren.this);
                ConfigureChildren.this.startActivityForResult(i, ACTIVITY_RESULT_ADD);
            }
        });
    }

    public void  getChildList(){
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        children = ChildrenManager.getInstance();
        List<String> tempList = gson.fromJson(json, type);
        if(tempList != null)
            children.setChildren(tempList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_ADD:
            case ACTIVITY_RESULT_EDIT:
                adapter.notifyDataSetChanged();
                break;
        }
    }
}

