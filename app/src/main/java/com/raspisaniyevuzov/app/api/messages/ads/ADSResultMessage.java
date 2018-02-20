package com.raspisaniyevuzov.app.api.messages.ads;

import com.raspisaniyevuzov.app.RVuzovApp;
import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.event.AdsResultReceivedEvent;
import com.raspisaniyevuzov.app.util.PrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by SAPOZHKOV on 22.12.2015.
 */
public class ADSResultMessage extends BaseMessage {

    public ADSResultMessage() {
        super();
    }

    public ADSResultMessage(String id, String data, String cuid) {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        super.onReceive();
        try {
            String result = null;
            JSONObject data = new JSONObject(dataObject);
            if (!data.isNull("banners")) {
                JSONArray bannersArray = data.getJSONArray("banners");
                if (bannersArray.length() > 0)
                    result = bannersArray.toString();
            }
            PrefUtil.setBanners(RVuzovApp.getContext(), result);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            EventBus.getDefault().post(new AdsResultReceivedEvent());
        }
    }

}
