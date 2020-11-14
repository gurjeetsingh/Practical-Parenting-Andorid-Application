/*This is the class to manage the data of the children*/

package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChildrenManager {

    private List<Children> children = new ArrayList<>();

    private static ChildrenManager instance;
    public static ChildrenManager getInstance() {
        if (instance == null) {
            instance = new ChildrenManager();
        }
        return instance;
    }

    public ChildrenManager() {
        //ensuring it is a singleton
    }

    //add a new child
    public void add (Children child){
        children.add(child);
    }

    //remove a child
    public void remove (int index){
        children.remove(index);
    }

    public Children get (int i) {
        return children.get(i);
    }

    public void set (int i, Children name) {
        children.set(i, name);
    }

    public int getNumChildren() {
        return children.size();
    }

    public List<Children> getChildren() {
        return this.children;
    }

    public void setChildren(List<Children> children) {
        this.children = children;
    }
}