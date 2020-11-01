package com.e.practicalparentlavateam.UI.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChildrenManager implements Iterable<Child>{
    private List<Child> children = new ArrayList<>();

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
    public void add (Child child){
        children.add(child);
    }

    //remove a child
    public void remove (Child child){
        children.remove(child);
    }

    public Child get (int i) {
        return children.get(i);
    }

    public int getNumChildren() {
        return children.size();
    }


    @Override
    public Iterator<Child> iterator() {
        return children.iterator();
    }
}