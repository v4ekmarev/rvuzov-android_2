package com.raspisaniyevuzov.app.misc.scheduleclasses;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import cz.msebera.android.httpclient.entity.StringEntity;

public class ScheduleLesson extends SortableScheduleClass implements Serializable, Cloneable {
    private static final long serialVersionUID = -8762608048663091154L;

    private static final String LOG_TAG = ScheduleLesson.class.getSimpleName();

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
    private final boolean groupSchedule;
    private boolean isChanged;
    private boolean isCancelled;
    private String cancelDate;

    public ScheduleLesson clone() {
        ScheduleLesson result = null;
        try {
            result = (ScheduleLesson) super.clone();
            result.setAuditories(this.getAuditories());
            result.setTeachers(this.getTeachers());
            result.setDates(this.getDates());
            result.setGroups(this.getGroups());
        } catch (CloneNotSupportedException e) {
            Log.e("ScheduleLesson", "Error clone ScheduleLesson: " + e);
        }
        return result;
    }

    public static StringEntity getFinalDifferenceEntity(JSONObject begin, JSONObject fin) {
        StringEntity entity;
        JSONObject result = new JSONObject();
        try {
            if (begin.getString("time_end").equals(fin.getString("time_end")))
                result.put("time_end", JSONObject.NULL);
            else
                result.put("time_end", fin.getString("time_end"));

            if (begin.getInt("type") == fin.getInt("type"))
                result.put("type", JSONObject.NULL);
            else
                result.put("type", fin.getInt("type"));

            if (begin.getInt("parity") == fin.getInt("parity"))
                result.put("parity", JSONObject.NULL);
            else
                result.put("parity", fin.getString("parity"));

            if (begin.getString("date_start").equals(fin.getString("date_start")))
                result.put("date_start", JSONObject.NULL);
            else
                result.put("date_start", fin.getString("date_start"));

            if (begin.getString("time_start").equals(fin.getString("time_start")))
                result.put("time_start", JSONObject.NULL);
            else
                result.put("time_start", fin.getString("time_start"));

            if (begin.getString("dates").equals(fin.getString("dates")))
                result.put("dates", JSONObject.NULL);
            else
                result.put("dates", fin.getString("dates"));

            if (begin.getString("subject").equals(fin.getString("subject")))
                result.put("subject", JSONObject.NULL);
            else
                result.put("subject", fin.getString("subject"));

            if (begin.getString("date_end").equals(fin.getString("date_end")))
                result.put("date_end", JSONObject.NULL);
            else
                result.put("date_end", fin.getString("date_end"));

            if (begin.getInt("weekday") == fin.getInt("weekday"))
                result.put("weekday", JSONObject.NULL);
            else
                result.put("weekday", fin.getInt("weekday"));

            JSONArray arr = fin.getJSONArray("auditories");
            JSONArray arr1 = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getLong("auditory_id") != 0)
                    arr1.put(obj);
            }

            if (begin.getJSONArray("auditories").equals(arr1))
                result.put("auditories", JSONObject.NULL);
            else
                result.put("auditories", arr1);

            arr = fin.getJSONArray("teachers");
            arr1 = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.getLong("teacher_id") != 0)
                    arr1.put(obj);
            }

            if (begin.getJSONArray("teachers").equals(arr1))
                result.put("teachers", JSONObject.NULL);
            else
                result.put("teachers", arr1);

            result.put("lesson_id", fin.getLong("lesson_id"));
            entity = new StringEntity(result.toString(), "UTF8");
            return entity;
        } catch (Exception e) {
            Log.e("getDifferenceEntity", "Error prepare final JSON:  " + e);
        }
        return null;
    }

    public StringEntity getStringEntity() {
        StringEntity entity = new StringEntity(getJSON().toString(), "UTF8");
        return entity;
    }

    public JSONObject getJSON() {
        try {
            JSONObject json = new JSONObject();

            // NAME & ID
            json.put("subject", getName());
            json.put("lesson_id", Long.parseLong(getId()));

            // AUDITORIES
            JSONArray jaudArr = new JSONArray();
            for (ScheduleAuditory a : auditories) {
                JSONObject jaud = new JSONObject();
                jaud.put("auditory_id", a.getId());
                jaud.put("auditory_name", a.getName());
                jaud.put("auditory_address", a.getAddress());
                jaudArr.put(jaud);
            }
            json.put("auditories", jaudArr);

            // TIME
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

            json.put("time_start", timeStart);
            json.put("time_end", timeEnd);

            // TEACHERS
            JSONArray jteachArr = new JSONArray();
            for (ScheduleTeacher t : teachers) {
                JSONObject jteach = new JSONObject();
                jteach.put("teacher_id", t.getId());
                jteach.put("teacher_name", t.getName());
                jteachArr.put(jteach);
            }
            json.put("teachers", jteachArr);

            // OTHER
            // json.put("parity", parity);
            json.put("type", type);
            json.put("weekday", day);
            json.put("parity", parity);

            // DATES
            JSONArray jdateArr = new JSONArray();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
            dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            for (String d : dates) {
                try {
                    JSONObject jdate = new JSONObject();
                    jdate.put("date", d);
                    jaudArr.put(jdate);
                } catch (Exception e) {
                }
            }
            // json.put("dates", jdateArr);
            json.put("dates", JSONObject.NULL);

            // DATES END & STARS
            if (dateStart == null) {
                json.put("date_start", JSONObject.NULL);
            } else {
                json.put("date_start", dateFormatter.format(dateStart));
            }

            if (dateEnd == null) {
                json.put("date_end", JSONObject.NULL);
            } else {
                json.put("date_end", dateFormatter.format(dateEnd));
            }

            Log.e("GSON", json.toString());
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // @Override
    // public String toString() {
    // return "DataObject [day_of_week=" + day + ", lesson_id=" + getId() +
    // ", subject=" + getName() + ", parity=" + parity + ", type=" + type +
    // ", time_start=" + timeStart + ", time_end="
    // + timeEnd + ", teacher=" + teachers + ", groups=" + groups +
    // ", auditories=" + auditories + ", end_date=" + dateEnd + ", dates=" +
    // dates + ", date_start=" + dateStart + "]";
    // }

    public ScheduleLesson(String name, String id, List<ScheduleAuditory> auditories, String timeStart, String timeEnd, int day, List<ScheduleTeacher> teachers,
                          List<ScheduleGroup> groups, int parity, int type, Set<String> dates, Date dateStart, Date dateEnd, boolean groupSchedule) {
        super.setName(name);
        super.setId(id);
        this.auditories = auditories;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.teachers = teachers;
        this.groups = groups;
        this.parity = parity;
        this.type = type;
        this.groupSchedule = groupSchedule;
        this.day = day;
        this.dateEnd = dateEnd;
        this.dateStart = dateStart;
        this.dates = dates;
        this.isChanged = false;
    }

    public ScheduleLesson copyThisLesson() {
        return new ScheduleLesson(getName(), getId(), auditories, timeStart, timeEnd, day, teachers, groups, parity, type, dates, dateStart, dateEnd,
                groupSchedule);
    }

    @Override
    public int compareTo(SortableScheduleInterface another) {
        if (another instanceof ScheduleLesson) {
            return getTimeStart().compareTo(((ScheduleLesson) another).getTimeStart());
        } else {
            return super.compareTo(another);
        }
    }

    public boolean hasDate(String date) {
        return dates.contains(date);
    }

    public boolean isOngoing(Date date) {
        return isOngoing(date.getTime());
    }

    public boolean isOngoing(long date) {
        if (dateStart == null) {
            if (dateEnd != null) {
                if (dateEnd.getTime() >= date) {
                    return true;
                }
            }
        } else if (dateEnd == null) {
            if (dateStart.getTime() <= date) {
                return true;
            }
        } else {
            if (dateStart.getTime() <= date && dateEnd.getTime() >= date) {
                return true;
            }
        }
        return false;
    }

    public void setParity(int parity) {
        this.parity = parity;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDates(Set<String> dates) {
        this.dates = dates;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Set<String> getDates() {
        return dates;
    }

    public List<ScheduleAuditory> getAuditories() {
        return auditories;
    }


    public int getDay() {
        return day;
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

    public List<ScheduleTeacher> getTeachers() {
        return teachers;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public int getType() {
        return type;
    }

    public boolean isGroupSchedule() {
        return groupSchedule;
    }

    public void setTeachers(List<ScheduleTeacher> teachers) {
        this.teachers = teachers;
    }

    public void setAuditories(List<ScheduleAuditory> auditories) {
        this.auditories = auditories;
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

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public String getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(String cancelDate) {
        this.cancelDate = cancelDate;
    }
}
