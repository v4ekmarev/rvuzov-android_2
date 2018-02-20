package com.raspisaniyevuzov.app.misc.scheduleclasses;

import java.io.Serializable;

public class ScheduleGroup extends SortableScheduleClass implements Serializable {

    private static final long serialVersionUID = 4920553853798083539L;

    public ScheduleGroup(String name, String id) {
        super.setName(name);
        super.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        return this.getName().equals(((ScheduleGroup)o).getName()) && this.getId().equals(((ScheduleGroup)o).getId());
    }
}
