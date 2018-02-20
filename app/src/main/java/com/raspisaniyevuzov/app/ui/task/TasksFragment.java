package com.raspisaniyevuzov.app.ui.task;

import android.content.Intent;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.task.CompletedTaskFragment;
import com.raspisaniyevuzov.app.ui.task.EditTaskActivity;
import com.raspisaniyevuzov.app.ui.task.OpenTaskFragment;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class TasksFragment extends BaseFragment {

    public FloatingActionButton mFab;
    private boolean mLeaveActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        Toolbar mToolbar = ((BaseActivity) getActivity()).getActionBarToolbar();
        mToolbar.setTitle(getString(R.string.fragment_tasks_title));
        mToolbar.setSubtitle(null);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);

            YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.NOT_COMPLETED_TASKS_SCREEN.type);

            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
            tabs.setViewPager(viewPager);
            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 1) {
                        hideFab();
                        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.COMPLETED_TASKS_SCREEN.type);
                    }
                    else {
                        showFab();
                        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.NOT_COMPLETED_TASKS_SCREEN.type);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLeaveActivity) {
                    mLeaveActivity = true;
                    Intent intent = new Intent(getActivity(), EditTaskActivity.class);
                    startActivity(intent);
                }
            }
        });

        showFab();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLeaveActivity = false;
    }

    public void showFab() {
        mFab.animate().scaleX(1).scaleY(1).setDuration(200).setStartDelay(500).start();
    }

    public void hideFab() {
        mFab.animate().scaleX(0).scaleY(0).setDuration(200).setStartDelay(0).start();
    }

    private void setupViewPager(ViewPager viewPager) {
        TaskAdapter adapter = new TaskAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new OpenTaskFragment(), getString(R.string.open_task_fragment_title).toUpperCase());
        adapter.addFragment(new CompletedTaskFragment(), getString(R.string.completed_task_fragment_title).toUpperCase());
        viewPager.setAdapter(adapter);
    }

    private class TaskAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TaskAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

}
