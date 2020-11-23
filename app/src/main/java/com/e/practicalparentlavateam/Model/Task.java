/*This is the class to define the data of the task item*/

package com.e.practicalparentlavateam.Model;

public class Task {
    private String task;
    private String name;

    public void setTask(String task){ this.task = task;}

    public void setName(String name){
        this.name = name;}

    public String getTask() {return task;}

    public String getName(){return name;}

    public Task(String task, String name){
        setTask(task);
        setName(name);
    }
}
