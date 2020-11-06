/*This is the class to define the data of the history item*/

package com.e.practicalparentlavateam.Model;

public class HistoryItem {
    private String time;
    private String choise;
    private String name;
    private int id;

    public void setTime(String t){ time = t;}

    public void setChoise(String c){choise = c;}

    public void setName(String n){name = n;}

    public void setId(int i){id = i;}

    public String getTime() {return time;}

    public String getChoise(){return choise;}

    public String getName(){return name;}

    public int getId(){return id;}

    public HistoryItem(String time, String name, String choise, int id){
        setTime(time);
        setChoise(choise);
        setName(name);
        setId(id);
    }
}
