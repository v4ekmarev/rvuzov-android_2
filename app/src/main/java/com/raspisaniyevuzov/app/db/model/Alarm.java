package com.raspisaniyevuzov.app.db.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class Alarm extends RealmObject {

    @PrimaryKey
    private String id;
    private Date time;
    private String text;
//    private RealmObject parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public RealmObject getParent() {
//        return parent;
//    }
//
//    public void setParent(RealmObject parent) {
//        this.parent = parent;
//    }

}
