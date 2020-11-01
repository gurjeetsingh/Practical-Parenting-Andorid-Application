package com.e.practicalparentlavateam.UI.model;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static HistoryManager instance;

    private List<HistoryItem> list = new ArrayList<>();

    public void add(HistoryItem h) {list.add(h);}

    public List<HistoryItem> getList(){return list;}

    public static HistoryManager getInstance() {return instance;}

    public static void setInstance(HistoryManager h){
        if(h == null)
            instance = new HistoryManager();
        else
            instance = h;
    }
}
