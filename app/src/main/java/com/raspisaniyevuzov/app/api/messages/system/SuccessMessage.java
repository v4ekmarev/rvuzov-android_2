package com.raspisaniyevuzov.app.api.messages.system;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.db.dao.MessagesPoolDao;
import com.raspisaniyevuzov.app.db.model.MessagesPool;
import com.raspisaniyevuzov.app.util.DbUtil;

import org.json.JSONException;

public class SuccessMessage extends BaseMessage {

    public SuccessMessage() {
        super();
    }

    public SuccessMessage(String id, String data, String cuid) throws JSONException {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        if (this.cuid != null) {
            try {
                MessagesPoolDao.getInstance(DbUtil.getRealm()).deleteById(MessagesPool.class, cuid);
            } catch (Exception e) {
                // TODO process error
            }
        }
    }

    @Override
    public String getCollapseKey() {
        return this.uid;
    }

}
