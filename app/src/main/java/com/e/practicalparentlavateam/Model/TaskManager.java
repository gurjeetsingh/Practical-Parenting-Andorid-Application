package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();

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
    public void add (Task task){
        tasks.add(task);
    }

    public void add (String task, String name){
        tasks.add(new Task(task,name));
    }

    //remove a task
    public void remove (int index){
        tasks.remove(index);
    }

    public Task getTasks (int index) {
        return tasks.get(index);
    }

    public void setTasks (int index, Task task) {
        tasks.set(index, task);
    }

    public void setName(int index, String name){
        tasks.get(index).setName(name);
    }

    public void setTask(int index, String task){
        tasks.get(index).setTask(task);
    }

    public int getNumTasks() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public static void setInstance(TaskManager taskManager){
        instance = taskManager;
    }
}
