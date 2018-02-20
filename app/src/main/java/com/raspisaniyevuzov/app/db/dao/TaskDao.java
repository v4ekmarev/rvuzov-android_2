package com.raspisaniyevuzov.app.db.dao;

import com.raspisaniyevuzov.app.db.model.File;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.db.model.Task;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by SAPOZHKOV on 17.09.2015.
 */
public class TaskDao extends BaseDAO {

    public TaskDao() {
        super();
    }

    public static TaskDao getInstance() {
        return new TaskDao();
    }

    public RealmResults<Task> getByComplete(boolean complete) {
        return realm.where(Task.class).equalTo("complete", complete).findAllSorted("dateAdd", Sort.DESCENDING);
    }

    public RealmResults<Task> getOpenTaskBySubject(Subject subject) {
        return realm.where(Task.class).equalTo("subject.name", subject.getName()).equalTo("complete", false).findAllSorted("dateAdd", Sort.DESCENDING);
    }

    public RealmResults<Task> getBySubject(Subject subject) {
        return realm.where(Task.class).equalTo("subject.name", subject.getName()).findAllSorted("dateAdd", Sort.DESCENDING);
    }

    public void setComplete(final Task task, final boolean complete) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                task.setComplete(complete);
            }
        });
    }

    public void updateTask(Task task, String text, Subject subject, RealmList<File> images, boolean complete, Date dateEnd) {
        realm.beginTransaction();
        task.setText(text);
        task.setImages(images);
        task.setComplete(complete);
        task.setDateEnd(dateEnd);
        task.setSubject(subject);
        realm.copyToRealmOrUpdate(task);
        realm.commitTransaction();
    }

}
