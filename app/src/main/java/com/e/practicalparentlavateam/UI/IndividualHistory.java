/*Show the individual history of chosen child*/

package com.e.practicalparentlavateam.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.e.practicalparentlavateam.Model.HistoryItem;
import com.e.practicalparentlavateam.Model.HistoryManager;
import com.e.practicalparentlavateam.R;

public class IndividualHistory extends AppCompatActivity {
    private static final String EXTRA_NAME = "com.e.practicalparentlavateam.UI - the name";
    HistoryManager history_manager = new HistoryManager();
    private ArrayAdapter<HistoryItem> adapter;
    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_history);

        getName();
        getIndividualHistory();
        populateList();
    }

    private void getIndividualHistory() {
        HistoryManager temp = HistoryManager.getInstance();
        for(int i = 0; i < temp.getList().size(); i++){
            if(temp.getList().get(i).getName().equals(name))
                history_manager.add(temp.getList().get(i));
        }
    }

    private void populateList() {
        adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.individualHistory);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<HistoryItem> {
        public MyListAdapter() {super(IndividualHistory.this, R.layout.history_list, history_manager.getList());}

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

    public static Intent makeIntent(Context context, String name){
        Intent intent = new Intent(context, IndividualHistory.class);
        intent.putExtra(EXTRA_NAME, name);
        return intent;
    }

    private void getName(){
        Intent i = getIntent();
        name = i.getStringExtra(EXTRA_NAME);
    }
}