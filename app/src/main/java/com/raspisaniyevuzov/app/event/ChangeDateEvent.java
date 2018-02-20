package com.raspisaniyevuzov.app.event;

/**
 * Created by SAPOZHKOV on 25.11.2015.
 */
public class ChangeDateEvent {

    public long selectedDate;

    public ChangeDateEvent(long selectedDate) {
        this.selectedDate = selectedDate;
    }
}
