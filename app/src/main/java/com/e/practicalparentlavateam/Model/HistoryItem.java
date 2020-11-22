/*This is the class to define the data of the history item.*/

package com.e.practicalparentlavateam.Model;

public class HistoryItem {
    private String time;
    private String choise;
    private String name;
    private int winOrLose;
    private int coinIcon;

    public int getCoinIcon() {
        return coinIcon;
    }

    public void setCoinIcon(int icon) {
        this.coinIcon = icon;
    }

    public void setTime(String time){ this.time = time;}

    public void setChoise(String choise){
        this.choise = choise;}

    public void setName(String name){
        this.name = name;}

    public void setId(int id){
        winOrLose = id;}

    public String getTime() {return time;}

    public String getChoise(){return choise;}

    public String getName(){return name;}

    public int getId(){return winOrLose;}

    public HistoryItem(String time, String name, String choise, int winOrLose, int coinIcon){
        setTime(time);
        setChoise(choise);
        setName(name);
        setId(winOrLose);
        setCoinIcon(coinIcon);
    }
}
