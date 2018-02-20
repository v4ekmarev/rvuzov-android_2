package com.raspisaniyevuzov.app.api.messages.schedule;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class AddScheduleRequestMessage extends BaseMessage {

    private final String name;
    private final String email;
    /**
     * student or employee
     */
    private final String type;
    private final String university;

    public AddScheduleRequestMessage(String name, String email, String type, String university) {
        super();
        this.name = name;
        this.email = email;
        this.type = type;
        this.university = university;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("email", email);
        jsonObject.put("type", type);
        jsonObject.put("university", university);
        return jsonObject;
    }

}
