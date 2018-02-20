package com.raspisaniyevuzov.app.misc.scheduleclasses;

import java.util.Date;

public class ScheduleUniversity extends SortableScheduleClass {
    private static final long serialVersionUID = -436485262523874418L;

    private String desctiption;
    private Date dateStart;
    private Date dateEnd;

    public ScheduleUniversity(String name, String description, String id, Date dateStart, Date dateEnd) {
        super.setId(id);
        super.setName(name);
        this.desctiption = description;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public String getDesctiption() {
        return desctiption;
    }
    
    public Date getDateStart() {
        return dateStart;
    }
    
    public Date getDateEnd() {
        return dateEnd;
    }
    
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
    
    public void setDesctiption(String desctiption) {
        this.desctiption = desctiption;
    }
    
    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }
    
}
