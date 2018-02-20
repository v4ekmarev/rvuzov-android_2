package com.raspisaniyevuzov.app.misc.scheduleclasses;

import java.io.Serializable;

public abstract class SortableScheduleClass implements Comparable<SortableScheduleInterface>, Serializable, SortableScheduleInterface {
    private static final long serialVersionUID = -1033009934872617576L;

    private String name;
    private String id;

    @Override
    public int compareTo(SortableScheduleInterface another) {
        return getName().toLowerCase().compareTo(another.getName().toLowerCase());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
