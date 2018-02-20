package com.raspisaniyevuzov.app.db.dao;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class StudentDao extends BaseDAO {

    public StudentDao() {
        super();
    }

    public static StudentDao getInstance() {
        return new StudentDao();
    }

}
