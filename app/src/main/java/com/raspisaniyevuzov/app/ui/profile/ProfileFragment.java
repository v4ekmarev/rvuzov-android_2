package com.raspisaniyevuzov.app.ui.profile;

import android.os.Bundle;
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
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.astuetz.PagerSlidingTabStrip;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class ProfileFragment extends BaseFragment {

    private boolean mLeaveActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar mToolbar = ((BaseActivity) getActivity()).getActionBarToolbar();
        mToolbar.setTitle(getString(R.string.fragment_profile_title));
        mToolbar.setSubtitle(null);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.AUTHORIZE_SCREEN.type);

            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
            tabs.setViewPager(viewPager);
            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_IN_FORM_OPEN.type);
                        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.AUTHORIZE_SCREEN.type);
                    }
                    else {
                        Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_UP_FORM_OPEN.type);
                        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.REGISTER_SCREEN.type);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLeaveActivity = false;
    }

    private void setupViewPager(ViewPager viewPager) {
        TaskAdapter adapter = new TaskAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new SignInFragment(), getString(R.string.sign_in_fragment_title).toUpperCase());
        adapter.addFragment(new SignUpFragment(), getString(R.string.sign_up_fragment_title).toUpperCase());
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
