package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;

import io.realm.Realm;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class GroupDao extends BaseDAO {

    public GroupDao() {
        super();
    }

    public static GroupDao getInstance() {
        return new GroupDao();
    }

    public void setName(final Group group, final String name) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                group.setName(name);
            }
        });
    }

    public void updateGroup(final Group group, final String name, final Faculty faculty) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                group.setFaculty(faculty);
                group.setName(name);
            }
        });
    }

}
