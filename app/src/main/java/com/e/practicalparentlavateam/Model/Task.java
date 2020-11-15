package com.e.practicalparentlavateam.Model;

public class Task {
    private String task;
    private String name;

    public void setTask(String t){ task = t;}

    public void setName(String n){name = n;}

    public String getTask() {return task;}

    public String getName(){return name;}

    public Task(String time, String name){
        setTask(time);
        setName(name);
    }
}
