package com.raspisaniyevuzov.app.db.dbimport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

public class Message {
    public static final String DB_ID = "_id";
    public static final String DB_AUTHOR_NAME = "author_name";
    public static final String DB_BODY = "body";
    public static final String DB_TYPE = "type";
    public static final String DB_DATE = "date";
    public static final String DB_IS_READ = "is_read";
    public static final String DB_RECIPIENTS_IDS = "recipients_ids";
    
    private long id;
    private String body;
    private String authorName;
    private boolean isRead;
    private String type;
    private long date;
    private String [] recipientsIds;
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    
    public Message(){}
    
    public Message(String type, long date) {
        this.type = type;
        this.date = date;
    }
    
    public Message(long id, String body, String authorName, String type, long date, String[] recipientsIds){
        this.id = id;
        this.body = body;
        this.authorName = authorName;
        this.type = type;
        this.date = date;
        this.isRead = false;
        this.setRecipientsIds(recipientsIds);
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public long getDateLong() {
        return date;
    }
    public Date getDate() {
        return new Date(date * 1000);
    }
    
    public String getDateString() {
        return formatter.format(new Date(date * 1000));
    }
    
    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        String result = "";
        if (!TextUtils.isEmpty(authorName)) {
            result += authorName;
        }
        if (!TextUtils.isEmpty(body)) {
            result += " " + body;
        }
        return result;
    }

    public String [] getRecipientsIds() {
        return recipientsIds;
    }

    public void setRecipientsIds(String [] recipientsIds) {
        this.recipientsIds = recipientsIds;
    }
}
