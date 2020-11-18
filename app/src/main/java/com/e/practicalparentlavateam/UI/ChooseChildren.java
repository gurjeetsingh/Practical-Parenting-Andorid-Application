/*This activity is to choose the child who will choose the head or tails*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.e.practicalparentlavateam.Model.Children;
import com.e.practicalparentlavateam.Model.ChildrenManager;
import com.e.practicalparentlavateam.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChooseChildren extends AppCompatActivity {
    private ChildrenManager children;
    private ArrayAdapter<Children> adapter;
    private ChildrenManager current_childrenList = new ChildrenManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_children);

        Toolbar toolbar = (Toolbar) findViewById(R.id.choose_child);
        setSupportActionBar(toolbar);

        //setupChildrenView();
        populateListView();
        registClickCallback();
    }

    /*Show the children list to choose*/
    private void populateListView() {
        SharedPreferences prefs = this.getSharedPreferences("childPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("childPrefs", null);
        Type type = new TypeToken<ChildrenManager>() {}.getType();
        children = ChildrenManager.getInstance();
        ChildrenManager tempList = gson.fromJson(json, type);
        if(tempList != null)
            children.setChildren(tempList.getChildren());

        SharedPreferences sp = getSharedPreferences("Save name",MODE_PRIVATE);
        String LastTimeName = sp.getString("name",null);
        if(LastTimeName == null){
            current_childrenList = children;
            current_childrenList.add(new Children("nobody"));
        }
        else {
            int index = 0;
            for (int i = 0; i < children.getChildren().size(); i++) {
                if (children.getChildren().get(i).getName().equals(LastTimeName))
                    index = (i + 1) % children.getChildren().size();
            }
            while (!children.getChildren().get(index).getName().equals(LastTimeName)) {
                current_childrenList.add(children.getChildren().get(index));
                index = (index + 1) % children.getChildren().size();
            }
            current_childrenList.add(new Children(LastTimeName));
            current_childrenList.add(new Children("nobody"));
        }

        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listOfChildren);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Children> {
        public MyListAdapter(){
            super(ChooseChildren.this, R.layout.children_view_for_list, current_childrenList.getChildren());
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.children_view_for_list, parent, false);
            }

            String currentChild = current_childrenList.getChildren().get(position).getName();
            TextView makeView = (TextView)itemView.findViewById(R.id.childList);
            makeView.setText(currentChild);

            ImageView imageView = (ImageView)itemView.findViewById(R.id.portrait);
            if(currentChild.equals("nobody")){
                imageView.setVisibility(View.INVISIBLE);
            }
            else {
                try {
                    File f = new File(children.getPath(), currentChild + ".jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    imageView.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return itemView;
        }
    }

    /*Click to choose children*/
    private void registClickCallback(){
        ListView list = (ListView) findViewById(R.id.listOfChildren);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                String clickedName = current_childrenList.getChildren().get(position).getName();
                /*TextView text = (TextView)findViewById(R.id.name);
                text.setText(clickedName);*/

                Intent intent = SelectChildren.makeLaunch(ChooseChildren.this, clickedName);
                startActivity(intent);
                finish();
            }
        });
    }

    public static Intent makeIntent2(Context context){
        Intent intent = new Intent(context, ChooseChildren.class);
        return intent;
    }
}