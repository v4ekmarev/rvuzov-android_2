package com.raspisaniyevuzov.app.db.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class DateObj extends RealmObject {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
