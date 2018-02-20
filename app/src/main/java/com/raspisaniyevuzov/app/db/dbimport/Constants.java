package com.raspisaniyevuzov.app.db.dbimport;

public class Constants {
    public static final int RESULT_FAILED = 999999;
    
    public static final String EXTRA_IS_MINE = "is_mine";
    public static final String EXTRA_IS_FAVORITE = "is_favorite";
    public static final String EXTRA_OBJECT_ID = "object_id";
    public static final String EXTRA_OBJECT_ROW_ID = "object_row_id";
    public static final String EXTRA_OBJECT_NAME = "object_name";
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_PICK_FAVORITE = "pick_favorite";
    public static final String EXTRA_UNIVERSITY_OBJECT = "university_object";
    public static final String EXTRA_FACULTY_OBJECT = "faculty_object";
    public static final String EXTRA_SCHEDULE_OBJECT = "schedule_object";
    public static final String EXTRA_LESSON = "lesson_object";
    public static final String EXTRA_SCHEDULE_LIST_OBJECTS = "schedule_list_objects";
    public static final String EXTRA_TASK_OBJECT = "task_object";
    public static final String EXTRA_TASK_LIST = "task_list";
    public static final String EXTRA_TASK_EDIT_MODE = "task_edit_mode";
    public static final String EXTRA_PARITY_COUNTDOWN = "parity_countdown";
    public static final String EXTRA_DATES_LIST = "dates_list";
    public static final String EXTRA_FACULTY_ID = "faculty_id";
    public static final String EXTRA_CATALOG_DEPARTMENT = "catalog_department";
    public static final String EXTRA_LESSON_CHANGE_ID = "lesson_change_id";
    public static final String EXTRA_LESSON_DATE = "lesson_current_date";

    public static final String EXTRA_SHOW_CURRENT_DEFAULTS = "show_current_defaults";
    
    public static final String MESSAGE_TYPE_CANCEL = "MSG_CANCEL";
    public static final String MESSAGE_TYPE_TASK = "MSG_TASK";
    public static final String MESSAGE_TYPE_TEXT = "MSG_TEXT";

    public static final String MODE_TEACHER = "teacher";
    public static final String MODE_GROUP = "group";

    public static final String PREFERENCE_SHOW_LABS = "schedule_show_labs";
    public static final String PREFERENCE_SHOW_LECTURES = "schedule_show_lectures";
    public static final String PREFERENCE_SHOW_PRACTICE = "schedule_show_practice";
    public static final String PREFERENCE_SHOW_SEMINAR = "schedule_show_seminar";
    public static final String PREFERENCE_SHOW_CONTROL = "schedule_show_control";
    public static final String PREFERENCE_SHOW_EXAM = "schedule_show_exam";
    public static final String PREFERENCE_SHOW_CONSULT = "schedule_show_consult";
    public static final String PREFERENCE_SHOW_SRS = "schedule_show_srs";
    public static final String PREFERENCE_SHOW_EAVESDROP = "schedule_show_eavesdrop";
    public static final String PREFERENCE_SHOW_PRESENTATION = "schedule_show_presntation";
    public static final String PREFERENCE_SHOW_MASTERCLASS = "schedule_show_mastrclass";
    public static final String PREFERENCE_SHOW_OPEN_DOORS = "schedule_show_opendoors";
    public static final String PREFERENCE_SHOW_EXCURSIONS = "schedule_show_excursions";
    public static final String PREFERENCE_SHOW_FILMS = "schedule_show_films";
    public static final String PREFERENCE_SHOW_CONCERTS = "schedule_show_concerts";
    public static final String PREFERENCE_SHOW_COMPETITIONS = "schedule_show_competitions";
    public static final String PREFERENCE_SHOW_CONFERENCES = "schedule_show_conferences";
    public static final String PREFERENCE_SHOW_ROUNDTABLE = "schedule_show_roundtable";
    public static final String PREFERENCE_SHOW_OLYMPIADS = "schedule_show_olympiads";
    public static final String PREFERENCE_SHOW_EXHIBITIONS = "schedule_show_exhibitions";
    public static final String PREFERENCE_SHOW_COURSEWORKS = "schedule_show_courseworks";

    public static final String PREFERENCE_FIRST_RUN_TIME = "application_first_time_run";
    public static final String PREFERENCE_REMINDER_DONT_SHOW_AGAIN = "application_reminder_dont_show_again";

    public static final String PREFERENCE_PARITY_COUNTDOWN = "schedule_parity_countown";

    public static final String PREFERENCE_TASK_SHOW_COMPLETE = "task_show_complete";
    public static final String PREFERENCE_TASK_SHOW_INCOMPLETE = "task_show_incomplete";

    public static final String PREFERENCE_INVITATION_SHOWN = "invitation_shown";
    public static final String PREFERENCE_INVITATION_INVITED = "invitation_invited";
    public static final String PREFERENCE_INVITATION_USER_ID = "invitation_user_id";
    public static final String PREFERENCE_INVITATION_TOKEN = "invitation_token";
    
    public static final String PREFERENCE_UNIVERSITY_ID = "university_id";
    public static final String PREFERENCE_UNIVERSITY_NAME = "university_name";
    public static final String PREFERENCE_FACULTY_NAME = "faculty_name";
    public static final String PREFERENCE_GROUP_NAME = "group_name";
    public static final String PREFERENCE_TEACHER_NAME = "teacher_name";
    
    public static final String PREFERENCE_APP_VERSION = "appVersion";
    public static final String PREFERENCE_REG_ID = "registration_id";

    public static final String PREFERENCE_CATALOG_LAST_UPDATE = "catalog_update_in";

    public static final int RESULT_REMOVED = 10001;

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";

    public static final String EVENT_CHOOSE_UNIVERSITY = "CHOOSE_UNIVERSITY";
    public static final String EVENT_CHOOSE_FACULTY = "CHOOSE_FACULTY";
    public static final String EVENT_CHOOSE_SCHEDULE_TEACHER = "CHOOSE_SCHEDULE_TEACHER";
    public static final String EVENT_CHOOSE_SCHEDULE_GROUP = "CHOOSE_SCHEDULE_GROUP";
    public static final String EVENT_ACTIONBAR_MARK_FAVORITE = "ACTIONBAR_MARK_FAVORITE";
    public static final String EVENT_ACTIONBAR_REMOVE_FAVORITE = "ACTIONBAR_REMOVE_FAVORITE";
    public static final String EVENT_MENU_TASK_ADD = "MENU_TASK_ADD";
    public static final String EVENT_MENU_MY_SCHEDULE = "MENU_MY_SCHEDULE";
    public static final String EVENT_MENU_TASK_LIST = "MENU_TASK_LIST";
    public static final String EVENT_MENU_ALARMS_LIST = "MENU_ALARMS_LIST";
    public static final String EVENT_MENU_SHARE = "MENU_SHARE";
    public static final String EVENT_CONTEXT_REPORT_ERROR_CLICK = "CONTEXT_REPORT_ERROR_CLICK";
    public static final String EVENT_CONTEXT_REPORT_ERROR_SEND = "CONTEXT_REPORT_ERROR_SEND";
    public static final String EVENT_CONTEXT_AUDITORY = "CONTEXT_AUDITORY";
    public static final String EVENT_CONTEXT_ADD_TASK = "CONTEXT_ADD_TASK";
    public static final String EVENT_CONTEXT_TEACHER_SCHEDULE = "CONTEXT_TEACHER_SCHEDULE";
    public static final String EVENT_CONTEXT_GROUP_SCHEDULE = "CONTEXT_GROUP_SCHEDULE";
    public static final String EVENT_CONTEXT_TASK_ADD = "CONTEXT_TASK_ADD";
    public static final String EVENT_LESSON_HAS_TASK_CLICK = "LESSON_HAS_TASK_CLICK";
    public static final String EVENT_ACTIONBAR_TODAY = "ACTIONBAR_TODAY";
    public static final String EVENT_ACTIONBAR_SCHEDULE_SETTINGS = "ACTIONBAR_SCHEDULE_SETTINGS";
    public static final String EVENT_SCHEDULE_PREFERENCES_CHANGE_DEFAULTS = "SCHEDULE_PREFERENCES_CHANGE_DEFAULTS";
    public static final String EVENT_SCHEDULE_PREFERENCES_SUPPORT = "SCHEDULE_PREFERENCES_SUPPORT";
    public static final String EVENT_SUPPORT_SHARE = "SCHEDULE_PREFERENCES_SHARE";
    public static final String EVENT_SUPPORT_WRITE_TO_DEVELOPERS = "SUPPORT_WRITE_TO_DEVELOPERS";
    public static final String EVENT_LIST_TASKS_ADD_NEW = "LIST_TASKS_ADD_NEW";
    public static final String EVENT_TASK_SHARE = "TASK_SHARE";
    public static final String EVENT_TASK_CHANGE_STATUS = "TASK_CHANGE_STATUS";
    public static final String EVENT_TASK_REMOVE = "TASK_REMOVE";
    public static final String EVENT_TASK_ADD_PHOTO = "TASK_ADD_PHOTO";
    public static final String EVENT_TASK_PREFERENCES = "TASK_PREFERENCES";
    public static final String EVENT_UPDATE_SCHEDULE_CHOOSE = "UPDATE_SCHEDULE_CHOOSE";
    public static final String EVENT_UPDATE_SCHEDULE_PULL = "UPDATE_SCHEDULE_PULL";
    public static final String EVENT_NO_SCHEDULE_SUBSCRIBE = "NO_SCHEDULE_SUBSCRIBE";
    public static final String EVENT_INVITE_VK_GROUPS = "INVITE_VK_GROUPS";
    public static final String EVENT_INVITE_VK_FRIENDS = "INVITE_VK_FRIENDS";
    public static final String EVENT_INVITE_CLOSE = "INVITE_CLOSE";
    public static final String EVENT_INVITE_EMAIL_FRIENDS = "INVITE_EMAIL_FRIENDS";

    public static final String EVENT_HEARING_LIST = "HEARING_LIST";
    public static final String EVENT_HEARING_SHOW = "HEARING_SHOW";
    public static final String EVENT_HEARING_VOTE = "HEARING_VOTE";
    public static final String EVENT_HEARING_SHARE = "HEARING_SHARE";
    public static final String EVENT_HEARING_SEND = "HEARING_SEND";
    public static final String EVENT_HEARING_DIALOG_SEND = "HEARING_DIALOG_SEND";
    public static final String EVENT_RATE_SHOW = "RATE_APP_SHOW";
    public static final String EVENT_RATE_CLICK = "RATE_APP_CLICK";
    
    public static final String EVENT_MENU_FAVORITE_LIST = "MENU_FAVORITE_LIST";
    public static final String EVENT_MENU_FAVORITE_ADD = "MENU_FAVORITE_ADD";
    public static final String EVENT_ACTIONBAR_FAVORITE_ADD = "ACTION_BAR_FAVORITE_ADD";
    public static final String EVENT_FAVORITE_CLICK = "FAVORITE_CLICK";
    
    public static final String EVENT_CHOOSE_DEPARTMENT = "CHOOSE_DEPARTMENT";
    public static final String EVENT_LIST_DEPARTMENTS_ADD_NEW = "LIST_DEPARTMENTS_ADD_NEW";
    public static final String EVENT_MENU_STAFF_ADD = "MENU_STAFF_ADD";
    public static final String EVENT_MENU_CATALOG_CLICK = "MENU_STAFF_CLICK";
    public static final String EVENT_LIST_STAFF_ADD_NEW = "LIST_STAFF_ADD_NEW";
    public static final String EVENT_REPORT_ERROR_MESSAGE = "REPORT_ERROR_MESSAGE";
    public static final String EVENT_SHARE_STAFF = "SHARE_STAFF";
    
    public static final String EVENT_MENU_MESSAGES_CLICK = "MENU_MESSAGES_CLICK";
    public static final String EVENT_MENU_MESSAGES_CREATE = "MENU_MESSAGES_CREATE";
    public static final String EVENT_MESSAGES_CREATE = "MESSAGES_CREATE";

    public static final String EVENT_MENU_EDITS_CLICK = "MENU_EDITS_CLICK";
   
    public static String[] choise_when = { "Только перед 1-й парой", "Перед каждой парой" };
    public static int ALARM_BEFORE_FIRST_LESSON = 0;
    public static int ALARM_BEFORE_EVERY_LESSON = 1;
    
    public static long DAY_TO_MILLISECOND = 86400000;
    public static long HOUR_TO_MILLISECOND = 3600000;
    public static long MINUTE_TO_MILLISECOND = 60000;
}
