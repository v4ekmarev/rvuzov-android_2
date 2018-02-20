package com.raspisaniyevuzov.app.api.messages.schedule;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupScheduleRequestMessage extends BaseMessage {

    private String groupId;

    public GroupScheduleRequestMessage(String groupId) {
        super();
        this.groupId = groupId;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        return new JSONObject().put("group_id", groupId);
    }

}
