package com.raspisaniyevuzov.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.MessagesFactory;
import com.raspisaniyevuzov.app.api.SyncService;
import com.raspisaniyevuzov.app.api.messages.ads.ADSResultMessage;
import com.raspisaniyevuzov.app.api.messages.ads.InAppPurchaseCompleteMessage;
import com.raspisaniyevuzov.app.api.messages.schedule.AddScheduleRequestMessage;
import com.raspisaniyevuzov.app.api.messages.schedule.GroupScheduleRequestMessage;
import com.raspisaniyevuzov.app.api.messages.schedule.GroupScheduleResponseMessage;
import com.raspisaniyevuzov.app.api.messages.service.PopupInfoMessage;
import com.raspisaniyevuzov.app.api.messages.service.TestMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.FacultySuggestRequestMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.FacultySuggestResponseMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.GroupSuggestRequestMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.GroupSuggestResponseMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.UniversitySuggestRequestMessage;
import com.raspisaniyevuzov.app.api.messages.suggest.UniversitySuggestResponseMessage;
import com.raspisaniyevuzov.app.api.messages.system.ErrorMessage;
import com.raspisaniyevuzov.app.api.messages.system.SuccessMessage;
import com.raspisaniyevuzov.app.api.messages.task.TaskListRequestMessage;
import com.raspisaniyevuzov.app.api.messages.task.TaskListResponseMessage;
import com.raspisaniyevuzov.app.api.messages.user.LogoutMessage;
import com.raspisaniyevuzov.app.api.messages.user.NewPushTokenMessage;
import com.raspisaniyevuzov.app.api.messages.user.ProfileResultMessage;
import com.raspisaniyevuzov.app.api.messages.user.RegisterMessage;
import com.raspisaniyevuzov.app.api.messages.user.RegisterResultMessage;
import com.raspisaniyevuzov.app.api.messages.user.RestorePasswordMessage;
import com.raspisaniyevuzov.app.api.messages.user.SignInMessage;
import com.raspisaniyevuzov.app.api.messages.user.SignUpMessage;
import com.raspisaniyevuzov.app.api.messages.user.UpdateProfileMessage;
import com.raspisaniyevuzov.app.db.RVuzovMigration;
import com.raspisaniyevuzov.app.db.dao.ClientDao;
import com.raspisaniyevuzov.app.util.FileUtil;
import com.raspisaniyevuzov.app.util.Settings;
import com.splunk.mint.Mint;
import com.yandex.metrica.YandexMetrica;

import net.hockeyapp.android.LocaleManager;

import org.prflr.sdk.PRFLR;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class RVuzovApp extends Application {

    private static RVuzovApp instance;

    public RVuzovApp() {
        instance = this;
    }

    public static RVuzovApp getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PRFLR.init(this);

        if (Flags.DEBUG)
            LocaleManager.initialize(getApplicationContext());

        initBugTracking();

        initAnalytics();

        initYandexMetrica();

        initDatabase();

        startService(new Intent(this, SyncService.class));
        loadClasses();

        if (ClientDao.getInstance().getClient() == null)
            Api.sendMessage(new RegisterMessage());

        FileUtil.mkDirs();
    }

    private void initYandexMetrica(){
        // подробнее https://tech.yandex.ru/metrica-mobile-sdk/doc/mobile-sdk-dg/concepts/android-initialize-docpage/
        YandexMetrica.activate(getApplicationContext(), getString(R.string.yandex_metrica_api_key));
    }

    private void initBugTracking() {
        Mint.initAndStartSession(this, getString(R.string.mint_api_key));
    }

    private void initDatabase() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this)
                .schemaVersion(Settings.DATABASE_SCHEME_VERSION)
                .migration(new RVuzovMigration())
                .build();
        Realm realm = Realm.getInstance(realmConfig);
        Realm.setDefaultConfiguration(realm.getConfiguration());
        realm.close();
    }

    private void initAnalytics() {
        // Amplitude
        Amplitude.getInstance().initialize(this, getString(R.string.amplitude_api_key)).enableForegroundTracking(this);

        // Adjust
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this, getString(R.string.adjust_api_key), environment);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                Adjust.onResume();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Adjust.onPause();
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

        });
    }

    private void loadClasses() {
        MessagesFactory factory = Api.getFactory();
        // system
        factory.registerTypeForClass(SuccessMessage.class);
        factory.registerTypeForClass(ErrorMessage.class);
        // user
        factory.registerTypeForClass(RegisterMessage.class);
        factory.registerTypeForClass(RegisterResultMessage.class);
        factory.registerTypeForClass(RestorePasswordMessage.class);
        factory.registerTypeForClass(NewPushTokenMessage.class);
        factory.registerTypeForClass(LogoutMessage.class);
        factory.registerTypeForClass(SignInMessage.class);
        factory.registerTypeForClass(SignUpMessage.class);
        factory.registerTypeForClass(UpdateProfileMessage.class);
        factory.registerTypeForClass(ProfileResultMessage.class);
        // schedule
        factory.registerTypeForClass(AddScheduleRequestMessage.class);
        factory.registerTypeForClass(GroupScheduleRequestMessage.class);
        factory.registerTypeForClass(GroupScheduleResponseMessage.class);
        // suggest
        factory.registerTypeForClass(UniversitySuggestRequestMessage.class);
        factory.registerTypeForClass(UniversitySuggestResponseMessage.class);
        factory.registerTypeForClass(FacultySuggestRequestMessage.class);
        factory.registerTypeForClass(FacultySuggestResponseMessage.class);
        factory.registerTypeForClass(GroupSuggestRequestMessage.class);
        factory.registerTypeForClass(GroupSuggestResponseMessage.class);
        // task
        factory.registerTypeForClass(TaskListRequestMessage.class);
        factory.registerTypeForClass(TaskListResponseMessage.class);
        // service
        factory.registerTypeForClass(PopupInfoMessage.class);
        factory.registerTypeForClass(TestMessage.class);
        factory.registerTypeForClass(ADSResultMessage.class);
        factory.registerTypeForClass(InAppPurchaseCompleteMessage.class);
        // TODO add another BaseMessage classes here
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * true если какая-нибудь из активити в foreground
     */
    private static boolean mInForeground = false;

    public static void setInForeground(boolean inForeground) {
        mInForeground = inForeground;
    }

    public static Boolean inForeground() {
        return mInForeground;
    }

}
