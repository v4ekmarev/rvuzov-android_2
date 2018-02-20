package com.raspisaniyevuzov.app.api.messages.service;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.event.ShowPopupInfoEvent;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class PopupInfoMessage extends BaseMessage {

    public PopupInfoMessage() {
        super();
    }

    public enum ActionType {

        SELECT_UNIVERSITY("selectUniversity"), APP_UPDATE("appUpdate"), RESET_APP_DATA("resetAppData");

        public final String action;

        ActionType(String action) {
            this.action = action;
        }

    }

    public PopupInfoMessage(String id, String data, String cuid) throws JSONException {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        try {
            JSONObject jsonObject = new JSONObject(dataObject);
            String text = jsonObject.getString("text");
            String action = jsonObject.getString("action");
            EventBus.getDefault().post(new ShowPopupInfoEvent(action, text));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
