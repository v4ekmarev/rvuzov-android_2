package com.raspisaniyevuzov.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.raspisaniyevuzov.app.ListMyScheduleActivity;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.schedule.GroupScheduleRequestMessage;
import com.raspisaniyevuzov.app.db.dao.FacultyDao;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.event.ChangeDateEvent;
import com.raspisaniyevuzov.app.event.ErrorEvent;
import com.raspisaniyevuzov.app.event.GroupNotFoundEvent;
import com.raspisaniyevuzov.app.event.GroupScheduleResponseProcessedEvent;
import com.raspisaniyevuzov.app.ui.schedule.DaySchedulePageFragment;
import com.raspisaniyevuzov.app.ui.schedule.DaySchedulePagerAdapter;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.ui.widget.CustomSwipeRefreshLayout;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.PrefUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;
import com.yandex.metrica.YandexMetrica;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class ScheduleFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static int SELECT_DATE_REQUEST_CODE = 100;
    private ViewPager mViewPager;
    CustomSwipeRefreshLayout mSwipeLayout;
    private DaySchedulePagerAdapter mAdapter;
    private boolean onStart = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        Toolbar toolbar = ((BaseActivity) getActivity()).getActionBarToolbar();

        List<Group> groups = GroupDao.getInstance().getAll(Group.class);

        if (getActivity().isFinishing()) return null;

        Group group;
        if (!groups.isEmpty()) group = groups.get(0);
        else {
            group = null;
            CustomDialog.showMessageWithTitle(getContext(), null, getString(R.string.group_not_found), null, new CustomDialog.CustomCallback() {
                @Override
                public void proceed() {
                    PrefUtil.setLogged(getActivity(), false);
                    DbUtil.clearDb();
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), ListMyScheduleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }

        toolbar.setTitle(group != null ? group.getName() : "");

        List<Faculty> faculties = FacultyDao.getInstance().getAll(Faculty.class);
        Faculty faculty = (!faculties.isEmpty()) ? faculties.get(0) : null;
        toolbar.setSubtitle(faculty != null ? faculty.getName() : "");

        mSwipeLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(new int[]{R.color.accentColor, R.color.primaryColor, R.color.primaryDarkColor, R.color.accentColor});

        mSwipeLayout.setDelegate(this);

        mAdapter = new DaySchedulePagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(DaySchedulePagerAdapter.MAX_COUNT / 2, false);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSwipeLayout.setEnabled(false);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSwipeLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!onStart) {
            mAdapter.notifyDataSetChanged();
        } else onStart = false;
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.SCHEDULE_SCREEN.type);
    }

    @Override
    public void onDestroy() {
        if (mSwipeLayout != null)
            mSwipeLayout.setAcceptEvents(false);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        List<Group> groups = GroupDao.getInstance().getAll(Group.class);

        if (!groups.isEmpty())
            Api.sendMessage(new GroupScheduleRequestMessage(groups.get(0).getId()));
        else {
            mSwipeLayout.setRefreshing(false);
            EventBus.getDefault().post(new GroupNotFoundEvent());
        }
    }

    public void onEvent(GroupScheduleResponseProcessedEvent event) {
        if (event.success) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mViewPager.getAdapter().notifyDataSetChanged();
                }
            });
            Toast.makeText(getContext(), getString(R.string.schedule_updated_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.schedule_updated_error), Toast.LENGTH_SHORT).show();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    public void onEvent(ErrorEvent event) {
        mSwipeLayout.setRefreshing(false);
    }

    public void onEvent(ChangeDateEvent event) {
        long newSelectedDate = event.selectedDate;
        if (newSelectedDate > 0) {
            double diff = newSelectedDate - Calendar.getInstance().getTimeInMillis();
            double days = Math.round(diff / TimeUtil.DAY);
            mViewPager.setCurrentItem((int) (DaySchedulePagerAdapter.MAX_COUNT / 2 + days), false);
        }
    }

    public ScrollView getCurrentScrollView() {
        View view = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        return (view != null) ? (ScrollView) view.findViewById(R.id.scrollView) : null;
    }

}
