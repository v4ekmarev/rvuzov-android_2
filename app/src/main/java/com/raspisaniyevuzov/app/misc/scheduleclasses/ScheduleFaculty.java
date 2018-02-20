package com.raspisaniyevuzov.app.misc.scheduleclasses;

public class ScheduleFaculty extends SortableScheduleClass {
    private static final long serialVersionUID = -436485262523874418L;

    private long dateStart;
    private long dateEnd;

    public ScheduleFaculty(String name, String id, long dateStart, long dateEnd) {
        super.setId(id);
        super.setName(name);
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }
    
    public long getDateStart() {
        return dateStart;
    }
    
    public long getDateEnd() {
        return dateEnd;
    }
    
    public void setDateEnd(long dateEnd) {
        this.dateEnd = dateEnd;
    }
    
    public void setDateStart(long dateStart) {
        this.dateStart = dateStart;
    }
    
}
