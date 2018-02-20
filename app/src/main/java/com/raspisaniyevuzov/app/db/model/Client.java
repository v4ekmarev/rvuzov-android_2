package com.raspisaniyevuzov.app.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class Client extends RealmObject {

    @PrimaryKey
    private String id;

    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
