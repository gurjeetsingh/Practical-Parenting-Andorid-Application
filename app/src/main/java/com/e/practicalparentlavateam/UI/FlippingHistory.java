package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.R;
import com.e.practicalparentlavateam.UI.model.HistoryItem;
import com.e.practicalparentlavateam.UI.model.HistoryManager;

public class FlippingHistory extends AppCompatActivity {
    private HistoryManager manager;
    private ArrayAdapter<HistoryItem> adapter;

    public static Intent makeLaunch(Context c) {
        return new Intent(c, FlippingHistory.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flipping_history);

        populateList();
    }

    private void populateList() {
        manager = HistoryManager.getInstance();
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.HistoryList);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<HistoryItem> {
        public MyListAdapter() {super(FlippingHistory.this, R.layout.history_list, manager.getList());}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.history_list, parent, false);
            }

            HistoryItem currentItem = manager.getList().get(position);

            TextView timeView = (TextView) itemView.findViewById(R.id.item_time);
            timeView.setText(currentItem.getTime());

            TextView numView = (TextView) itemView.findViewById(R.id.item_num);
            numView.setText(currentItem.getChoise());

            ImageView result = (ImageView) itemView.findViewById(R.id.winOrfalse);
            result.setImageResource(currentItem.getId());
            return itemView;
        }
    }
}