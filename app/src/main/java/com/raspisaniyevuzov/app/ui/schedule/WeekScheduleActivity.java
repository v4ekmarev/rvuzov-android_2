package com.raspisaniyevuzov.app.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.dao.FacultyDao;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.manager.LessonManager;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.Lesson;
import com.raspisaniyevuzov.app.event.ChangeDateEvent;
import com.raspisaniyevuzov.app.event.GroupNotFoundEvent;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class WeekScheduleActivity extends BaseActivity {

    private static final int THRESHOLD = 5;
    private long mSelectedDate;
    private WeekScheduleAdapter mAdapter;
    private List<WeekScheduleObject> items = new ArrayList<>();
    private ListView mlvWeekSchedule;
    private static final int WEEK_SCHEDULE_PART = 14;
    private int mLeftPosition = -WEEK_SCHEDULE_PART, mLeftPosition2 = 0, mRightPosition = 1, mRightPosition2 = WEEK_SCHEDULE_PART;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_schedule);

        List<Group> groups = GroupDao.getInstance().getAll(Group.class);

        Group group;
        if (!groups.isEmpty()) group = groups.get(0);
        else {
            group = null;
            EventBus.getDefault().post(new GroupNotFoundEvent());
        }

        Toolbar toolbar = initToolbarForActivity(group != null ? group.getName() : "");

        List<Faculty> faculties = FacultyDao.getInstance().getAll(Faculty.class);
        Faculty faculty = (!faculties.isEmpty()) ? faculties.get(0) : null;
        toolbar.setSubtitle(faculty != null ? faculty.getName() : "");

        mSelectedDate = getIntent().getLongExtra(DaySchedulePageFragment.EXTRA_SELECTED_DATE, 0);

        mlvWeekSchedule = (ListView) findViewById(R.id.lvWeekSchedule);
        mlvWeekSchedule.setOnScrollListener(new EndlessScrollListener(THRESHOLD) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (getScrollDirection() == SCROLL_DIRECTION_DOWN) {
                    mRightPosition = mRightPosition2;
                    mRightPosition2 += WEEK_SCHEDULE_PART;
                    loadRight();
                } else {
                    mLeftPosition2 = mLeftPosition;
                    mLeftPosition -= WEEK_SCHEDULE_PART;
                    loadLeft(true);
                }
            }

        });

        mAdapter = new WeekScheduleAdapter(items, this);
        mlvWeekSchedule.setAdapter(mAdapter);

        mlvWeekSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().post(new ChangeDateEvent(items.get(position).getDate().getTime()));
//                Intent intent = new Intent();
//                intent.putExtra(DaySchedulePageFragment.EXTRA_SELECTED_DATE, items.get(position).getDate().getTime());
//                setResult(RESULT_OK, intent);
                finish();
            }
        });

        updateSchedule();
        mlvWeekSchedule.setSelection(WEEK_SCHEDULE_PART - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.WEEK_SCHEDULE_SCREEN.type);
    }

    private void updateSchedule() {
        loadLeft(false);
        loadRight();
    }

    private void loadRight() {
        for (int i = mRightPosition; i < mRightPosition2; i++)
            loadScheduleForDay(false, i);
        mAdapter.notifyDataSetChanged();
    }

    private void loadLeft(boolean shouldScroll) {
        for (int i = mLeftPosition2; i > mLeftPosition; i--)
            loadScheduleForDay(true, i);
        mAdapter.notifyDataSetChanged();
        if (shouldScroll)
            mlvWeekSchedule.setSelection(WEEK_SCHEDULE_PART + THRESHOLD + 1);
    }

    private void loadScheduleForDay(boolean left, int i) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mSelectedDate);
        c.add(Calendar.DAY_OF_MONTH, i);

        RealmResults<Lesson> lessons = LessonManager.getLessonsForDate(c.getTime());

        WeekScheduleObject object = new WeekScheduleObject();
        object.setDate(c.getTime());

        if (!lessons.isEmpty()) {
            object.setTime(lessons.get(0).getTimeStart());
            object.setIsWeekend(false);
        } else object.setIsWeekend(true);

        if (!left)
            items.add(object);
        else
            items.add(0, object);
    }

}
