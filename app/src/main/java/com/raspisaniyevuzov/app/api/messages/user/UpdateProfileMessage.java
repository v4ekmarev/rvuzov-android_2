package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class UpdateProfileMessage extends BaseMessage {

    private final String avatar;
    private final String name;
    private final String email;
    private final String group;

    /**
     *
     * @param avatar
     * @param name
     * @param email
     * @param group - group Id
     */
    public UpdateProfileMessage(String avatar, String name, String email, String group) {
        super();
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.group = group;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("avatar", avatar);
        jsonObject.put("name", name);
        jsonObject.put("email", email);
        jsonObject.put("group", group);
        return jsonObject;
    }

}