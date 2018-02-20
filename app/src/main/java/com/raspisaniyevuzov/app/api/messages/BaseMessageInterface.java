package com.raspisaniyevuzov.app.api.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by SAPOZHKOV on 23.10.2015.
 */
public interface BaseMessageInterface {

    String getType();

    String getCollapseKey();

    void beforeSend();

    void send();

    void onReceive();

    String getCuid();

    void setCuid(String cuid);

    String getUid();

    void setUid(String uid);

    JSONObject encodeToJSON() throws JSONException;

}
