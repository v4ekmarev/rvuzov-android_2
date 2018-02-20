package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.util.DeviceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class RegisterMessage extends BaseMessage {

    public RegisterMessage() {
        super();
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lang", Locale.getDefault().getLanguage());
        jsonObject.put("device", DeviceUtil.getDeviceName());
        jsonObject.put("os", DeviceUtil.getOS());
        jsonObject.put("v", DeviceUtil.getVersion());
        jsonObject.put("client_id", DeviceUtil.getAndroidIdMd5());
        return jsonObject;
    }

}