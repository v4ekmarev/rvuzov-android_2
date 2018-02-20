package com.raspisaniyevuzov.app.db.dbimport;

import com.raspisaniyevuzov.app.misc.scheduleclasses.SortableScheduleClass;

public class ScheduleCatalogDepartment extends SortableScheduleClass {
    private static final long serialVersionUID = 4331313824976751566L;
    
    public static final String DB_ID = "_id";
    public static final String DB_NAME = "department_name";
    
    public ScheduleCatalogDepartment(){}
    
    public ScheduleCatalogDepartment(String id, String name){
        super.setId(id);
        super.setName(name);
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if(o instanceof ScheduleCatalogDepartment
                && this.getId().equals(((ScheduleCatalogDepartment) o).getId()) 
                && this.getName().equals(((ScheduleCatalogDepartment) o).getName())) {
            isEqual = true;
        }
        return isEqual;
    }
    
    
}
