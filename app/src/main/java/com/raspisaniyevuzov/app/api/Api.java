package com.raspisaniyevuzov.app.api;

import com.loopj.android.http.RequestParams;
import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.api.messages.user.RegisterMessage;
import com.raspisaniyevuzov.app.api.messages.system.SuccessMessage;
import com.raspisaniyevuzov.app.db.dao.ClientDao;
import com.raspisaniyevuzov.app.db.dao.MessagesPoolDao;
import com.raspisaniyevuzov.app.db.model.MessagesPool;
import com.raspisaniyevuzov.app.event.SyncEvent;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.LogUtil;
import com.raspisaniyevuzov.app.util.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Api {

    private static MessagesFactory factory;

    public static boolean syncInProgress = false;
    private static final int MAX_JSON_SIZE = 1048576;

    static {
        factory = new MessagesFactory();
    }

    public static MessagesFactory getFactory() {
        return factory;
    }

    public static void sendMessage(BaseMessage message) {
        MessagesPoolDao messagesPoolDao = MessagesPoolDao.getInstance(DbUtil.getRealm());
        if (message.getCollapseKey() != null)
            messagesPoolDao.deleteMessageByCollapseKey(message.getCollapseKey());
        try {
            JSONObject json = new JSONObject();
            json.put("type", message.getType());
            json.put("uid", message.getUid());
            json.put("cuid", message.getCuid());
            json.put("data", message.encodeToJSON().toString());
            json.put("time", System.currentTimeMillis() / 1000);

            MessagesPool messagesPool = new MessagesPool();
            messagesPool.setId(message.getUid());
            messagesPool.setType(message.getType());
            messagesPool.setCollapseKey(message.getCollapseKey());
            messagesPool.setJson(json.toString());
            messagesPoolDao.save(messagesPool);

            LogUtil.d(Api.class.getSimpleName(), "saved new msg=" + message.getType());
        } catch (JSONException e) {
            // TODO process
        }
        EventBus.getDefault().post(new SyncEvent());
    }

    public static void sync() {
        if (!syncInProgress) {
            try {
                MessagesPoolDao messagesPoolDao = MessagesPoolDao.getInstance(DbUtil.getRealm());
                List<MessagesPool> messages = messagesPoolDao.getAll(MessagesPool.class);

                syncInProgress = true;
                ArrayList<String> jsons = new ArrayList<>();
                int jsonSize = 0;

                String token = ClientDao.getInstance().getClient().getToken();

                for (int i = 0; i < messages.size(); i++) {
                    MessagesPool message = messages.get(i);
                    // If we have no TOKEN, we can send ONLY Register message
                    if (token != null || message.getType().equals(BaseMessage.getMessageName(RegisterMessage.class))) {
                        if (message.getJson() != null) {
                            jsons.add(message.getJson());
                            jsonSize += message.getJson().length();
                            if (jsonSize > MAX_JSON_SIZE) { // 1 Mb limit
                                // TODO process error
                                break;
                            }
                        }
                    } else {
                        // TODO show error
                    }
                    if (message.getType().equals(BaseMessage.getMessageName(SuccessMessage.class)))
                        messagesPoolDao.deleteById(MessagesPool.class, message.getId());
                }
                RequestParams requestParams = new RequestParams();
                if (token != null)
                    requestParams.put("token", token);
                if (jsons.size() > 0) {
                    requestParams.put("data", jsons.toString());
                    ApiHttpClient.post(Settings.RVUZOV_SERVER_API_URL, requestParams, new ApiServerHttpResponseHandler());
                } else syncInProgress = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else LogUtil.d(Api.class.getSimpleName(), "skip, because sync in progress");
    }

    public static void receiveMessages(JSONArray messages) {
        if (messages != null) {
            for (int i = 0; i < messages.length(); i++) {
                try {
                    JSONObject jsonObject = messages.getJSONObject(i);
                    String uid = null, type = null, cuid = null, data = null;
                    if (jsonObject.has("uid"))
                        uid = jsonObject.getString("uid");
                    if (jsonObject.has("type"))
                        type = jsonObject.getString("type");
                    if (jsonObject.has("data"))
                        data = jsonObject.getString("data");
                    if (jsonObject.has("cuid"))
                        cuid = jsonObject.getString("cuid");
                    BaseMessage msg = Api.getFactory().messageWithJSONObject(uid, type, data, cuid);

                    LogUtil.d(Api.class.getSimpleName(), "receive msg=" + type);

                    if (msg == null)
                        continue;

                    msg.onReceive();

                    LogUtil.d(Api.class.getSimpleName(), "processed msg=" + type);
                } catch (JSONException e) {
                    // TODO proceed
                    e.printStackTrace();
                }
            }
        }
        if (MessagesPoolDao.getInstance(DbUtil.getRealm()).getAll(MessagesPool.class).size() > 0)
            EventBus.getDefault().post(new SyncEvent());
    }

}
