package com.raspisaniyevuzov.app.db.manager;

import com.raspisaniyevuzov.app.db.dao.LessonDao;
import com.raspisaniyevuzov.app.db.model.Lesson;
import com.raspisaniyevuzov.app.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 18.09.2015.
 */
public class LessonManager {

    public static RealmResults<Lesson> getLessonsForDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        int weekday = c.get(Calendar.DAY_OF_WEEK) - 1; // 1 - SUNDAY, not MONDAY!
        return LessonDao.getInstance().getLessonsForDate(weekday == 0 ? 7 : weekday, TimeUtil.isWeekEven(date.getTime()) ? 2 : 1, TimeUtil.resetHoursAndMinutes(c).getTime());
    }

}
