package com.raspisaniyevuzov.app.ui.task;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.raspisaniyevuzov.app.db.model.File;

import java.util.List;

public class PhotoPageAdapter extends FragmentStatePagerAdapter {

    private final List<File> photos;

    public PhotoPageAdapter(FragmentManager fm, List<File> photos) {
        super(fm);
        this.photos = photos;
    }

    @Override
    public Fragment getItem(int position) {
        return PhotoPageFragment.newInstance(photos.get(position).getName());
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}