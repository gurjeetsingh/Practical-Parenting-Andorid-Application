/*This is rhw class to manage the HistoryItem class*/

package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static HistoryManager instance;

    private List<HistoryItem> list = new ArrayList<>();

    public void add(HistoryItem historyItem) {list.add(historyItem);}

    public List<HistoryItem> getList(){return list;}

    public static HistoryManager getInstance() {return instance;}

    public static void setInstance(HistoryManager historyManager){
        if(historyManager == null)
            instance = new HistoryManager();
        else
            instance = historyManager;
    }
}
