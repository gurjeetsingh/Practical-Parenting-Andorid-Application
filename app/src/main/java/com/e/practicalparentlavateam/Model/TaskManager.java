package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> Tasks = new ArrayList<>();

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
        Tasks.add(task);
    }

    public void add (String task, String name){
        Tasks.add(new Task(task,name));
    }

    //remove a task
    public void remove (int index){
        Tasks.remove(index);
    }

    public Task getTasks (int index) {
        return Tasks.get(index);
    }

    public void setTasks (int index, Task task) {
        Tasks.set(index, task);
    }

    public void setName(int index, String name){
        Tasks.get(index).setName(name);
    }

    public void setTask(int index, String task){
        Tasks.get(index).setTask(task);
    }

    public int getNumTasks() {
        return Tasks.size();
    }

    public List<Task> getTasks() {
        return Tasks;
    }

    public static void setInstance(TaskManager taskManager){
        instance = taskManager;
    }
}
