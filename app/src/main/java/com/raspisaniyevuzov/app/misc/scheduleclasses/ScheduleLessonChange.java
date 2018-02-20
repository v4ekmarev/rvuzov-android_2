package com.raspisaniyevuzov.app.misc.scheduleclasses;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ScheduleLessonChange {
    
    public static final String DB_FIELD_ID = "_id";
    public static final String DB_FIELD_AUTHOR = "author";
    public static final String DB_FIELD_LESSON_ID = "lesson_id";
    public static final String DB_FIELD_LESSON_SUBJECT = "lesson_subject";
    public static final String DB_FIELD_SUBJECT = "subject";
    public static final String DB_FIELD_AUDITORIES = "auditories";
    public static final String DB_FIELD_TIME_START = "time_start";
    public static final String DB_FIELD_TIME_END = "time_end";
    public static final String DB_FIELD_TEACHERS = "teachers";
    public static final String DB_FIELD_GROUPS = "groups";
    public static final String DB_FIELD_PARITY = "parity";
    public static final String DB_FIELD_TYPE = "type";
    public static final String DB_FIELD_DAY = "day";
    public static final String DB_FIELD_DATES = "dates";
    public static final String DB_FIELD_DATE_START = "date_start";
    public static final String DB_FIELD_DATE_END = "date_end";
    public static final String DB_FIELD_CANCELLED = "cancelled";
    public static final String DB_FIELD_CANCEL_DATE = "cancel_date";
    public static final String DB_FIELD_APPLIED = "applied";
    public static final String DB_FIELD_IS_MINE = "is_mine";
    
    private long id;
    private int authorId;
    private String author;
    private String lessonId;
    private String lessonSubject;
    private String subject;
    private List<ScheduleAuditory> auditories;
    private String timeStart;
    private String timeEnd;
    private List<ScheduleTeacher> teachers;
    private List<ScheduleGroup> groups;
    private int parity;
    private int type;
    private int day;
    private Set<String> dates;
    private Date dateStart;
    private Date dateEnd;
    private boolean isCancelled;
    private String cancelDate;
    private boolean isApplied;
    private boolean isMine;
    
    public ScheduleLessonChange(String lessonId){
        this.lessonId = lessonId;
        author = null;
        lessonSubject = null;
        subject = null;
        auditories = null;
        timeStart = null;;
        timeEnd = null;
        teachers = null;
        groups = null;
        parity = -1;
        type = -1;
        day = -1;
        dates = null;
        dateStart = null;
        dateEnd = null;
        isCancelled = false;
        setCancelDate(null);
        isApplied = false;
        isMine = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<ScheduleAuditory> getAuditories() {
        return auditories;
    }

    public void setAuditories(List<ScheduleAuditory> auditories) {
        this.auditories = auditories;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public List<ScheduleTeacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<ScheduleTeacher> teachers) {
        this.teachers = teachers;
    }

    public List<ScheduleGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ScheduleGroup> groups) {
        this.groups = groups;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Set<String> getDates() {
        return dates;
    }

    public void setDates(Set<String> dates) {
        this.dates = dates;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public boolean isApplied() {
        return isApplied;
    }

    public void setApplied(boolean isApplied) {
        this.isApplied = isApplied;
    }

    public String getLessonSubject() {
        return lessonSubject;
    }

    public void setLessonSubject(String lessonSubject) {
        this.lessonSubject = lessonSubject;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }
}