package com.raspisaniyevuzov.app.ui.widget;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.manager.UniversityManager;
import com.raspisaniyevuzov.app.db.model.University;
import com.raspisaniyevuzov.app.ui.MainActivity;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.ImageUtil;
import com.raspisaniyevuzov.app.util.LogUtil;
import com.raspisaniyevuzov.app.util.PrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */

public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
//    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final int PROFILE_INDEX = 3;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private Button btnAuth;
    private ImageView imgAvatar;
    private LinearLayout mAdsLayout;
    private ImageView mBanner1, mBanner2;
    private FrameLayout ivDisableAds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        final List<NavigationItem> navigationItems = getMenu();
        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(navigationItems, getActivity());
        adapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(adapter);
        selectItem(mCurrentSelectedPosition);

        mAdsLayout = (LinearLayout) view.findViewById(R.id.adsLayout);
        mBanner1 = (ImageView) mAdsLayout.findViewById(R.id.ivBanner1);
        mBanner2 = (ImageView) mAdsLayout.findViewById(R.id.ivBanner2);
        ivDisableAds = (FrameLayout) mAdsLayout.findViewById(R.id.ivDisableAds);

        // ads #1
       /* AdView adView = (AdView) view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // ads #1
        AdView adView2 = (AdView) view.findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        adView2.loadAd(adRequest2);*/

        return view;
    }

    public void setFirstFragmentOnLoad(int position){
        mCurrentSelectedPosition = position;
        selectItem(mCurrentSelectedPosition);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        selectItem(position);
    }

    public List<NavigationItem> getMenu() {
        List<NavigationItem> items = new ArrayList<NavigationItem>();
        items.add(new NavigationItem(getString(R.string.fragment_schedule_title), getResources().getDrawable(R.drawable.ic_event_white_24dp)));
        items.add(new NavigationItem(getString(R.string.fragment_tasks_title), getResources().getDrawable(R.drawable.ic_assignment_white_24dp)));
        items.add(new NavigationItem(getString(R.string.fragment_support_title), getResources().getDrawable(R.drawable.ic_help_white_24dp)));
        return items;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param toolbar      The Toolbar of the activity.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = (View) getActivity().findViewById(fragmentId).getParent();

        btnAuth = (Button) mFragmentContainerView.findViewById(R.id.btnAuth);
        imgAvatar = (ImageView) mFragmentContainerView.findViewById(R.id.imgAvatar);

        mDrawerLayout = drawerLayout;

        int w = ImageUtil.getDisplayWidth(getActivity()) - ImageUtil.convertDpToPixel(56, getActivity());
        mDrawerLayout.findViewById(R.id.scrimInsetsFrameLayout).getLayoutParams().width = w;
        mDrawerLayout.findViewById(R.id.rlUserAccount).getLayoutParams().width = w;
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primaryDarkColor));
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()

//                mAdsLayout.setVisibility(DeviceUtil.hasNetworkConnection() ? View.VISIBLE : View.INVISIBLE);

                try {
                    String jsonBanners = PrefUtil.getBanners(getActivity());
                    if (jsonBanners != null) {
                        JSONArray banners = new JSONArray(jsonBanners);

                        if (banners.length() > 0) {
                            JSONObject banner1 = banners.getJSONObject(0);
                            sendEvent(banner1.getString("code"), 1, AnalyticsUtil.AmplitudeEventType.BANNER_SHOW.type);
                        }
                        if (banners.length() > 1) {
                            JSONObject banner2 = banners.getJSONObject(1);
                            sendEvent(banner2.getString("code"), 2, AnalyticsUtil.AmplitudeEventType.BANNER_SHOW.type);
                        }
                    }
                } catch (JSONException e) {
                    LogUtil.e(NavigationDrawerFragment.class.getSimpleName(), e.getMessage());
                }
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        int h = w * 9 / 16;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mDrawerLayout.findViewById(R.id.rlTop).getLayoutParams();
        layoutParams.height = h;
        layoutParams.width = w;

        mFragmentContainerView.findViewById(R.id.navigationHeader).getLayoutParams().height = h - ImageUtil.convertDpToPixel(72, getActivity());

        mFragmentContainerView.findViewById(R.id.rlTop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(PROFILE_INDEX);
            }
        });

        mFragmentContainerView.findViewById(R.id.btnAuth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(PROFILE_INDEX);
            }
        });
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
        ((NavigationDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setUserData(String user, String avatar) {
        if (user != null) {
            if (!user.isEmpty())
                btnAuth.setText(user);
            else btnAuth.setText(getString(R.string.profile));
        } else btnAuth.setText(getString(R.string.enter));

        if (avatar != null && !avatar.isEmpty()) {
            ImageUtil.displayImage(avatar, imgAvatar, getActivity());
        } else {
            imgAvatar.setImageResource(R.drawable.ic_person_white_48dp);
        }
    }

    public void updateAvatar(String avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            ImageUtil.displayImage(avatar, imgAvatar, getActivity());
        } else {
            imgAvatar.setImageResource(R.drawable.ic_person_white_48dp);
        }
    }

    public void updateBanners(boolean isNoAds) {
        String jsonBanners = PrefUtil.getBanners(getActivity());

        if (jsonBanners != null/* && DeviceUtil.hasNetworkConnection()*/ && !isNoAds) {
            mAdsLayout.setVisibility(View.VISIBLE);

            try {
                JSONArray banners = new JSONArray(jsonBanners);

                if (banners.length() > 0) {
                    // first banner
                    final JSONObject banner1 = banners.getJSONObject(0);
                    ImageUtil.displayImage(banner1.getString("img"), mBanner1, getActivity());
                    mBanner1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.addCategory(Intent.CATEGORY_BROWSABLE);
                                i.setData(Uri.parse(banner1.getString("url")));
                                startActivity(i);

                                sendEvent(banner1.getString("code"), 1, AnalyticsUtil.AmplitudeEventType.BANNER_CLICK.type);

                                closeDrawer();

                            } catch (ActivityNotFoundException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                if (banners.length() > 1) {
                    // second banner
                    mBanner2.setVisibility(View.VISIBLE);
                    final JSONObject banner2 = banners.getJSONObject(1);
                    ImageUtil.displayImage(banner2.getString("img"), mBanner2, getActivity());
                    mBanner2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.addCategory(Intent.CATEGORY_BROWSABLE);
                                i.setData(Uri.parse(banner2.getString("url")));
                                startActivity(i);

                                sendEvent(banner2.getString("code"), 2, AnalyticsUtil.AmplitudeEventType.BANNER_CLICK.type);

                                closeDrawer();

                            } catch (ActivityNotFoundException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    mBanner2.setVisibility(View.INVISIBLE);
                }
                ivDisableAds.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) getActivity()).onDisableAdsButtonClicked();
                    }
                });

            } catch (JSONException e) {
                LogUtil.e(NavigationDrawerFragment.class.getSimpleName(), e.getMessage());
            }
        } else {
            mAdsLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void sendEvent(String code, int position, String eventName) {
        University university = UniversityManager.getUniversity();
        try {
            JSONObject param = new JSONObject();
            param.put("code", code);
            param.put("position", position);
            param.put("university", university != null ? university.getId() : null);
            Amplitude.getInstance().logEvent(eventName, param);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
