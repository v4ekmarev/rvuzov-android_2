package com.raspisaniyevuzov.app.db.model;

/**
 * Created by SAPOZHKOV on 18.09.2015.
 */
public abstract class BaseModel {

//    @PrimaryKey
//    private String id;
//
//    public void save() {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.copyToRealm(BaseModel.this);
//            }
//        });
//    }
//
//    public void delete() {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                BaseModel.this.removeFromRealm();
//            }
//        });
//    }
//
//    public static RealmObject get(String id, Realm realm) {
//        return realm.where(BaseModel.class).equalTo("id", id).findFirst();
//    }
//
//    public void update(RealmObject realmObject, Realm realm) {
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.copyToRealmOrUpdate(BaseModel.this);
//            }
//        });
//    }

}
