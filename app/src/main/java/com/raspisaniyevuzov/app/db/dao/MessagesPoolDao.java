package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.MessagesPool;

import io.realm.Realm;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class MessagesPoolDao extends BaseDAO {

    private static MessagesPoolDao instance;

    public MessagesPoolDao() {
        super();
    }

    public MessagesPoolDao(Realm realm) {
        super(realm);
    }

    public static MessagesPoolDao getInstance() {
        if (instance == null) instance = new MessagesPoolDao();
        return instance;
    }

    public void deleteMessageByCollapseKey(String collapseKey) {
        realm.beginTransaction();
        realm.where(MessagesPool.class).equalTo("collapseKey", collapseKey).findAll().clear();
        realm.commitTransaction();
    }

    public static MessagesPoolDao getInstance(Realm realm) {
        return new MessagesPoolDao(realm);
    }

}
