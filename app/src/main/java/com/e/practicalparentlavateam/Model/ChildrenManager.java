package com.e.practicalparentlavateam.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChildrenManager {

    private List<String> children = new ArrayList<>();

    private static ChildrenManager instance;
    public static ChildrenManager getInstance() {
        if (instance == null) {
            instance = new ChildrenManager();
        }
        return instance;
    }

    private ChildrenManager() {
        //ensuring it is a singleton
    }

    //add a new child
    public void add (String child){
        children.add(child);
    }

    //remove a child
    public void remove (int index){
        children.remove(index);
    }

    public String get (int i) {
        return children.get(i);
    }

    public int getNumChildren() {
        return children.size();
    }

    public List<String> getChildren() {
        return children;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }


//    @Override
//    public Iterator<String> iterator() {
//        return children.iterator();
//    }
}