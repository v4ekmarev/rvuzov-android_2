package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SAPOZHKOV on 13.11.2015.
 */
public class RestorePasswordMessage extends BaseMessage {

    private final String email;

    public RestorePasswordMessage(String email) {
        super();
        this.email = email;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        return jsonObject;
    }

}
