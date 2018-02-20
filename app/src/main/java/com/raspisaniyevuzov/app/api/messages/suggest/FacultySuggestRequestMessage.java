package com.raspisaniyevuzov.app.api.messages.suggest;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FacultySuggestRequestMessage extends BaseMessage {

    private String query;
    private String universityId;

    public FacultySuggestRequestMessage(String query, String universityId) {
        super();
        this.query = query;
        this.universityId = universityId;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        return new JSONObject().put("query", query).put("university_id", universityId);
    }

}
