package com.raspisaniyevuzov.app.db.dbimport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.raspisaniyevuzov.app.misc.scheduleclasses.ScheduleData;
import com.raspisaniyevuzov.app.misc.scheduleclasses.ScheduleLessonChange;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "applicationdata";
    public static final String TABLE_NAME_SCHEDULE = "schedule_data";
    public static final String TABLE_NAME_TASKS = "tasks_data";
    public static final String TABLE_NAME_CHANGED_LESSONS = "changed_lessons";
    public static final String TABLE_NAME_USERS_DESCRIPTION_ALARM = "users_settings_alarm";
    public static final String TABLE_NAME_TIMES_RING_ALARMS = "ring_time_alarms";
    public static final String TABLE_NAME_CATALOGS = "catalogs";
    public static final String TABLE_NAME_DEFAULT_LESSONS = "default_lessons";
    public static final String TABLE_NAME_DEPARTMENTS = "departments";
    public static final String TABLE_NAME_MESSAGES = "messages";
    public static final String TABLE_NAME_LESSONS_CHANGES = "lessons_changes";
    private static final int DATABASE_VERSION = 7;

    public static final String KEY_TASK_ROWID = "_id";
    public static final String KEY_TASK_DATA = "data";

    private static final String DATABASE_CREATE_SCHEDULE_DATA = "create table " + TABLE_NAME_SCHEDULE + " ("
            + ScheduleData.DB_ROW_ID                + " integer primary key autoincrement, "
            + ScheduleData.DB_POSITION              + " integer not null, "
            + ScheduleData.DB_SCHEDULE              + " BLOB not null,"
            + ScheduleData.DB_OBJECT_ID             + " text not null, "
            + ScheduleData.DB_OBJECT_NAME           + " text not null, "
            + ScheduleData.DB_MODE                  + " text not null, "
            + ScheduleData.DB_IS_MINE               + " integer not null, "
            + ScheduleData.DB_FACULTY_ID            + " text not null, "
            + ScheduleData.DB_FACULTY_NAME          + " text not null, "
            + ScheduleData.DB_FACULTY_DATE_START    + " integer not null, "
            + ScheduleData.DB_FACULTY_DATE_END      + " integer not null, "
            + ScheduleData.DB_UNIVERSITY_ID         + " text not null, "
            + ScheduleData.DB_UNIVERSITY_NAME       + " text not null, "
            + ScheduleData.DB_UNIVERSITY_SHORTNAME  + " text not null, "
            + ScheduleData.DB_UNIVERSITY_DATE_START + " integer not null, "
            + ScheduleData.DB_UNIVERSITY_DATE_END   + " integer not null, "
            + ScheduleData.DB_LAST_UPDATED_SERVER   + " integer not null, "
            + ScheduleData.DB_UPDATE_TIME           + " integer not null" +
            ");";

    private static final String DATABASE_CREATE_TASKS_DATA = "create table " + TABLE_NAME_TASKS + " ("
            + KEY_TASK_ROWID + " integer primary key autoincrement, "
            + KEY_TASK_DATA  + " BLOB not null);";

    private static final String DATABASE_CREATE_USER_SETTINGS_ALARMS = "create table " + TABLE_NAME_USERS_DESCRIPTION_ALARM + " ("
            + ScheduleData.DB_ROW_ID         + " integer primary key autoincrement, "
            + AlarmDiscriptionModel.DB_ALARM + " BLOB not null);";

    private static final String DATABASE_CREATE_TIME_RING_ALARMS = "create table " + TABLE_NAME_TIMES_RING_ALARMS + " ("
            + ScheduleData.DB_ROW_ID       + " integer primary key autoincrement, "
            + RingAlarmModel.DB_RING_TIMES + " BLOB not null);";

    private static final String DATABASE_CREATE_CATALOGS = "create table " + TABLE_NAME_CATALOGS + " ("
            + ScheduleCatalog.DB_ID            + " text primary key, "
            + ScheduleCatalog.DB_DEPARTMENT_ID + " text not null, "
            + ScheduleCatalog.DB_NAME          + " text, "
            + ScheduleCatalog.DB_OCCUPATION    + " text not null, "
            + ScheduleCatalog.DB_EMAILS        + " text, "
            + ScheduleCatalog.DB_INT_PHONES    + " text,"
            + ScheduleCatalog.DB_EXT_PHONES    + " text,"
            + ScheduleCatalog.DB_URLS          + " text,"
            + ScheduleCatalog.DB_LOCATIONS     + " text);";

    private static final String DATABASE_CREATE_DEPARTMENTS = "create table " + TABLE_NAME_DEPARTMENTS + " ("
            + ScheduleCatalogDepartment.DB_ID   + " text primary key, "
            + ScheduleCatalogDepartment.DB_NAME + " text not null);";

    private static final String DATABASE_CREATE_MESSAGES = "create table " + TABLE_NAME_MESSAGES + " ("
            + Message.DB_ID             + " long primary key, "
            + Message.DB_AUTHOR_NAME    + " text not null, "
            + Message.DB_BODY           + " text not null, "
            + Message.DB_TYPE           + " text not null, "
            + Message.DB_DATE           + " text not null, "
            + Message.DB_IS_READ        + " integer not null, "
            + Message.DB_RECIPIENTS_IDS + " text not null);";

    private static final String DATABASE_CREATE_LESSONS_CHANGES = "create table " +TABLE_NAME_LESSONS_CHANGES + " ("
            + ScheduleLessonChange.DB_FIELD_ID + " integer primary key autoincrement, "
            + ScheduleLessonChange.DB_FIELD_AUTHOR + " text, "
            + ScheduleLessonChange.DB_FIELD_LESSON_ID + " text not null, "
            + ScheduleLessonChange.DB_FIELD_LESSON_SUBJECT + " text not null, "
            + ScheduleLessonChange.DB_FIELD_SUBJECT + " text, "
            + ScheduleLessonChange.DB_FIELD_AUDITORIES + " BLOB, "
            + ScheduleLessonChange.DB_FIELD_TIME_START + " text, "
            + ScheduleLessonChange.DB_FIELD_TIME_END + " text, "
            + ScheduleLessonChange.DB_FIELD_TEACHERS + " BLOB, "
            + ScheduleLessonChange.DB_FIELD_GROUPS + " BLOB, "
            + ScheduleLessonChange.DB_FIELD_PARITY + " integer, "
            + ScheduleLessonChange.DB_FIELD_TYPE + " integer, "
            + ScheduleLessonChange.DB_FIELD_DAY + " integer, "
            + ScheduleLessonChange.DB_FIELD_DATES + " BLOB, "
            + ScheduleLessonChange.DB_FIELD_DATE_START + " BLOB, "
            + ScheduleLessonChange.DB_FIELD_DATE_END + " BLOB, "
            + ScheduleLessonChange.DB_FIELD_CANCELLED + " integer, "
            + ScheduleLessonChange.DB_FIELD_CANCEL_DATE + " text, "
            + ScheduleLessonChange.DB_FIELD_APPLIED + " integer not null, "
            + ScheduleLessonChange.DB_FIELD_IS_MINE + " integer not null);";

    private static final String DATABASE_CREATE_DEFAULT_LESSONS = "create table " + TABLE_NAME_DEFAULT_LESSONS + " ("
            + ScheduleData.DB_ROW_ID + " integer primary key autoincrement, "
            + ScheduleData.DB_SCHEDULE + " BLOB not null);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_SCHEDULE_DATA);
        database.execSQL(DATABASE_CREATE_TASKS_DATA);
        database.execSQL(DATABASE_CREATE_USER_SETTINGS_ALARMS);
        database.execSQL(DATABASE_CREATE_TIME_RING_ALARMS);
        database.execSQL(DATABASE_CREATE_CATALOGS);
        database.execSQL(DATABASE_CREATE_DEPARTMENTS);
        database.execSQL(DATABASE_CREATE_MESSAGES);
        database.execSQL(DATABASE_CREATE_LESSONS_CHANGES);
        database.execSQL(DATABASE_CREATE_DEFAULT_LESSONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }



}
