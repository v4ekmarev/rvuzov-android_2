package com.raspisaniyevuzov.app.db.model;

/**
 * Created by SAPOZHKOV on 23.09.2015.
 */
public enum LessonType {

    PRACTICE(0),
    LAB(1),
    LECTURE(2),
    WORKSHOP(3),
    CONSULTATION(4),
    SELF_WORK(5),
    CREDIT(6),
    EXAM(7),
    PRESENTATION(8),
    MASTER_CLASS(9),
    OPEN_DOORS(10),
    EXCURSION(11),
    MOVIE(12),
    CONCERT(13),
    COMPETITION(14),
    CONFERENCE(15),
    ROUND_TABLE(16),
    OLYMPIAD(17),
    EXHIBITION(18),
    COURSE_WORK(19),
    SCIENCE_SHOW(20),
    SCIENTIFIC_SCHOOL(21);

    public final int type;

    LessonType(int type) {
        this.type = type;
    }

}
