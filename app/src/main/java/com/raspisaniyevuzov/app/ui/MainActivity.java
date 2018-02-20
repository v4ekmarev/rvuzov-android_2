package com.raspisaniyevuzov.app.ui;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.Flags;
import com.raspisaniyevuzov.app.ListMyScheduleActivity;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.ads.InAppPurchaseCompleteMessage;
import com.raspisaniyevuzov.app.api.messages.schedule.GroupScheduleRequestMessage;
import com.raspisaniyevuzov.app.api.messages.service.PopupInfoMessage;
import com.raspisaniyevuzov.app.api.messages.user.LogoutMessage;
import com.raspisaniyevuzov.app.api.messages.user.RegisterMessage;
import com.raspisaniyevuzov.app.db.dao.GroupDao;
import com.raspisaniyevuzov.app.db.dao.UserProfileDao;
import com.raspisaniyevuzov.app.db.manager.UserProfileManager;
import com.raspisaniyevuzov.app.db.model.Group;
import com.raspisaniyevuzov.app.db.model.UserProfile;
import com.raspisaniyevuzov.app.event.AdsResultReceivedEvent;
import com.raspisaniyevuzov.app.event.ShowPopupInfoEvent;
import com.raspisaniyevuzov.app.event.UpdateAvatarEvent;
import com.raspisaniyevuzov.app.event.UserProfileChangedEvent;
import com.raspisaniyevuzov.app.ui.profile.ProfileFragment;
import com.raspisaniyevuzov.app.ui.profile.UserLoggedFragment;
import com.raspisaniyevuzov.app.ui.task.TasksFragment;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.ui.widget.NavigationDrawerCallbacks;
import com.raspisaniyevuzov.app.ui.widget.NavigationDrawerFragment;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.GcmUtil;
import com.raspisaniyevuzov.app.util.LogUtil;
import com.raspisaniyevuzov.app.util.PrefUtil;
import com.raspisaniyevuzov.app.util.Settings;
import com.raspisaniyevuzov.app.util.inapppurchase.IabHelper;
import com.raspisaniyevuzov.app.util.inapppurchase.IabResult;
import com.raspisaniyevuzov.app.util.inapppurchase.Inventory;
import com.raspisaniyevuzov.app.util.inapppurchase.Purchase;
import com.yandex.metrica.YandexMetrica;

import net.hockeyapp.android.UpdateManager;

import org.prflr.sdk.PRFLR;

import java.util.List;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class MainActivity extends BaseActivity
        implements NavigationDrawerCallbacks {

    public static final String INTENT_ACTION_START_SHOW_PROFILE = "showProfile";
    public static final String INTENT_ACTION_START_SHOW_TASKS = "showTasks";

    private static final String PREF_POPUP_TEXT = "pref_popup_text";
    private static final String PREF_POPUP_ACTION = "pref_popup_action";
    private static final String TAG = MainActivity.class.getSimpleName();

    // Does the user disable the ads?
    boolean mIsNoAds = false;

    /**
     * Non-consumable
     */
    static final String SKU_NO_ADS = "no_ads";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * selected position in Navigation Drawer
     */
    private int currentPosition = -1;

    // request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = getActionBarToolbar();

        mIsNoAds = PrefUtil.isNoAds(this);

        initInAppPurchase();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Устанавливаем первым нужный нам фрагмент
        if(getIntent().getBooleanExtra(INTENT_ACTION_START_SHOW_PROFILE, false)){
            mNavigationDrawerFragment.setFirstFragmentOnLoad(3);
        } else if(getIntent().getBooleanExtra(INTENT_ACTION_START_SHOW_TASKS, false)){
            mNavigationDrawerFragment.setFirstFragmentOnLoad(1);
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);
        // populate the navigation drawer

        if (Flags.DEBUG)
            checkForUpdates();

        PRFLR.begin("TestTimer1");
        PRFLR.end("TestTimer1", "success");

        updateSchedule();

        DbUtil.importDataFromOldAppVersionDatabase(this);

        PrefUtil.setLogged(this, true);

        GcmUtil.registerInBackground(this);
    }

    private void initInAppPurchase() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl2xUcB+TMFxBHvTYdQTuv5rqWzae6NGr4pACSXvlgwHXrft0xss9HFiOMp8aI2BlBBabG26fRGkEO7Qs9QMfGAc/vlgVuh7V8S7MrJM8M62jRavwguJ+BhBtLe6k06cLWxo65r2GRyHdFFR1eYJznqMQGNoXdX9s27fxZxodteNt6GKIjXfuyKWr+w8lAKhGCiOvIG31wiBK3FLhizj2MMe5Ia7IY8axm9n856KauWRgUlAdmNZqB7/o0OtnXiZbXWoUfzxwSpS65cq2UpBkgYw1koqcae4KSnkMlPm9PFvUpG5wzcwkTumCVEAyzyLyJ1lcWHJp1EbUiX6oHxX5YwIDAQAB";
        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.enableDebugLogging(false);
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        LogUtil.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                LogUtil.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    LogUtil.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                LogUtil.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    private void updateSchedule() {
        if (PrefUtil.isLogged(this)) {
            List<Group> groups = GroupDao.getInstance().getAll(Group.class);
            if (!groups.isEmpty())
                Api.sendMessage(new GroupScheduleRequestMessage(groups.get(0).getId()));
        }
    }

    private void checkForUpdates() {
        UpdateManager.register(this, getString(R.string.hockeyapp_id));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPopupMessage();
        updateUserData();
    }

    public void onEventMainThread(final ShowPopupInfoEvent event) {
        PrefUtil.addStringProperty(this, PREF_POPUP_TEXT, event.text);
        PrefUtil.addStringProperty(this, PREF_POPUP_ACTION, event.action);
        showPopup(event.text, event.action);
    }

    public void checkPopupMessage() {
        String text = PrefUtil.getStringProperty(this, PREF_POPUP_TEXT);
        String action = PrefUtil.getStringProperty(this, PREF_POPUP_ACTION);
        if (text != null)
            showPopup(text, action);
    }

    private void showPopup(final String text, final String action) {
        PrefUtil.addStringProperty(this, PREF_POPUP_TEXT, null);
        PrefUtil.addStringProperty(this, PREF_POPUP_ACTION, null);

        CustomDialog.showMessageWithTitle(this, null, text, null, new CustomDialog.CustomCallback() {
            @Override
            public void proceed() {
                if (PopupInfoMessage.ActionType.SELECT_UNIVERSITY.action.equals(action)) {
                    PrefUtil.setLogged(MainActivity.this, false);
                    DbUtil.clearDb();
                    startActivityWithParam(new Intent(MainActivity.this, ListMyScheduleActivity.class));
                } else if (PopupInfoMessage.ActionType.APP_UPDATE.action.equals(action)) {
                    try {
                        startActivityWithParam(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivityWithParam(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
                    }
                } else if (PopupInfoMessage.ActionType.RESET_APP_DATA.action.equals(action)) {
                    PrefUtil.setLogged(MainActivity.this, false);
                    DbUtil.clearDb();
                    startActivityWithParam(new Intent(MainActivity.this, ListMyScheduleActivity.class));
                }
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (currentPosition != position || position == 0) { // FIXME We block this line because we need to go to TODAY after selecting Schedule Menu Item. Therefore we recreate schedule fragment each time
            currentPosition = position;

            BaseFragment fragment = null;

            String tag = null;

            switch (position) {
                case 3: // Profile
                    UserProfile userProfile = UserProfileManager.getCurrentUserProfile();
                    if (userProfile != null)
                        fragment = new UserLoggedFragment();
                    else
                        fragment = new ProfileFragment();
                    break;
                case 0: // Schedule
                    fragment = new ScheduleFragment();
                    tag = ScheduleFragment.class.getSimpleName();
                    break;
                case 1: // Tasks
                    fragment = new TasksFragment();
                    break;
                case 2: // Support
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addCategory(Intent.CATEGORY_BROWSABLE);
                        i.setData(Uri.parse(Settings.SUPPORT_URL));
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            if (fragment != null) {
                final String finalTag = tag;
                final BaseFragment finalFragment = fragment;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            if (finalTag != null)
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, finalFragment, finalTag).addToBackStack(null).commitAllowingStateLoss();
                            else
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, finalFragment).addToBackStack(null).commitAllowingStateLoss();
                        }
//                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, finalFragment, finalTag).commitAllowingStateLoss();
                    }
                }, 250);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            finish();
    }

    public void login() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserLoggedFragment()).commitAllowingStateLoss();
        updateUserData();
    }

    public void updateUserData() {
        UserProfile userProfile = UserProfileManager.getCurrentUserProfile();
        if (userProfile != null)
            mNavigationDrawerFragment.setUserData(userProfile.getName(), userProfile.getAvatar());
    }

    public void updateBanners(boolean isNoAds) {
        mNavigationDrawerFragment.updateBanners(isNoAds);
    }

    private void updateAvatar(String avatar) {
        mNavigationDrawerFragment.updateAvatar(avatar);
    }

    public void updateUserData(String name, String avatar) {
        mNavigationDrawerFragment.setUserData(name, avatar);
    }

    public void logout() {
        Api.sendMessage(new LogoutMessage());
        UserProfileDao userProfileDao = UserProfileDao.getInstance();
        userProfileDao.deleteAll(UserProfile.class);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commitAllowingStateLoss();
        mNavigationDrawerFragment.setUserData(null, null);
        Api.sendMessage(new RegisterMessage());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    public void onEventMainThread(UserProfileChangedEvent event) {
        updateUserData();
    }

    public void onEventMainThread(AdsResultReceivedEvent event) {
        updateBanners(mIsNoAds);
    }

    public void onEventMainThread(UpdateAvatarEvent event) {
        updateAvatar(event.path);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            LogUtil.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                LogUtil.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            LogUtil.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we disable ads?
            Purchase noAdsPurchase = inventory.getPurchase(SKU_NO_ADS);
            mIsNoAds = (noAdsPurchase != null /*&& verifyDeveloperPayload(noAdsPurchase)*/);
            LogUtil.d(TAG, "NO_ADS is " + mIsNoAds);

            saveData();

            updateBanners(mIsNoAds);
            LogUtil.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    // User clicked the "Disable ads" button.
    public void onDisableAdsButtonClicked() {
        LogUtil.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

        Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.REMOVE_AD_CLICK.type);
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.US_YMETRICA_ADVERTISEMENT_REMOVE_CLICKED.type);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";//ClientDao.getInstance().getClient().getToken();

        mHelper.launchPurchaseFlow(this, SKU_NO_ADS, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            LogUtil.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

//        String token = ClientDao.getInstance().getClient().getToken();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            LogUtil.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain(String.format(getString(R.string.purchase_error), result));
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
//                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            LogUtil.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_NO_ADS)) {
                LogUtil.d(TAG, "Purchase is NO_ADS. Congratulating user.");
                alert(getString(R.string.no_ads_congratulation));
                mIsNoAds = true;

                Api.sendMessage(new InAppPurchaseCompleteMessage(SKU_NO_ADS, "android", purchase.getToken(), purchase.getSignature(), purchase.getOriginalJson()));

                // TODO send Amplitude event
//                Amplitude.getInstance().logRevenue();
                YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.US_YMETRICA_SUCESSFUL_NOADVERTISEMENT_PURCHASE.type);

                updateBanners(mIsNoAds);
            }
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        LogUtil.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    void complain(String message) {
        LogUtil.e(TAG, "**** Rvuzov Error: " + message);
        alert(message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        LogUtil.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    void saveData() {
        LogUtil.d(TAG, "Saving data");
        PrefUtil.setNoAds(this, mIsNoAds);
    }

}
