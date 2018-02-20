/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.raspisaniyevuzov.app.ui.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.raspisaniyevuzov.app.ListMyScheduleActivity;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.dto.SuggestDto;
import com.raspisaniyevuzov.app.api.messages.user.UpdateProfileMessage;
import com.raspisaniyevuzov.app.db.dao.FacultyDao;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.UniversityDAO;
import com.raspisaniyevuzov.app.db.manager.UserProfileManager;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.db.model.UserProfile;
import com.raspisaniyevuzov.app.event.UpdateAvatarEvent;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.MainActivity;
import com.raspisaniyevuzov.app.ui.auth.DataLoadingActivity;
import com.raspisaniyevuzov.app.ui.auth.search.BaseSearchFragment;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.ui.widget.ImageChooserDialog;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.FileUtil;
import com.raspisaniyevuzov.app.util.ImageUtil;
import com.raspisaniyevuzov.app.util.Settings;
import com.yandex.metrica.YandexMetrica;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class UserLoggedFragment extends BaseFragment implements BaseSearchFragment.Searchable, ImageChooserDialog.OnImageChooserResultListener {

    private FloatingActionButton fabPhoto;
    private BaseSearchFragment searchFragment;
    private TextView tvSelectedUniversity, tvSelectedFaculty, tvSelectedGroup;
    private SuggestDto selectedFaculty, selectedGroup, selectedUniversity;
    private ImageView ivAvatar;
    private EditText edtName;
    private TextView tvEmail;
    private ImageView dummyAvatar;
    private boolean mHasChanges = false;
    private String mUserAvatar;
    private String mGroupName;
    private String mGroupId;
    private ProgressDialog mProgressDialog;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_user_logged, container, false);

        context = getActivity();

        tvSelectedUniversity = (TextView) view.findViewById(R.id.tvSelectedUniversity);
        tvSelectedUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearch(BaseSearchFragment.SearchType.UNIVERSITY.type, false, null);
            }
        });

        tvSelectedFaculty = (TextView) view.findViewById(R.id.tvSelectedFaculty);
        tvSelectedFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedUniversity == null)
                    Toast.makeText(getContext(), getString(R.string.university_not_selected), Toast.LENGTH_SHORT).show();
                else
                    showSearch(BaseSearchFragment.SearchType.FACULTY.type, true, selectedUniversity.id);
            }
        });

        tvSelectedGroup = (TextView) view.findViewById(R.id.tvSelectedGroup);
        tvSelectedGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFaculty == null)
                    Toast.makeText(getContext(), getString(R.string.faculty_not_selected), Toast.LENGTH_SHORT).show();
                else
                    showSearch(BaseSearchFragment.SearchType.GROUP.type, true, selectedFaculty.id);
            }
        });

        UserProfile userProfile = UserProfileManager.getCurrentUserProfile();

        if (userProfile != null) {

            fabPhoto = (FloatingActionButton) view.findViewById(R.id.fabPhoto);
            fabPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageChooserDialog dialog = ImageChooserDialog.newInstance();
                    dialog.setTargetFragment(UserLoggedFragment.this, 1);
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                }
            });

            ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);

            dummyAvatar = (ImageView) view.findViewById(R.id.dummyAvatar);

            mUserAvatar = userProfile.getAvatar();

            if (mUserAvatar != null && !mUserAvatar.isEmpty()) {
                dummyAvatar.setVisibility(View.GONE);
                ImageUtil.displayImage(userProfile.getAvatar(), ivAvatar, getContext());
            } else dummyAvatar.setVisibility(View.VISIBLE);

            edtName = (EditText) view.findViewById(R.id.edtName);

            edtName.setText(userProfile.getName());

            edtName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    saveChangesLocally();
                    ((MainActivity) getActivity()).updateUserData();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            tvEmail = (TextView) view.findViewById(R.id.tvEmail);
            tvEmail.setText(userProfile.getEmail());

            // display schedule info
            List<Group> groups = GroupDao.getInstance().getAll(Group.class);
            List<Faculty> faculties = FacultyDao.getInstance().getAll(Faculty.class);
            List<University> universities = UniversityDAO.getInstance().getAll(University.class);

            if (groups != null && !groups.isEmpty()) {
                Group group = groups.get(0);
                mGroupName = group.getName();
                tvSelectedGroup.setText(mGroupName);
                mGroupId = group.getId();
                selectedGroup = new SuggestDto(group.getId(), group.getName(), null);
            }
            if (faculties != null && !faculties.isEmpty()) {
                Faculty faculty = faculties.get(0);
                tvSelectedFaculty.setText(faculty.getName());
                selectedFaculty = new SuggestDto(faculty.getId(), faculty.getName(), null);
            }
            if (universities != null && !universities.isEmpty()) {
                University university = universities.get(0);
                tvSelectedUniversity.setText(university.getName());
                selectedUniversity = new SuggestDto(university.getId(), university.getName(), null);
            }

            showFab();

            setHasOptionsMenu(true);
        }
        return view;
    }

    private void saveChangesLocally() {
        mHasChanges = true;
        UserProfileManager.updateUserProfile(edtName.getText().toString(), mUserAvatar, tvEmail.getText().toString(), mGroupId);
    }

    @Override
    public void onStop() {
        if (mHasChanges)
            saveChangesToBackend();
        super.onStop();
    }

    private void saveChangesToBackend() {
        Api.sendMessage(new UpdateProfileMessage(mUserAvatar, edtName.getText().toString(), tvEmail.getText().toString(), mGroupId));
    }

    private void showSearch(String searchType, boolean hasUniversity, String entityId) {
        searchFragment = new BaseSearchFragment();
        searchFragment.setTargetFragment(this, 0);
        Bundle bundle = new Bundle();
        bundle.putString(BaseSearchFragment.EXTRA_SEARCH_TYPE, searchType);
        bundle.putString(BaseSearchFragment.EXTRA_ENTITY_ID, entityId);
        if (hasUniversity)
            bundle.putString(BaseSearchFragment.EXTRA_UNIVERSITY, tvSelectedUniversity.getText().toString());
        searchFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer, searchFragment).commitAllowingStateLoss();
    }

    public void showFab() {
        fabPhoto.animate().scaleX(1).scaleY(1).setDuration(200).setStartDelay(500).start();
    }

    public void hideFab() {
        fabPhoto.animate().scaleX(0).scaleY(0).setDuration(200).setStartDelay(0).start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_logged_actions, menu);

        menu.findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CustomDialog.showMessageWithTitle(getContext(), null, getString(R.string.user_profile_logout_confirm_msg), null, null, new CustomDialog.CustomCallback() {
                    @Override
                    public void proceed() {
                        mHasChanges = false;
                        ((MainActivity) getActivity()).logout();
                    }
                }, null);
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onUniversitySelect(SuggestDto item) {
        selectedUniversity = item;
        tvSelectedUniversity.setText(item.abbr);

        selectedFaculty = null;
        tvSelectedFaculty.setText(getString(R.string.faculty));
        selectedGroup = null;
        tvSelectedGroup.setText(getString(R.string.group));
    }

    @Override
    public void onFacultySelect(SuggestDto item) {
        selectedFaculty = item;
        tvSelectedFaculty.setText(item.name);

        selectedGroup = null;
        tvSelectedGroup.setText(getString(R.string.group));
    }

    @Override
    public void onGroupSelect(SuggestDto item) {
        selectedGroup = item;
        tvSelectedGroup.setText(item.name);

        mGroupName = item.name;
        mGroupId = item.id;

        saveChangesLocally();

        Intent intent = new Intent(getActivity(), DataLoadingActivity.class);
        intent.putExtra(ListMyScheduleActivity.EXTRA_SELECTED_GROUP_ID, selectedGroup.id);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onSearchClose(boolean isSelected) {
        searchFragment = null;
    }

    @Override
    public void onImageChooserResult(final Uri imageUri, final String imageSource) {
        final String path = imageUri.getPath();
        mUserAvatar = path;
        final Uri uri = Uri.parse("file://" + path);

        dummyAvatar.setVisibility(View.GONE);
        showProgress();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                final String privateFile = FileUtil.getNewFileName();

                boolean success;

                if (ImageChooserDialog.PHOTO_SOURCE_CAMERA.equals(imageSource)) {
                    // image from camera
                    Bitmap initBitmap;
                    int rotation = ImageUtil.getRotation(getContext());
                    if (rotation > 0) {
                        try {
                            initBitmap = ImageUtil.rotateImage(getContext(), uri, rotation);
                            Bitmap resizedBitmap = (initBitmap != null) ? initBitmap : ImageUtil.getResizeBitmap(uri, getContext()); // resize
                            ImageUtil.saveBitmapToFile(privateFile, resizedBitmap);
                            success = true;
                        } catch (Exception e) {
                            success = false;
                        }
                    } else {
                        success = ImageUtil.resizeBitmap(uri, privateFile, Settings.AVATAR_PHOTO_IMAGE_SIZE, Settings.AVATR_PHOTO_IMAGE_QUALITY, 7);
                    }
                } else {
                    // image from file

                    /*Bitmap resizedBitmap = ImageUtil.getResizeBitmap(uri, UserLoggedFragment.this.getActivity()); // resize
                    ImageUtil.saveBitmapToFile(privateFile, resizedBitmap);
                    success = true;*/
                    success = ImageUtil.resizeBitmap(uri, privateFile, Settings.AVATAR_PHOTO_IMAGE_SIZE, Settings.AVATR_PHOTO_IMAGE_QUALITY, 1);
                }
                return success ? FileUtil.upload(privateFile) : null;
            }

            @Override
            protected void onPostExecute(String newAvatarUrl) {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                mUserAvatar = newAvatarUrl;

                if (context != null)
                    ImageUtil.displayImage(mUserAvatar, ivAvatar, context);
                saveChangesLocally();

                EventBus.getDefault().post(new UpdateAvatarEvent(mUserAvatar));
//                ((MainActivity) getActivity()).updateUserData(edtName.getText().toString(), path);
            }
        }.execute();
    }

    private void showProgress() {
        if (isAdded()) {
            String msg = getString(R.string.loading_msg3);
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(msg);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.PROFILE_SCREEN.type);
    }

}
