package com.raspisaniyevuzov.app.util;

import android.content.Context;

import com.raspisaniyevuzov.app.RVuzovApp;
import com.raspisaniyevuzov.app.db.dao.AlarmDao;
import com.raspisaniyevuzov.app.db.dao.AudienceDao;
import com.raspisaniyevuzov.app.db.dao.DateObjDao;
import com.raspisaniyevuzov.app.db.dao.FacultyDao;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.LessonDao;
import com.raspisaniyevuzov.app.db.dao.MessagesPoolDao;
import com.raspisaniyevuzov.app.db.dao.StudentDao;
import com.raspisaniyevuzov.app.db.dao.SubjectDao;
import com.raspisaniyevuzov.app.db.dao.TaskDao;
import com.raspisaniyevuzov.app.db.dao.TeacherDao;
import com.raspisaniyevuzov.app.db.dao.UniversityDAO;
import com.raspisaniyevuzov.app.db.dbimport.DbAdapterScheduleTask;
import com.raspisaniyevuzov.app.db.model.Alarm;
import com.raspisaniyevuzov.app.db.model.Audience;
import com.raspisaniyevuzov.app.db.model.DateObj;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.File;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.Lesson;
import com.raspisaniyevuzov.app.db.model.MessagesPool;
import com.raspisaniyevuzov.app.db.model.Student;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.db.model.Teacher;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.misc.scheduleclasses.ScheduleTask;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by SAPOZHKOV on 23.09.2015.
 */
public class DbUtil {

    public static void clearDb() {
        UniversityDAO.getInstance().deleteAll(University.class);
        AlarmDao.getInstance().deleteAll(Alarm.class);
        AudienceDao.getInstance().deleteAll(Audience.class);
        FacultyDao.getInstance().deleteAll(Faculty.class);
        GroupDao.getInstance().deleteAll(Group.class);
        LessonDao.getInstance().deleteAll(Lesson.class);
        StudentDao.getInstance().deleteAll(Student.class);

        StudentDao.getInstance().deleteAll(Student.class);
//        SubjectDao.getInstance().deleteAll(Subject.class);
//        TaskDao.getInstance().deleteAll(Task.class); // delete tasks ONLY after Logout!
        TeacherDao.getInstance().deleteAll(Teacher.class);
//        FileDao.getInstance().deleteAll(File.class);
        DateObjDao.getInstance().deleteAll(DateObj.class);
        MessagesPoolDao.getInstance().deleteAll(MessagesPool.class);
//        ClientDao.getInstance().deleteAll(Client.class);  // delete tasks ONLY after Logout!
    }

    public static void clearScheduleData() {
        UniversityDAO.getInstance().deleteAll(University.class);
        FacultyDao.getInstance().deleteAll(Faculty.class);
        GroupDao.getInstance().deleteAll(Group.class);
    }

    public static void clearLessonData() {
        LessonDao.getInstance().deleteAll(Lesson.class);
    }

    /**
     * MongoDB requirements
     */
    public static String getNewUid() {
        return UUID.randomUUID().toString();
    }

    public static Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    /**
     * Import data from old database version
     */
    public static void importDataFromOldAppVersionDatabase(Context context) {
        if (!PrefUtil.dataHasAlreadyImported(context)) {
            DbAdapterScheduleTask dbAdapterScheduleTask = new DbAdapterScheduleTask(context);
            dbAdapterScheduleTask = dbAdapterScheduleTask.open();
            List<ScheduleTask> tasks = dbAdapterScheduleTask.fetchAllEntries();

            if (tasks.size() > 0) {
                TaskDao taskDao = TaskDao.getInstance();
                for (ScheduleTask task : tasks) {
                    Task taskForSave = new Task();
                    taskForSave.setId(DbUtil.getNewUid());
                    taskForSave.setComplete(task.isComplete());
                    taskForSave.setText(task.getDescription());

                    String subjectName = task.getLessonName();

                    SubjectDao subjectDao = SubjectDao.getInstance();

                    Subject subject = subjectDao.getByName(subjectName);
                    if (subject == null) {
                        subject = new Subject();
                        subject.setId(DbUtil.getNewUid());
                        subject.setName(subjectName);
                    }
                    taskForSave.setSubject(subject);

                    RealmList<File> images = new RealmList<>();
                    List<String> uris = task.getImagesURI();
                    for (String uri : uris) {
                        com.raspisaniyevuzov.app.db.model.File file = new com.raspisaniyevuzov.app.db.model.File();
                        file.setId(DbUtil.getNewUid());
                        file.setName(uri);
                        images.add(file);
                    }
                    taskForSave.setImages(images);
                    taskDao.save(taskForSave);
                }
            }
            PrefUtil.setDataImported(context, true);
        }
    }

}
