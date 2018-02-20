package com.raspisaniyevuzov.app.api.messages.task;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskListRequestMessage extends BaseMessage {

    private String query;

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        return new JSONObject();
    }

}
