package com.raspisaniyevuzov.app.ui.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Calendar;

/**
 * Created by SAPOZHKOV on 25.09.2015.
 */
public class DaySchedulePagerAdapter extends FragmentStatePagerAdapter {

    public static final int MAX_COUNT = 10000;

    public DaySchedulePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(Object object) {
        return FragmentStatePagerAdapter.POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, position - MAX_COUNT / 2);
        bundle.putLong(DaySchedulePageFragment.EXTRA_SELECTED_DATE, calendar.getTimeInMillis());
        bundle.putInt(DaySchedulePageFragment.EXTRA_SELECTED_INDEX, position);
        DaySchedulePageFragment fragment = new DaySchedulePageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return MAX_COUNT;
    }

}