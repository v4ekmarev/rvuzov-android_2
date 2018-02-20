package com.raspisaniyevuzov.app.event;

/**
 * Created by SAPOZHKOV on 06.11.2015.
 */
public class GroupScheduleResponseProcessedEvent {

    public final boolean success;

    public GroupScheduleResponseProcessedEvent(boolean success) {
        this.success = success;
    }

}
