/*This activity is to see the child you already have and decide to add or
* edit children's name*/

package com.e.practicalparentlavateam.UI;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;

public class ConfigureChildren extends AppCompatActivity {
    // Arbitrary numbers for startActivityForResult:
    private static final int ACTIVITY_RESULT_ADD = 101;
    private static final int ACTIVITY_RESULT_EDIT = 102;

    private ChildrenManager children;
    private ArrayAdapter<Children> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_children);

        Toolbar toolbar = (Toolbar) findViewById(R.id.configure_toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        children = ChildrenManager.getInstance();
        getChild();

        setupFloatingActionButton();
        //setupChildrenView();
        populateListView();
        registClickCallback();
    }

    public void getChild(){
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<ChildrenManager>() {}.getType();
        ChildrenManager temp = gson.fromJson(json, type);
        if(temp != null)
            ChildrenManager.setInstance(temp);
        children = ChildrenManager.getInstance();
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = ChildDetails.makeIntentForAdd(ConfigureChildren.this);
                ConfigureChildren.this.startActivityForResult(i, ACTIVITY_RESULT_ADD);
            }
        });
    }

    private void populateListView() {
        children = ChildrenManager.getInstance();
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.child_list_view);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Children> {
        public MyListAdapter(){
            super(ConfigureChildren.this, R.layout.children_view_for_list, children.getChildren());
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.children_view_for_list, parent, false);
            }

            String currentChild = children.getChildren().get(position).getName();
            TextView makeView = (TextView)itemView.findViewById(R.id.child_list);
            makeView.setText(currentChild);

            ImageView imageView = (ImageView)itemView.findViewById(R.id.portrait);
            try {
                File file=new File(children.getPath(), currentChild + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            return itemView;
        }
    }

    private void registClickCallback(){
        ListView list = (ListView) findViewById(R.id.child_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Intent intent = EditChild.makeEditIntent(ConfigureChildren.this, position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configure, menu);
        return true;
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

    @Override
    public void onBackPressed() {
        //clear the old stack
        //Resource used to understand concept:
        // https://stackoverflow.com/questions/5794506/android-clear-the-back-stack
        Intent mainIntent = MainMenu.makeIntent(ConfigureChildren.this);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        ConfigureChildren.this.finish();
    }

    //Returning Necessary Activity
    public static Intent makeIntent(Context context) {
        Intent configintent = new Intent(context,ConfigureChildren.class);
        return configintent;
    }

}

