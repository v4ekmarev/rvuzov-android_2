package com.raspisaniyevuzov.app.ui.task;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.model.File;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.ui.widget.HackyViewPager;

import java.util.List;

public class PhotoViewerFragment extends BaseFragment {

    public static final String PHOTO_POSITION = "photo_position";
    public static final String DELETE_PHOTO_ENABLE = "delete_photo_enable";
    private List<File> photos;
    private int selectedPosition = 0;
    private HackyViewPager mViewPager;
    private PhotoPageAdapter mPhotosAdapter;
    private TextView mTitle;
    private ViewPager.OnPageChangeListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_photo_viewer, container, false);
        mViewPager = (HackyViewPager) view.findViewById(R.id.viewPager);

        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseTaskActivity) getActivity()).onClose();
            }
        });

        mTitle = (TextView) view.findViewById(R.id.title);

        selectedPosition = getArguments().getInt(PHOTO_POSITION, 0);
        boolean deletePhotoEnable = getArguments().getBoolean(DELETE_PHOTO_ENABLE, false);

        FrameLayout flDeletePhoto = (FrameLayout) view.findViewById(R.id.flDeletePhoto);
        flDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.showMessageWithTitleShort(getContext(), getString(R.string.photo_deletion_title), getString(R.string.photo_deletion_message), new CustomDialog.CustomCallback() {
                    @Override
                    public void proceed() {
                        ((BaseTaskActivity) getActivity()).deletePhoto(selectedPosition);
                        Toast.makeText(getContext(), getString(R.string.photo_deleted), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (deletePhotoEnable) flDeletePhoto.setVisibility(View.VISIBLE);
        else flDeletePhoto.setVisibility(View.GONE);

        photos = ((BaseTaskActivity) getActivity()).getFileList();
        mPhotosAdapter = new PhotoPageAdapter(getFragmentManager(), photos);
        mViewPager.setAdapter(mPhotosAdapter);

        mListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updatePhotoCounter(position);
                selectedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };

        mViewPager.addOnPageChangeListener(mListener);

        mViewPager.setCurrentItem(selectedPosition);
        updatePhotoCounter(selectedPosition);

        return view;
    }

    @Override
    public void onDestroyView() {
        mViewPager.removeOnPageChangeListener(mListener);
        super.onDestroyView();
    }

    private void updatePhotoCounter(int position) {
        mTitle.setText(String.format(getString(R.string.photo_viewer_title), position + 1, photos.size()));
    }

    public void setCurrentPhoto(int position) {
        photos = ((BaseTaskActivity) getActivity()).getFileList();
        mPhotosAdapter = new PhotoPageAdapter(getFragmentManager(), photos);
        mViewPager.setAdapter(mPhotosAdapter);
        mPhotosAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(position, false);
        updatePhotoCounter(position);
        selectedPosition = position;
    }

}
