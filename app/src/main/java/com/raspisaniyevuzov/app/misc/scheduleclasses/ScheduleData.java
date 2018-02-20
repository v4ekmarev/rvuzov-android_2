package com.raspisaniyevuzov.app.misc.scheduleclasses;

import com.raspisaniyevuzov.app.db.dbimport.Constants;
import com.raspisaniyevuzov.app.db.dbimport.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduleData implements Serializable {
    private static final long serialVersionUID = -4393688129424482622L;

    public static ScheduleData makeDummyData(String name, String universityShortName) {
        Date emptyDate = new Date();
        return new ScheduleData((List<Pair<Integer, List<ScheduleLesson>>>) null, false,
                new ScheduleGroup(name, ""),
                new ScheduleUniversity(universityShortName, "", "", emptyDate, emptyDate),
                new ScheduleFaculty("", "", 0, 0),
                0, 0, -1, 0);
    }
    
    public final static String DB_SCHEDULE = "SCHEDULE";
    public final static String DB_OBJECT_ID = "OBJECT_ID";
    public final static String DB_OBJECT_NAME = "OBJECT_NAME";
    public final static String DB_MODE = "MODE";
    public final static String DB_IS_MINE = "IS_MINE";
    
    public final static String DB_FACULTY_ID = "FACULTY_ID";
    public final static String DB_FACULTY_NAME = "FACULTY_NAME";
    public final static String DB_FACULTY_DATE_START = "FACULTY_DATE_START";
    public final static String DB_FACULTY_DATE_END = "FACULTY_DATE_END";
    
    public final static String DB_UNIVERSITY_ID = "UNIVERSITY_ID";
    public final static String DB_UNIVERSITY_NAME = "UNIVERSITY_NAME";
    public final static String DB_UNIVERSITY_SHORTNAME = "UNIVERSITY_SHORTNAME";
    public final static String DB_UNIVERSITY_DATE_START = "UNIVERSITY_DATE_START";
    public final static String DB_UNIVERSITY_DATE_END = "UNIVERSITY_DATE_END";

    public final static String DB_UPDATE_TIME = "UPDATE_TIME";
    public final static String DB_LAST_UPDATED_SERVER = "LAST_UPDATED_SERVER";
    public final static String DB_POSITION = "POSITION";
    public final static String DB_ROW_ID = "_id";
    
    
    
    public final static String[] COLUMNS = {
        DB_SCHEDULE,
        DB_OBJECT_NAME,
        DB_OBJECT_ID,
        DB_MODE,
        DB_IS_MINE,
        DB_FACULTY_ID,
        DB_FACULTY_NAME,
        DB_FACULTY_DATE_START,
        DB_FACULTY_DATE_END,
        DB_UNIVERSITY_ID,
        DB_UNIVERSITY_NAME,
        DB_UNIVERSITY_SHORTNAME,
        DB_UNIVERSITY_DATE_START,
        DB_UNIVERSITY_DATE_END,
        DB_LAST_UPDATED_SERVER,
        DB_UPDATE_TIME,
        DB_POSITION,
        DB_ROW_ID
    };

    private List<Pair<Integer, List<ScheduleLesson>>> schedule;
    private ArrayList<ScheduleLesson> scheduleLessonList;
    private ScheduleGroup group;
    private ScheduleTeacher teacher;
    private ScheduleFaculty faculty;
    private ScheduleUniversity university;
    private boolean isMine;
    private long updateTime;
    private long lastUpdatedServer;
    private int position;

    private long rowId;

    public ScheduleData(List<Pair<Integer, List<ScheduleLesson>>> schedule, boolean isMine, SortableScheduleClass groupOrTeacher,
                        ScheduleUniversity university,
                        ScheduleFaculty faculty,
                        long updateTime, long lastUpdatedServer, long rowId, int position) {
        this.schedule = schedule;
        this.university = university;
        this.faculty = faculty;
        this.isMine = isMine;
        this.updateTime = updateTime;
        this.lastUpdatedServer = lastUpdatedServer;
        this.rowId = rowId;
        
        if (groupOrTeacher instanceof ScheduleGroup) {
            this.group = (ScheduleGroup) groupOrTeacher;
        } else {
            this.teacher = (ScheduleTeacher) groupOrTeacher;
        }
    }

    public List<ScheduleLesson> getLessonsList() {
        if(scheduleLessonList != null) {
        	return scheduleLessonList;
        }
    	
    	if (schedule == null) {
            return null;
        }

        final Set<ScheduleLesson> lessons = new HashSet<ScheduleLesson>();
        final int daysCount = schedule.size();
        for (int m = 0; m < daysCount; m++) {
            final List<ScheduleLesson> lessonsData = schedule.get(m).getSecond();
            final int lessonsCount = lessonsData.size();
            for (int i = 0; i < lessonsCount; i++) {
           		lessons.add(lessonsData.get(i));
            }
        }
        scheduleLessonList = new ArrayList<ScheduleLesson>(lessons);
        return scheduleLessonList;
    }
    
    public String getMode() {
        return group != null ? Constants.MODE_GROUP : Constants.MODE_TEACHER;
    }
    
    public SortableScheduleClass getObject() {
        if (group != null) {
            return group;
        } else {
            return teacher;
        }
    }
    
    public long getLastUpdatedServer() {
        return lastUpdatedServer;
    }
    
    public void setLastUpdatedServer(long lastUpdated) {
        this.lastUpdatedServer = lastUpdated;
    }

    public String getObjectId() {
        if (group != null) {
            return group.getId();
        } else {
            return teacher.getId();
        }
    }

    public String getObjectName() {
        if (group != null) {
            return group.getName();
        } else {
            return teacher.getName();
        }
    }
    
    public ScheduleFaculty getFaculty() {
        return faculty;
    }
    
    public void setFaculty(ScheduleFaculty faculty) {
        this.faculty = faculty;
    }
    
    public ScheduleUniversity getUniversity() {
        return university;
    }
    
    public void setUniversity(ScheduleUniversity university) {
        this.university = university;
    }

    public int getPosition() {
        return position;
    }
    
    public long getRowId() {
        return rowId;
    }

    public List<Pair<Integer, List<ScheduleLesson>>> getSchedule() {
        return schedule;
    }

    public String getUniversityId() {
        return university.getId();
    }
    
    public Date getUniversityDateStart() {
        return university.getDateStart();
    }
    
    public Date getUniversityDateEnd() {
        return university.getDateEnd();
    }
    
    public void setUniverstiyDateStart(Date dateStart) {
        university.setDateStart(dateStart);
    }
    
    public void setUniverstiyDateEnd(Date dateEnd) {
        university.setDateEnd(dateEnd);
    }

    public String getUniversityName() {
        return university.getDesctiption();
    }

    public String getUniversityShortName() {
        return university.getName();
    }
    
    public String getFacultyId() {
        return faculty.getId();
    }
    
    public long getFacultyDateStart() {
        return faculty.getDateStart();
    }
    
    public long getFacultyDateEnd() {
        return faculty.getDateEnd();
    }
    
    public String getFacultyName() {
        return faculty.getName();
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public void setGroup(ScheduleGroup group) {
        this.group = group;
        this.teacher = null;
    }
    
    public void setTeacher(ScheduleTeacher teacher) {
        this.teacher = teacher;
        this.group = null;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public void setSchedule(List<Pair<Integer, List<ScheduleLesson>>> schedule) {
        this.schedule = schedule;
    }

    public void setUniversityId(String universityId) {
        university.setId(universityId);
    }

    public void setUniversityName(String universityName) {
        university.setDesctiption(universityName);
    }

    public void setUniversityShortName(String universityShortName) {
        university.setName(universityShortName);
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public boolean equals(Object o) {
        return this.getMode().equals(((ScheduleData) o).getMode())
                && this.getObjectId().equals(((ScheduleData)o).getObjectId());
    }
}
