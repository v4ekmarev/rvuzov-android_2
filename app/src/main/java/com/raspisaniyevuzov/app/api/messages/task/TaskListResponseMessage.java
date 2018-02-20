package com.raspisaniyevuzov.app.api.messages.task;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.event.TaskListResultReceivedEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class TaskListResponseMessage extends BaseMessage {

    public TaskListResponseMessage(String id, String data, String cuid) {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        super.onReceive();
        try {
            JSONArray result = new JSONObject(dataObject).getJSONArray("result");

            if (result.length() > 0) {
                for (int i = 0; i < result.length(); i++) {
                    // TODO proceed result!
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new TaskListResultReceivedEvent());
    }

}
