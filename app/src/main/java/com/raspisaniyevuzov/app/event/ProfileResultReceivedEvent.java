package com.raspisaniyevuzov.app.event;

/**
 * Created by SAPOZHKOV on 23.10.2015.
 */
public class ProfileResultReceivedEvent {

    public boolean success;

    public ProfileResultReceivedEvent(boolean success) {
        this.success = success;
    }

}

