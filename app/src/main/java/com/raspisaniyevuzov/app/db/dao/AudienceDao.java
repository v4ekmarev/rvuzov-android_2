package com.raspisaniyevuzov.app.db.dao;

import io.realm.Realm;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class AudienceDao extends BaseDAO {

    public AudienceDao() {
        super();
    }

    public AudienceDao(Realm realm) {
        super(realm);
    }

    public static AudienceDao getInstance() {
        return new AudienceDao();
    }

}
