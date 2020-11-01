package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.Model.Child;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        setupFloatingActionButton();
        setupLensView();
    }

    private void setupLensView() {
        // SOURCE: https://developer.android.com/guide/topics/ui/layout/recyclerview
        ListView rv = findViewById(R.id.childListView);

        // Could also use an ArrayAdapter (as in tutorial video)
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return children.getNumChildren();
            }

            @Override
            public Child getItem(int position) {
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

                String name = getItem(position).getName();
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
}

