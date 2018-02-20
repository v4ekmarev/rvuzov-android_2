package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class SignUpMessage extends BaseMessage {

    private final String email;
    private final String password;

    public SignUpMessage(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        return jsonObject;
    }

}