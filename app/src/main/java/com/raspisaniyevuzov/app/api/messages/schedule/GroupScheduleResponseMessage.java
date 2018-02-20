package com.raspisaniyevuzov.app.api.messages.schedule;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.UserProfileDao;
import com.raspisaniyevuzov.app.db.manager.UserProfileManager;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.db.model.UserProfile;
import com.raspisaniyevuzov.app.event.GroupScheduleResponseProcessedEvent;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.LogUtil;
import com.raspisaniyevuzov.app.util.ScheduleHandler;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import de.greenrobot.event.EventBus;

public class GroupScheduleResponseMessage extends BaseMessage {

    public GroupScheduleResponseMessage(String id, String data, String cuid) {
        super(id, data, cuid);
    }

    @Override
    public void onReceive() {
        super.onReceive();
        boolean success = false;
        try {
            DbUtil.clearLessonData();

            JSONObject scheduleObject = new JSONObject(dataObject);
            JSONObject jsonUniversity = scheduleObject.getJSONObject("university");
            JSONObject jsonFaculty = scheduleObject.getJSONObject("faculty");
            String jsonGroupName = scheduleObject.getString("name");

            GroupDao groupDao = GroupDao.getInstance();
            String groupId = ((Group) groupDao.getAll(Group.class).get(0)).getId();

            DbUtil.clearScheduleData();

            // save university
            University university = new University();
            university.setId(jsonUniversity.getString("id"));
            university.setName(jsonUniversity.getString("name"));
            university.setAbbr(jsonUniversity.getString("abbr"));
            university.setIsActive(true);

            // save faculty
            Faculty faculty = new Faculty();
            faculty.setId(jsonFaculty.getString("id"));
            faculty.setName(jsonFaculty.getString("name"));
            faculty.setUniversity(university);

            // save group
            UserProfile profile = UserProfileManager.getCurrentUserProfile();
            Group group = new Group();
            group.setId(profile != null ? profile.getGroup() : groupId);
            group.setName(jsonGroupName);
            group.setFaculty(faculty);
            groupDao.save(group);

            success = ScheduleHandler.proceed(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(scheduleObject.getString("lessons").getBytes()))));
        } catch (Exception e) {
            Mint.logEvent("Schedule parsing exception: msg=" + e.getMessage(), MintLogLevel.Error);
            LogUtil.e(GroupScheduleResponseMessage.class.getSimpleName(), e.getMessage());
        }
        EventBus.getDefault().post(new GroupScheduleResponseProcessedEvent(success));
    }

}
