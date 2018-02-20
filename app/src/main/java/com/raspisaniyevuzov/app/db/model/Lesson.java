package com.raspisaniyevuzov.app.db.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class Lesson extends RealmObject {

    @PrimaryKey
    private String id;

    /**
     * Comma-separated values: 123,54,1564...
     */
    private String subgroups;

    /**
     * Time from day start, millis
     */
    private long timeStart;
    /**
     * Time from day start, millis
     */
    private long timeEnd;
    /**
     * even week (2) / odd week (1)
     */
    private int week;
    private java.util.Date dateStart;
    private java.util.Date dateEnd;
    private RealmList<DateObj> dates;
    /**
     * 1 - Monday, ..., 7 - Sunday
     */
    private int weekday;
    private Subject subject;
    private Group group;
    private RealmList<Teacher> teacher;
    private RealmList<Audience> audience;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public java.util.Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(java.util.Date dateStart) {
        this.dateStart = dateStart;
    }

    public java.util.Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(java.util.Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public RealmList<DateObj> getDates() {
        return dates;
    }

    public void setDates(RealmList<DateObj> dates) {
        this.dates = dates;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public RealmList<Teacher> getTeacher() {
        return teacher;
    }

    public void setTeacher(RealmList<Teacher> teacher) {
        this.teacher = teacher;
    }

    public RealmList<Audience> getAudience() {
        return audience;
    }

    public void setAudience(RealmList<Audience> audience) {
        this.audience = audience;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(String subgroups) {
        this.subgroups = subgroups;
    }

}
