package com.raspisaniyevuzov.app.api.messages.user;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.UserProfileDao;
import com.raspisaniyevuzov.app.db.manager.UserProfileManager;
import com.raspisaniyevuzov.app.event.ProfileResultReceivedEvent;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class ProfileResultMessage extends BaseMessage {

    public ProfileResultMessage() {
        super();
    }

    public ProfileResultMessage(String id, String data, String cuid) {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        super.onReceive();

        boolean success = false;

        try {
            JSONObject result = new JSONObject(dataObject);
            String avatar = result.getString("avatar");
            String name = result.getString("name");
            String email = result.getString("email");
            String group = result.getString("group"); // group Id
            UserProfileManager.updateUserProfile(name, avatar, email, group);
            success = true;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            EventBus.getDefault().post(new ProfileResultReceivedEvent(success));
        }
    }

}