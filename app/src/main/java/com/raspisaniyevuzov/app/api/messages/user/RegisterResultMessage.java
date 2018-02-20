package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.db.dao.ClientDao;
import com.raspisaniyevuzov.app.event.RegisterResultReceivedEvent;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class RegisterResultMessage extends BaseMessage {

    public RegisterResultMessage() {
        super();
    }

    public RegisterResultMessage(String id, String data, String cuid) {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        super.onReceive();
        try {
            ClientDao.getInstance().saveToken(new JSONObject(dataObject).getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            EventBus.getDefault().post(new RegisterResultReceivedEvent());
        }
    }

}