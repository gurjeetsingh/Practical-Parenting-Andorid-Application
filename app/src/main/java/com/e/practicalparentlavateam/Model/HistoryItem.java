/*This is the class to define the data of the history item.*/

package com.e.practicalparentlavateam.Model;

public class HistoryItem {
    private String time;
    private String choise;
    private String name;
    private int win_or_Lose;
    private int coin_icon;

    public int getCoinIcon() {
        return coin_icon;
    }

    public void setCoinIcon(int icon) {
        this.coin_icon = icon;
    }

    public void setTime(String t){ time = t;}

    public void setChoise(String c){choise = c;}

    public void setName(String n){name = n;}

    public void setId(int i){win_or_Lose = i;}

    public String getTime() {return time;}

    public String getChoise(){return choise;}

    public String getName(){return name;}

    public int getId(){return win_or_Lose;}

    public HistoryItem(String time, String name, String choise, int win_or_Lose, int coin_icon){
        setTime(time);
        setChoise(choise);
        setName(name);
        setId(win_or_Lose);
        setCoinIcon(coin_icon);
    }
}
