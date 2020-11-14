package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<String> tasks = new ArrayList<>();

    private static TaskManager instance;
    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public TaskManager() {
        //ensuring it is a singleton
    }

    //add a new task
    public void add (String task){
        tasks.add(task);
    }

    //remove a task
    public void remove (int index){
        tasks.remove(index);
    }

    public String get (int i) {
        return tasks.get(i);
    }

    public void set (int i, String name) {
        tasks.set(i, name);
    }

    public int getNumTasks() {
        return tasks.size();
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
}
