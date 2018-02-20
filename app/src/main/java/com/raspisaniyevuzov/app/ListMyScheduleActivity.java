package com.raspisaniyevuzov.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.api.dto.SuggestDto;
import com.raspisaniyevuzov.app.db.RVuzovMigration;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.model.Faculty;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.MainActivity;
import com.raspisaniyevuzov.app.ui.auth.DataLoadingActivity;
import com.raspisaniyevuzov.app.ui.auth.search.BaseSearchFragment;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.GcmUtil;
import com.raspisaniyevuzov.app.util.ImageUtil;
import com.raspisaniyevuzov.app.util.PrefUtil;
import com.yandex.metrica.YandexMetrica;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Form fos University, Faculty and Group selection
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class ListMyScheduleActivity extends BaseActivity implements BaseSearchFragment.Searchable {

    public static final String EXTRA_SELECTED_GROUP_ID = "extra_selected_group_id";
    /**
     * True if all required fields are filled
     */
    private boolean completed;
    private SuggestDto selectedFaculty, selectedGroup, selectedUniversity;

    private View llMain;
    private TextView tvSelectedUniversity, tvSelectedFaculty, tvSelectedGroup, mHintText;
    private RelativeLayout searchFaculty;
    private RelativeLayout searchGroup;
    private Button mBtnAuth;
    private FrameLayout flGroup, flFaculty, flUniversity;
    private BaseSearchFragment searchFragment = null;
    private String mCurrentSearchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);

        if (PrefUtil.isLogged(this)) {
            goToMain();
            return;
        }

        DbUtil.clearScheduleData();

        // DO NOT CALLS for lollipop devices and higher
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams toolbarLayoutParams = (RelativeLayout.LayoutParams) findViewById(R.id.searchContainer).getLayoutParams();
            toolbarLayoutParams.setMargins(0, 0, 0, 0);
        }

        llMain = findViewById(R.id.llMain);
        findViewById(R.id.searchUniversity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.UNIVERSITY_SEARCH_OPEN.type);
                showSearch(BaseSearchFragment.SearchType.UNIVERSITY.type, false, null);
            }
        });

        searchFaculty = (RelativeLayout) findViewById(R.id.searchFaculty);
        searchGroup = (RelativeLayout) findViewById(R.id.searchGroup);
        tvSelectedUniversity = (TextView) findViewById(R.id.tvSelectedUniversity);
        tvSelectedFaculty = (TextView) findViewById(R.id.tvSelectedFaculty);
        tvSelectedGroup = (TextView) findViewById(R.id.tvSelectedGroup);

        mHintText = (TextView) findViewById(R.id.hintText);

        flGroup = (FrameLayout) findViewById(R.id.flGroup);
        flFaculty = (FrameLayout) findViewById(R.id.flFaculty);
        flUniversity = (FrameLayout) findViewById(R.id.flUniversity);

        tvSelectedUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.UNIVERSITY_SEARCH_OPEN.type);
                showSearch(BaseSearchFragment.SearchType.UNIVERSITY.type, false, null);
            }
        });

        mBtnAuth = (Button) findViewById(R.id.btnAuth);

        mBtnAuth.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            DbUtil.clearScheduleData();

                                            GroupDao groupDao = GroupDao.getInstance();

                                            // save university
                                            University university = new University();
                                            university.setId(selectedUniversity.id);
                                            university.setName(selectedUniversity.name);
                                            university.setAbbr(selectedUniversity.abbr);
                                            university.setIsActive(true);

                                            // save group
                                            Faculty faculty = new Faculty();
                                            faculty.setId(selectedFaculty.id);
                                            faculty.setName(selectedFaculty.name);
                                            faculty.setAbbr(selectedFaculty.abbr);
                                            faculty.setUniversity(university);

                                            Group group = new Group();
                                            group.setId(selectedGroup.id);
                                            group.setName(selectedGroup.name);
                                            group.setFaculty(faculty);
                                            groupDao.save(group);

                                            Intent intent = new Intent(ListMyScheduleActivity.this, DataLoadingActivity.class);
                                            intent.putExtra(EXTRA_SELECTED_GROUP_ID, selectedGroup.id);
                                            startActivity(intent);
                                            finish();

                                            Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SCHEDULE_LOAD.type);
                                        }
                                    }

        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HeightAnimation heightAnim = new HeightAnimation(flGroup, flGroup.getHeight(), ImageUtil.convertDpToPixel(48, ListMyScheduleActivity.this));
                heightAnim.setDuration(300);
                flGroup.startAnimation(heightAnim);
            }
        }, 1200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HeightAnimation heightAnim = new HeightAnimation(flFaculty, flFaculty.getHeight(), ImageUtil.convertDpToPixel(48, ListMyScheduleActivity.this));
                heightAnim.setDuration(300);
                flFaculty.startAnimation(heightAnim);
            }
        }, 1200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HeightAnimation heightAnim = new HeightAnimation(flUniversity, flUniversity.getHeight(), ImageUtil.convertDpToPixel(48, ListMyScheduleActivity.this));
                heightAnim.setDuration(300);
                heightAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        flUniversity.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                flUniversity.startAnimation(heightAnim);
            }
        }, 1200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HeightAnimation heightAnim = new HeightAnimation(mHintText, mHintText.getHeight(), ImageUtil.convertDpToPixel(48, ListMyScheduleActivity.this));
                heightAnim.setDuration(300);
                heightAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mHintText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mHintText.startAnimation(heightAnim);
            }
        }, 1200);

        if (Flags.DEBUG) {
            checkForUpdates();
            checkForCrashes();
        }

        // Регистрация приложения в GCM
        GcmUtil.registerInBackground(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.START_SCREEN.type);
    }

    @Override
    public void onBackPressed() {
        if (searchFragment != null) searchFragment.handleToolBar(false);
        else finish();
    }

    private void showSearch(String searchType, boolean hasUniversity, String entityId) {
        this.mCurrentSearchType = searchType;
        searchFragment = new BaseSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BaseSearchFragment.EXTRA_SEARCH_TYPE, searchType);
        bundle.putString(BaseSearchFragment.EXTRA_ENTITY_ID, entityId);
        if (hasUniversity)
            bundle.putString(BaseSearchFragment.EXTRA_UNIVERSITY, tvSelectedUniversity.getText().toString());
        searchFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer, searchFragment).commit();
    }

    private class HeightAnimation extends Animation {
        protected final int originalHeight;
        protected final View view;
        protected float perValue;

        public HeightAnimation(View view, int fromHeight, int toHeight) {
            this.view = view;
            this.originalHeight = fromHeight;
            this.perValue = (toHeight - fromHeight);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            view.getLayoutParams().height = (int) (originalHeight + perValue * interpolatedTime);
            view.requestLayout();
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    private void onSearchOpen() {
        mBtnAuth.setVisibility(View.INVISIBLE);
        llMain.setVisibility(View.INVISIBLE);
    }

    private void goToMain() {
        startActivity(new Intent(ListMyScheduleActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkForCrashes() {
        if (!isFinishing()) {
            CrashManager.register(this, getString(R.string.hockeyapp_id), new CrashManagerListener() {
                public boolean shouldAutoUploadCrashes() {
                    return true;
                }
            });
        }
    }

    private void checkForUpdates() {
        UpdateManager.register(this, getString(R.string.hockeyapp_id));
    }

    public SuggestDto getUniversity() {
        return selectedUniversity;
    }

    public SuggestDto getFaculty() {
        return selectedFaculty;
    }

    @Override
    public void onUniversitySelect(SuggestDto item) {
        selectedUniversity = item;
        tvSelectedUniversity.setVisibility(View.VISIBLE);
        tvSelectedUniversity.setText(item.abbr);
        flFaculty.setVisibility(View.VISIBLE);
        tvSelectedFaculty.setVisibility(View.INVISIBLE);
        tvSelectedGroup.setVisibility(View.INVISIBLE);
        searchGroup.setVisibility(View.INVISIBLE);
        mBtnAuth.setVisibility(View.INVISIBLE);
        searchFaculty.setVisibility(View.VISIBLE);
        searchFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.FACULTY_SEARCH_OPEN.type);
                showSearch(BaseSearchFragment.SearchType.FACULTY.type, true, selectedUniversity.id);
            }
        });
        mHintText.setText(getString(R.string.search_faculty_hint));
        completed = false;
    }

    @Override
    public void onFacultySelect(SuggestDto item) {
        tvSelectedFaculty.setVisibility(View.VISIBLE);
        tvSelectedFaculty.setText(item.name);
        selectedFaculty = item;
        tvSelectedFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.FACULTY_SEARCH_OPEN.type);
                showSearch(BaseSearchFragment.SearchType.FACULTY.type, true, selectedUniversity.id);
            }
        });

        flGroup.setVisibility(View.VISIBLE);
        searchGroup.setVisibility(View.VISIBLE);
        tvSelectedGroup.setVisibility(View.INVISIBLE);
        mBtnAuth.setVisibility(View.INVISIBLE);
        searchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.GROUP_SEARCH_OPEN.type);
                showSearch(BaseSearchFragment.SearchType.GROUP.type, true, selectedFaculty.id);
            }
        });
        mHintText.setText(getString(R.string.search_group_hint));
        completed = false;

    }

    @Override
    public void onGroupSelect(SuggestDto item) {
        selectedGroup = item;
        tvSelectedGroup.setVisibility(View.VISIBLE);
        tvSelectedGroup.setText(item.name);
        tvSelectedGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchOpen();
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.GROUP_SEARCH_OPEN.type);
                showSearch(BaseSearchFragment.SearchType.GROUP.type, true, selectedFaculty.id);
            }
        });
        mBtnAuth.setVisibility(View.VISIBLE);
        mHintText.setText(getString(R.string.all_fields_filled_hint));
        completed = true;
    }

    @Override
    public void onSearchClose(boolean isSelected) {
        String type = null;

        if (mCurrentSearchType != null) {
            if (mCurrentSearchType.equals(BaseSearchFragment.SearchType.UNIVERSITY.type))
                type = isSelected ? AnalyticsUtil.AmplitudeEventType.UNIVERSITY_SEARCH_SUCCESSFUL.type : AnalyticsUtil.AmplitudeEventType.UNIVERSITY_SEARCH_FAULT.type;
            if (mCurrentSearchType.equals(BaseSearchFragment.SearchType.FACULTY.type))
                type = isSelected ? AnalyticsUtil.AmplitudeEventType.FACULTY_SEARCH_SUCCESSFUL.type : AnalyticsUtil.AmplitudeEventType.FACULTY_SEARCH_FAULT.type;
            if (mCurrentSearchType.equals(BaseSearchFragment.SearchType.GROUP.type))
                type = isSelected ? AnalyticsUtil.AmplitudeEventType.GROUP_SEARCH_SUCCESSFUL.type : AnalyticsUtil.AmplitudeEventType.GROUP_SEARCH_FAULT.type;
        }

        if (type != null)
            Amplitude.getInstance().logEvent(type);

        if (completed)
            mBtnAuth.setVisibility(View.VISIBLE);
        llMain.setVisibility(View.VISIBLE);
        searchFragment = null;
    }

}
