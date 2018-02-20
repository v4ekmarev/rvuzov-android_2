package com.raspisaniyevuzov.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.ListMyScheduleActivity;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.schedule.GroupScheduleRequestMessage;
import com.raspisaniyevuzov.app.db.dao.FacultyDao;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.UniversityDAO;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.event.GroupScheduleResponseProcessedEvent;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.MainActivity;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.PrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by SAPOZHKOV on 28.10.2015.
 */
public class DataLoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_loading);

        String groupId = getIntent().getStringExtra(ListMyScheduleActivity.EXTRA_SELECTED_GROUP_ID);

        if (groupId != null)
            Api.sendMessage(new GroupScheduleRequestMessage(groupId));
        else
            onError();
    }

    public void onEvent(GroupScheduleResponseProcessedEvent event) {
        if (event.success && !PrefUtil.isScheduleActivated(this)) {

            PrefUtil.setScheduleActivated(this, true);

            List<University> universityList = UniversityDAO.getInstance().getAll(University.class);
            List<Faculty> facultyList = FacultyDao.getInstance().getAll(Faculty.class);
            List<Group> groupList = GroupDao.getInstance().getAll(Group.class);
            JSONObject eventParams = new JSONObject();
            try {
                eventParams.put("University", !universityList.isEmpty() ? universityList.get(0).getName() : null);
                eventParams.put("Faculty", !facultyList.isEmpty() ? facultyList.get(0).getName() : null);
                eventParams.put("Group", !groupList.isEmpty() ? groupList.get(0).getName() : null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SCHEDULE_LOAD_SUCCESSFUL.type, eventParams);
        } else if (!PrefUtil.isScheduleActivated(this)) {
            Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SCHEDULE_LOAD_FAULT.type);
        }
        if (event.success) {
            startActivity(new Intent(DataLoadingActivity.this, MainActivity.class));
            finish();
        } else
            onError();
    }

    private void onError() {
        Toast.makeText(this, getString(R.string.schedule_is_unavailable), Toast.LENGTH_LONG).show();
        startActivity(new Intent(DataLoadingActivity.this, ListMyScheduleActivity.class));
        finish();
    }

}
