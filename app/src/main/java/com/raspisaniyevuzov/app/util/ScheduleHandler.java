package com.raspisaniyevuzov.app.util;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.LessonDao;
import com.raspisaniyevuzov.app.db.dao.SubjectDao;
import com.raspisaniyevuzov.app.db.model.Audience;
import com.raspisaniyevuzov.app.db.model.DateObj;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.Lesson;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.db.model.Teacher;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.event.GroupNotFoundEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 22.09.2015.
 */
public class ScheduleHandler {

    public static boolean proceed(BufferedReader reader) {
        boolean success = false;
        JsonReader jsonReader = new JsonReader(reader);

        List<Group> groups = GroupDao.getInstance().getAll(Group.class);

        Group group;
        if (!groups.isEmpty()) group = groups.get(0);
        else {
            EventBus.getDefault().post(new GroupNotFoundEvent());
            return false;
        }

        LessonDao lessonDao = LessonDao.getInstance();

        SubjectDao subjectDao = SubjectDao.getInstance();

        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                Lesson lesson = new Lesson();
                lesson.setId(DbUtil.getNewUid());
                lesson.setGroup(group);

                RealmList<DateObj> dates = new RealmList<>();

                jsonReader.beginObject();

                while (jsonReader.hasNext()) {
                    final String innerName = jsonReader.nextName();

                    if (innerName.equalsIgnoreCase("subject")) {
                        String subjectName = readValue(jsonReader);
                        if (subjectName != null) {
                            Subject subject = subjectDao.getByName(subjectName);
                            if (subject == null) {
                                subject = new Subject();
                                subject.setId(DbUtil.getNewUid());
                                subject.setName(subjectName);
                            }
                            lesson.setSubject(subject);
                        }
                    } else if (innerName.equalsIgnoreCase("subgroups")) {
                        String subgroups = readValue(jsonReader);
                        if (subgroups != null)
                            lesson.setSubgroups(subgroups);
                    } else if (innerName.equalsIgnoreCase("type")) {
                        String type = readValue(jsonReader);
                        if (type != null)
                            lesson.setType(type);
                    } else if (innerName.equalsIgnoreCase("time")) {
                        final boolean isInnerNull = jsonReader.peek() == JsonToken.NULL;
                        if (!isInnerNull) {
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
                                String value = jsonReader.nextName();
                                if (value.equalsIgnoreCase("start")) {
                                    String start = readValue(jsonReader);
                                    if (start != null)
                                        lesson.setTimeStart(TimeUtil.convertTimeStringToTime(start));
                                } else if (value.equalsIgnoreCase("end")) {
                                    String end = readValue(jsonReader);
                                    if (end != null)
                                        lesson.setTimeEnd(TimeUtil.convertTimeStringToTime(end));
                                } else jsonReader.skipValue();
                            }
                            jsonReader.endObject();
                        } else jsonReader.skipValue();
                    } else if (innerName.equalsIgnoreCase("audiences")) {
                        final boolean isInnerNull = jsonReader.peek() == JsonToken.NULL;
                        if (!isInnerNull) {
                            jsonReader.beginArray();
                            RealmList<Audience> audiences = new RealmList<>();

                            while (jsonReader.hasNext()) {
                                jsonReader.beginObject();
                                Audience audience = new Audience();
                                audience.setId(DbUtil.getNewUid());

                                while (jsonReader.hasNext()) {
                                    String value = jsonReader.nextName();
                                    if (value.equalsIgnoreCase("name")) {
                                        String audienceName = readValue(jsonReader);
                                        if (audienceName != null)
                                            audience.setName(audienceName);
                                    } else if (value.equalsIgnoreCase("addr")) {
                                        String audienceAddr = readValue(jsonReader);
                                        if (audienceAddr != null)
                                            audience.setFloor(audienceAddr);
                                    } else jsonReader.skipValue();
                                }
                                audiences.add(audience);
                                jsonReader.endObject();
                            }
                            lesson.setAudience(audiences);
                            jsonReader.endArray();
                        } else jsonReader.skipValue();
                    } else if (innerName.equalsIgnoreCase("teachers")) {
                        final boolean isInnerNull = jsonReader.peek() == JsonToken.NULL;
                        if (!isInnerNull) {
                            jsonReader.beginArray();
                            RealmList<Teacher> teachers = new RealmList<>();

                            while (jsonReader.hasNext()) {
                                jsonReader.beginObject();
                                Teacher teacher = new Teacher();
                                teacher.setId(DbUtil.getNewUid());

                                while (jsonReader.hasNext()) {
                                    if (jsonReader.nextName().equalsIgnoreCase("name")) {
                                        String teacherName = readValue(jsonReader);
                                        if (teacherName != null)
                                            teacher.setName(teacherName);
                                    } else jsonReader.skipValue();
                                }
                                jsonReader.endObject();
                                teachers.add(teacher);
                            }
                            jsonReader.endArray();
                            lesson.setTeacher(teachers);
                        } else jsonReader.skipValue();
                    } else if (innerName.equalsIgnoreCase("date")) {
                        final JsonToken token = jsonReader.peek();
                        if (token != JsonToken.NULL) {
                            if (token == JsonToken.BEGIN_OBJECT) {
                                jsonReader.beginObject();
                                while (jsonReader.hasNext()) {
                                    final String innerInnerName = jsonReader.nextName();
                                    if (innerInnerName.equalsIgnoreCase("start")) {
                                        String start = readValue(jsonReader);
                                        Date startDate;
                                        if (start == null || start.isEmpty()) {
                                            Calendar c = Calendar.getInstance();
                                            c.add(Calendar.YEAR, -1);
                                            startDate = c.getTime();
                                        } else
                                            startDate = TimeUtil.convertStringDateToDate(start);
                                        lesson.setDateStart(startDate);
                                    } else if (innerInnerName.equalsIgnoreCase("end")) {
                                        String end = readValue(jsonReader);
                                        Date endDate;
                                        if (end == null || end.isEmpty()) {
                                            Calendar c = Calendar.getInstance();
                                            c.add(Calendar.YEAR, 1);
                                            endDate = c.getTime();
                                        } else
                                            endDate = TimeUtil.convertStringDateToDate(end);
                                        lesson.setDateEnd(endDate);
                                    } else if (innerInnerName.equalsIgnoreCase("week")) {
                                        String week = readValue(jsonReader);
                                        if (week != null)
                                            lesson.setWeek(Integer.valueOf(week));
                                    } else if (innerInnerName.equalsIgnoreCase("weekday")) {
                                        String weekday = readValue(jsonReader);
                                        if (weekday != null)
                                            lesson.setWeekday(Integer.valueOf(weekday));
                                    } else jsonReader.skipValue();
                                }
                                jsonReader.endObject();
                            } else {
                                String datesArrayString = jsonReader.nextString();
                                Log.d(ScheduleHandler.class.getClass().getSimpleName(), "dates=" + datesArrayString);

                                String[] array = datesArrayString.split(",");
                                for (String anArray : array) {
                                    DateObj dateObj = new DateObj();
                                    dateObj.setDate(TimeUtil.convertStringDateToDate(anArray));
                                    dates.add(dateObj);
                                }
                            }
                        } else jsonReader.skipValue();
                    } else jsonReader.skipValue();
                }
                jsonReader.endObject();
                lesson.setDates(dates);

                lessonDao.save(lesson);
            }
            jsonReader.endArray();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    private static String readValue(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.NULL) {
            return jsonReader.nextString();
        } else {
            jsonReader.skipValue();
        }
        return null;
    }

}
