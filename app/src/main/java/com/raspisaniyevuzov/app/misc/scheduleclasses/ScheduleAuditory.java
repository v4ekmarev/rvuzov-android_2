package com.raspisaniyevuzov.app.misc.scheduleclasses;

import java.io.Serializable;

public class ScheduleAuditory extends SortableScheduleClass implements Serializable {

    private static final long serialVersionUID = -1346380572729850617L;
    private final String address;
    
    public ScheduleAuditory(String name, String address, String id) {
        super.setName(name);
        super.setId(id);
        
        this.address = address;
    }
    
    public String getAddress() {
        return address;
    }

    
//    public String toGsonString() {
//       
//        return "DataObject [ auditory_address = "+address+", auditory_name="+getName() +", auditory_id="+getId() + "]";
//    }
    
    

}
