package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.Lesson;

import java.util.Date;

import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class LessonDao extends BaseDAO {

    public LessonDao() {
        super();
    }

    public static LessonDao getInstance() {
        return new LessonDao();
    }

    /**
     * Gets all lessons for selected weekday and week (parity) AND check date (dateStart <=  x <= dateEnd)
     * OR gets all lessons for selected date
     * See https://docs.google.com/document/d/1yVk9Exjo6U8kdhgNmp7sGpp3r_h9B6EpbebhYZ100po/edit#
     *
     * @param weekday - week day
     * @param week    parity, 1 - odd, 2 - even
     * @param date    - selected date
     * @return
     */
    public RealmResults<Lesson> getLessonsForDate(int weekday, int week, Date date) {
        return realm.where(Lesson.class).equalTo("weekday", weekday).beginGroup().equalTo("week", week).or().equalTo("week", 0).endGroup().lessThanOrEqualTo("dateStart", date).greaterThanOrEqualTo("dateEnd", date).or().equalTo("dates.date", date).findAllSorted("timeStart");
    }

}
