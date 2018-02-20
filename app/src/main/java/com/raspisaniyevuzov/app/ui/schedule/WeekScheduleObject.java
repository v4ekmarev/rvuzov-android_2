package com.raspisaniyevuzov.app.ui.schedule;

import java.util.Date;

/**
 * Created by SAPOZHKOV on 25.09.2015.
 */
public class WeekScheduleObject {

    /**
     * Day without lessons
     */
    private boolean isWeekend;

    private Date date;

    /**
     * Lesson start time, seconds
     */
    private long time;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

}
