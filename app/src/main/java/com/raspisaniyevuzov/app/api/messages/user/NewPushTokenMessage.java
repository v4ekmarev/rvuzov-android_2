package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.util.DeviceUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SAPOZHKOV on 13.11.2015.
 */
public class NewPushTokenMessage extends BaseMessage {

    private final String pushToken;

    public NewPushTokenMessage(String pushToken) {
        super();
        this.pushToken = pushToken;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("push_token", pushToken);
        jsonObject.put("client_id", DeviceUtil.getAndroidIdMd5());
        return jsonObject;
    }

}
