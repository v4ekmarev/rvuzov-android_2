package com.raspisaniyevuzov.app.db.dbimport;

import java.io.Serializable;

public class RingAlarmModel implements Serializable, Comparable<SortableRingInterface>, SortableRingInterface {

    @Override
    public String toString() {
        return "RingAlarmModel [ id = " + id + ", position_in_list = " + position_in_list + ", time_ring = " + time_ring + ", messageAlarm = " + messageAlarm
                + ", real_time_lesson = " + real_time_lesson + ", flag_go_to_vibration = " + flag_go_to_vibration + "]";
    }

    private static final long serialVersionUID = 176467099743047459L;
    public final static String DB_RING_TIMES = "TIMES_ALARM";

    public RingAlarmModel(int id, int position_in_list, long time_ring, String messageAlarm, long real_time_lesson, boolean flag_go_to_vibration) {
        this.id = id;
        this.position_in_list = position_in_list;
        this.time_ring = time_ring;
        this.messageAlarm = messageAlarm;
        this.real_time_lesson = real_time_lesson;
        this.flag_go_to_vibration = flag_go_to_vibration;
        // this.before_first_lesson = before_first_lesson;
        // this.is_first_pair = first_pair;
    }

    public int id;
    public int position_in_list;
    public long time_ring;
    public String messageAlarm;
    public long real_time_lesson;
    public boolean flag_go_to_vibration;

    // public boolean before_first_lesson;
    // public boolean is_first_pair;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public long getTime() {
        return time_ring;
    }

    @Override
    public int compareTo(SortableRingInterface another) {
        // TODO Auto-generated method stub
        return ((Long) getTime()).compareTo(another.getTime());
    }

}
