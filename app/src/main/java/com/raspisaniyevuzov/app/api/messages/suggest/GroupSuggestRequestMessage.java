package com.raspisaniyevuzov.app.api.messages.suggest;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupSuggestRequestMessage extends BaseMessage {

    private String query;
    private String facultyId;

    public GroupSuggestRequestMessage(String query, String facultyId) {
        super();
        this.query = query;
        this.facultyId = facultyId;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        return new JSONObject().put("query", query).put("faculty_id", facultyId);
    }

}
