package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<String> tasks = new ArrayList<>();
    private List<String> name = new ArrayList<>();

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
    public void add (String task, String name){
        tasks.add(task);
        this.name.add(name);
    }

    //remove a task
    public void remove (int index){
        tasks.remove(index);
        name.remove(index);
    }

    public String getTasks (int i) {
        return tasks.get(i);
    }

    public void setTasks (int i, String name) {
        tasks.set(i, name);
    }

    public String getName (int i) {
        return name.get(i);
    }

    public void setName (int i, String name) {
        this.name.set(i, name);
    }

    public int getNumTasks() {
        return tasks.size();
    }

    public List<String> getTasks() {
        return tasks;
    }
    public List<String> getName() {
        return name;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
    public void setName(List<String> name) {
        this.name = name;
    }
}
