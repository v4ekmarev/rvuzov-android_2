package com.raspisaniyevuzov.app.api.messages.suggest;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class UniversitySuggestRequestMessage extends BaseMessage {

    private String query;

    public UniversitySuggestRequestMessage(String query) {
        super();
        this.query = query;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        return new JSONObject().put("query", query);
    }

}
