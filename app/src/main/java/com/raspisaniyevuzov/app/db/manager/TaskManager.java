package com.raspisaniyevuzov.app.db.manager;

import android.content.Context;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.dao.TaskDao;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.db.model.Task;

import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 07.10.2015.
 */
public class TaskManager {

    public static RealmResults<Task> getAllOpen() {
        return TaskDao.getInstance().getByComplete(false);
    }

    public static RealmResults<Task> getAllCompleted() {
        return TaskDao.getInstance().getByComplete(true);
    }

    public static RealmResults<Task> getOpenTaskBySubject(Subject subject) {
        return TaskDao.getInstance().getOpenTaskBySubject(subject);
    }

    public static void complete(Task task) {
        TaskDao.getInstance().setComplete(task, true);
    }

    public static void open(Task task) {
        TaskDao.getInstance().setComplete(task, false);
    }

    public static String getTaskText(Task task, Context context) {
        String text;
        String taskText = task.getText();
        if (taskText != null && !taskText.isEmpty()) text = taskText;
        else
            text = String.format(context.getString(R.string.photo_count), String.valueOf(task.getImages().size()));
        return text;
    }

}
