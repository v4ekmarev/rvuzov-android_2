package com.raspisaniyevuzov.app.db.dao;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class AlarmDao extends BaseDAO {

    public AlarmDao() {
        super();
    }

    public static AlarmDao getInstance() {
        return new AlarmDao();
    }

}
