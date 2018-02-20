package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.util.DbUtil;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 18.09.2015.
 */
public class BaseDAO {

    protected Realm realm;

    public BaseDAO() {
        this.realm = DbUtil.getRealm();
    }

    public BaseDAO(Realm realm) {
        this.realm = realm;
    }

    public void save(final RealmObject realmObject) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(realmObject);
            }
        });
    }

    public void delete(final RealmObject realmObject) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmObject.removeFromRealm();
            }
        });
    }

    public void deleteById(final Class cl, final String id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(cl).equalTo("id", id).findFirst().removeFromRealm();
            }
        });
    }

    public void deleteAll(final Class cl) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.clear(cl);
            }
        });
    }

    public RealmObject update(RealmObject realmObject) {
        RealmObject result;
        realm.beginTransaction();
        result = realm.copyToRealmOrUpdate(realmObject);
        realm.commitTransaction();
        return result;
    }

    public RealmObject get(String id, Class cl) {
        return realm.where(cl).equalTo("id", id).findFirst();
    }

    public RealmResults getAll(Class cl) {
        RealmQuery res = realm.where(cl);

        return res.findAll();
    }

}
