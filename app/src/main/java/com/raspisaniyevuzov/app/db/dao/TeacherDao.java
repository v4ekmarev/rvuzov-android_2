package com.raspisaniyevuzov.app.db.dao;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class TeacherDao extends BaseDAO {

    public TeacherDao() {
        super();
    }

    public static TeacherDao getInstance() {
        return new TeacherDao();
    }

}
