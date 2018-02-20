package com.raspisaniyevuzov.app.misc.scheduleclasses;

import java.io.Serializable;

public class ScheduleTeacher extends SortableScheduleClass implements Serializable {

    private static final long serialVersionUID = -1346380572729850617L;

    public ScheduleTeacher(String name, String id) {
        super.setName(name);
        super.setId(id);
    }
    


}
