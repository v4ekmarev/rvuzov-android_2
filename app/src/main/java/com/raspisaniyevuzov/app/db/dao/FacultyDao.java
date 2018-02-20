package com.raspisaniyevuzov.app.db.dao;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class FacultyDao extends BaseDAO {

    public FacultyDao() {
        super();
    }

    public static FacultyDao getInstance() {
        return new FacultyDao();
    }

}
