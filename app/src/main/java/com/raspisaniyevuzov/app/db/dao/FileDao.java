package com.raspisaniyevuzov.app.db.dao;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class FileDao extends BaseDAO {

    public FileDao() {
        super();
    }

    public static FileDao getInstance() {
        return new FileDao();
    }

}
