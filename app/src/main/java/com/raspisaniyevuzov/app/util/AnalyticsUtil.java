package com.raspisaniyevuzov.app.util;

/**
 * Created by SAPOZHKOV on 16.12.2015.
 */
public class AnalyticsUtil {

    public enum AmplitudeEventType {
        UNIVERSITY_SEARCH_OPEN("university_search_open"), FACULTY_SEARCH_OPEN("faculty_search_open"), GROUP_SEARCH_OPEN("group_search_open"),
        UNIVERSITY_SEARCH_SUCCESSFUL("university_search_successful"), FACULTY_SEARCH_SUCCESSFUL("faculty_search_successful"), GROUP_SEARCH_SUCCESSFUL("group_search_successful"),
        UNIVERSITY_SEARCH_FAULT("university_search_fault"), FACULTY_SEARCH_FAULT("faculty_search_fault"), GROUP_SEARCH_FAULT("group_search_fault"),
        SCHEDULE_LOAD("schedule_load"), SCHEDULE_LOAD_SUCCESSFUL("schedule_load_successful"), SCHEDULE_LOAD_FAULT("schedule_load_fault"),
        RESTORE_PASSWORD("restore_password"), SIGN_IN_FORM_OPEN("sign_in_form_open"), SIGN_UP_FORM_OPEN("sign_up_form_open"),
        SIGN_IN_SUCCESSFUL("sign_in_successful"), SIGN_UP_SUCCESSFUL("sign_up_successful"),
        SIGN_IN_FAULT("sign_in_fault"), SIGN_UP_FAULT("sign_up_fault"),
        SEND_FORM_OPEN("send_form_open"), SEND_FORM_SUCCESSFUL("send_form_successful"), SEND_FORM_FAULT("send_form_fault"), BANNER_SHOW("banner_show"), BANNER_CLICK("banner_click"), REMOVE_AD_CLICK("remove_ad_click");

        public String type;

        AmplitudeEventType(String type) {
            this.type = type;
        }
    }

    public enum YandexMetricaEventType {
        START_SCREEN("screen_FirstLaunch"),
        SCHEDULE_SCREEN("screen_Schedule"),
        WEEK_SCHEDULE_SCREEN("screen_WeekSchedule"),
        LESSON_SCREEN("screen_Lesson"),
        EDIT_TASK_SCREEN("screen_EditTask"),
        TASK_SCREEN("screen_Task"),
        REGISTER_SCREEN("screen_Register"),
        AUTHORIZE_SCREEN("screen_Authorize"),
        PROFILE_SCREEN("screen_Profile"),
        NOT_COMPLETED_TASKS_SCREEN("screen_not_completed_tasks"),
        COMPLETED_TASKS_SCREEN("screen_completed_tasks"),
        US_YMETRICA_ADVERTISEMENT_REMOVE_CLICKED("remove_ad_click"),
        US_YMETRICA_SUCESSFUL_NOADVERTISEMENT_PURCHASE("purchase_no_advertisement_sucessful");

        public String type;

        YandexMetricaEventType(String type) {
            this.type = type;
        }
    }

}
