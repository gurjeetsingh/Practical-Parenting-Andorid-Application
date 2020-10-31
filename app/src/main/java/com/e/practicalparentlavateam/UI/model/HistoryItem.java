package com.e.practicalparentlavateam.UI.model;

public class HistoryItem {
    private String time;
    private String choise;
    private int id;

    public void setTime(String t){ time = t;}

    public void setChoise(String c){choise = c;}

    public void setId(int i){id = i;}

    public String getTime() {return time;}

    public String getChoise(){return choise;}

    public int getId(){return id;}

    public HistoryItem(String time, String choise, int id){
        setTime(time);
        setChoise(choise);
        setId(id);
    }
}
