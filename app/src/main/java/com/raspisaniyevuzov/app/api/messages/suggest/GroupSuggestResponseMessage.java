package com.raspisaniyevuzov.app.api.messages.suggest;

import com.raspisaniyevuzov.app.api.dto.SuggestDto;
import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.event.GroupSuggestResultReceivedEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class GroupSuggestResponseMessage extends BaseMessage {

    public GroupSuggestResponseMessage(String id, String data, String cuid) {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        super.onReceive();
        String query = "";
        List<SuggestDto> suggestList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(dataObject);
            JSONArray result = jsonObject.getJSONArray("result");
            query = jsonObject.getString("query");

            if (result.length() > 0) {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject suggest = result.getJSONObject(i);
                    SuggestDto suggestDto = new SuggestDto(suggest.getString("id"), suggest.getString("name"), null);
                    suggestList.add(suggestDto);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(new GroupSuggestResultReceivedEvent(suggestList, query));
    }

}
