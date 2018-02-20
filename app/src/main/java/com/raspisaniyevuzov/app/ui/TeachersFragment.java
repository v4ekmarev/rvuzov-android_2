package com.raspisaniyevuzov.app.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raspisaniyevuzov.app.R;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class TeachersFragment extends BaseFragment {

    private static final String TAG = TeachersFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teachers, container, false);

        Toolbar mToolbar = ((BaseActivity) getActivity()).getActionBarToolbar();
        mToolbar.setTitle(null);
        mToolbar.setSubtitle(null);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

}
