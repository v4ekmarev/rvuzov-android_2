package com.raspisaniyevuzov.app.db.dbimport;

import java.io.Serializable;

public class AlarmDiscriptionModel implements Serializable {

    private static final long serialVersionUID = 1951136821920412607L;

    public final static String DB_ALARM = "ALARM";

    public long id;
    public int when_show;
    public int hour;
    public int minute;
    public boolean flag_active = true;
    public boolean switch_off_music;
    
    @Override
    public String toString() {
        return "AlarmDiscriptionModel [id=" + id + ", when_show=" + when_show + ", hour=" + hour + ", minute=" + minute + ", flag_active=" + flag_active
                + ", switch_off_music=" + switch_off_music + "]";
    }
    
    

}
