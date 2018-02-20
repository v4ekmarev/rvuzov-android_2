package com.raspisaniyevuzov.app.api.messages.service;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;

public class TestMessage extends BaseMessage {

    public TestMessage() {
        super();
    }

    public TestMessage(String id, String data, String cuid) throws JSONException {
        super(id, data, cuid);
    }

}
