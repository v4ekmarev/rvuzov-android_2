package com.raspisaniyevuzov.app.api.messages.system;

import com.raspisaniyevuzov.app.api.ResponseCodesHelper;
import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.db.dao.MessagesPoolDao;
import com.raspisaniyevuzov.app.db.model.MessagesPool;
import com.raspisaniyevuzov.app.event.ErrorEvent;
import com.raspisaniyevuzov.app.util.DbUtil;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class ErrorMessage extends BaseMessage {

    public Integer code;
    public String msg;

    public ErrorMessage(String msg) {
        super();
        this.msg = msg;
    }

    public ErrorMessage(String id, String data, String cuid) throws JSONException {
        super(id, data, cuid);
        JSONObject jsonObject = new JSONObject(dataObject);
        code = jsonObject.getInt("code");
        msg = jsonObject.getString("msg");
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", this.code);
        jsonObject.put("msg", this.msg);
        return jsonObject;
    }

    @Override
    public void onReceive() {
        if (this.cuid != null) {
            try {
                MessagesPoolDao.getInstance(DbUtil.getRealm()).deleteById(MessagesPool.class, cuid);
                EventBus.getDefault().post(new ErrorEvent());
            } catch (Exception e) {
                // TODO process error
            }
        }
        if (this.code != null)
            ResponseCodesHelper.process(this.code);
    }

}
