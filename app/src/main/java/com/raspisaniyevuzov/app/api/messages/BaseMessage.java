package com.raspisaniyevuzov.app.api.messages;

import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.system.SuccessMessage;
import com.raspisaniyevuzov.app.util.DbUtil;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseMessage implements BaseMessageInterface {

    protected String uid, cuid;
    protected String dataObject;

    public BaseMessage() {
        uid = DbUtil.getNewUid();
    }

    public BaseMessage(String id, String data, String cuid) {
        this();
        if (id != null)
            uid = id;
        this.cuid = cuid;
        this.dataObject = data;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        return new JSONObject();
    }

    @Override
    public final void send() {

    }

    @Override
    public void onReceive() {
        if (uid != null) {
            SuccessMessage successMessage = new SuccessMessage();
            successMessage.setCuid(uid);
            Api.sendMessage(successMessage);
        }
    }

    @Override
    public void beforeSend() {
        // TODO implement
    }

    @Override
    public String getCollapseKey() {
        return getMessageName(getClass());
    }

    @Override
    public final String getUid() {
        return uid;
    }

    @Override
    public final void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public final String getType() {
        return getMessageName(getClass());
    }

    @Override
    public final String getCuid() {
        return cuid;
    }

    @Override
    public final void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public static String getMessageName(Class cls) {
        return cls.getSimpleName().replace("Message", "");
    }

}
