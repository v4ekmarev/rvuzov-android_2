package com.raspisaniyevuzov.app.event;

/**
 * Created by SAPOZHKOV on 09.11.2015.
 */
public class ShowPopupInfoEvent {

    public String action;
    public String text;

    public ShowPopupInfoEvent(String action, String text) {
        this.action = action;
        this.text = text;
    }

}
