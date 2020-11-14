/*This is the flipping history which show the history of the flipping*/

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
import com.e.practicalparentlavateam.Model.HistoryItem;
import com.e.practicalparentlavateam.Model.HistoryManager;

public class FlippingHistory extends AppCompatActivity {
    private HistoryManager history_manager;
    private ArrayAdapter<HistoryItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flipping_history);

        populateList();
    }

    private void populateList() {
        history_manager = HistoryManager.getInstance();
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.HistoryList);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<HistoryItem>{
        public MyListAdapter() {super(FlippingHistory.this, R.layout.history_list, history_manager.getList());}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.history_list, parent, false);
            }

            HistoryItem currentItem = history_manager.getList().get(position);

            TextView timeView = (TextView) itemView.findViewById(R.id.item_time);
            timeView.setText(currentItem.getTime());

            TextView choiceView = (TextView) itemView.findViewById(R.id.item_num);
            choiceView.setText(currentItem.getChoise());

            TextView nameView = (TextView) itemView.findViewById(R.id.item_name);
            nameView.setText(currentItem.getName());

            ImageView result = (ImageView) itemView.findViewById(R.id.winOrfalse);
            result.setImageResource(currentItem.getId());

            ImageView coin = (ImageView) itemView.findViewById(R.id.coinResult);
            coin.setImageResource(currentItem.getCoinIcon());
            return itemView;
        }
    }

    public static Intent makeLaunch(Context c) {
        return new Intent(c, FlippingHistory.class);
    }
}