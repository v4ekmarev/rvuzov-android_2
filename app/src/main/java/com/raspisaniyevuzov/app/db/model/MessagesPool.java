package com.raspisaniyevuzov.app.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessagesPool extends RealmObject {

    @PrimaryKey
    private String id;
    private String collapseKey;
    private String json;
    private String type;
    /**
     * Parent id
     */
    private String cuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollapseKey() {
        return collapseKey;
    }

    public void setCollapseKey(String collapseKey) {
        this.collapseKey = collapseKey;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

}
