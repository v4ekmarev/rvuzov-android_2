package com.raspisaniyevuzov.app.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.RVuzovApp;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.user.NewPushTokenMessage;
import com.raspisaniyevuzov.app.db.dao.ClientDao;
import com.raspisaniyevuzov.app.event.SyncEvent;
import com.raspisaniyevuzov.app.ui.MainActivity;

import java.io.IOException;

import de.greenrobot.event.EventBus;

public class GcmUtil {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    // TODO CHANGE SENDER_ID TO PRODUCTION
    private static final String SENDER_ID = "246575038369";
    // Spec topic, can hadle it and do something if the equal topic coming in
    // Global topic, to show noty in all registered devices
    private static final String[] TOPICS = {"global"};
    private static final String TAG = GcmUtil.class.getSimpleName();
    private static final String PREF_PROPERTY_REG_ID = "pref_property_reg_id";
    private static final String PREF_PROPERTY_APP_VERSION = "pref_property_app_version";

/*
    “sheduleUpdate” - открываем и принудительно обновляем расписание
    “shedule” - открываем расписание
    “tasks”  - открываем таски
    “profile”  - открываем профиль пользователя
    нет параметра / пустой параметр - открываем приложение
*/
    enum PushEventType {
        SCHEDULE_UPDATE("sheduleUpdate"),
        SCHEDULE("shedule"),
        TASKS("tasks"),
        PROFILE("profile");

        public final String type;

        PushEventType(String type) {
            this.type = type;
        }
    }

    public static void receiveMessage(Bundle extras) {
        EventBus.getDefault().post(new SyncEvent());

        // {"title": "Расписание ВУЗов", "body":  data.Text, "action": data.Action}
        String title = extras.getString("title");
        String body = extras.getString("body");
        String action = extras.getString("action");

        // Проверяем наличие заголовка и описания для уведомления
        if(title == null || body == null){
            Log.e(TAG, "Some push-msg data trouble...");
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "body: " + body);
            Log.e(TAG, "action: " + action);
            return;
        }

        // Формируем и показываем уведомление
        showNotification(RVuzovApp.getContext(), title, body, action);
    }

    private static void showNotification(Context context, String notificationTitle, String notificationMessage, String app_action) {
/*
   0 “sheduleUpdate” - открываем и принудительно обновляем расписание: MainActivity, обновляет автоматом
   1 “shedule” - открываем расписание: MainActivity
   2 “tasks”  - открываем таски: MainActivity.INTENT_ACTION_START_SHOW_TASKS set true
   3 “profile”  - открываем профиль пользователя: MainActivity.INTENT_ACTION_START_SHOW_PROFILE set true
   4 нет параметра / пустой параметр - открываем приложение: MainActivity
*/
        Intent intent;
        int notificationId;
        if (PushEventType.SCHEDULE_UPDATE.type.equals(app_action)) {
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationId = 0;
        } else if (PushEventType.SCHEDULE.type.equals(app_action)) {
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationId = 1;
        } else if (PushEventType.TASKS.type.equals(app_action)) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.INTENT_ACTION_START_SHOW_TASKS, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationId = 2;
        } else if (PushEventType.PROFILE.type.equals(app_action)) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(MainActivity.INTENT_ACTION_START_SHOW_PROFILE, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationId = 3;
        } else {
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationId = 4;
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(notificationTitle)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                        .setContentText(notificationMessage);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private static void subscribeTopics(Context context, String token) throws IOException {
        Log.d(TAG, "subscribeTopics()");
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public static void registerInBackground(final Activity activity) {
        final String oldGCMToken = getRegistrationId(activity.getApplicationContext());
        Log.d(TAG, "Already saved token: " + oldGCMToken);
        if(oldGCMToken.length() == 0){
            new AsyncTask<Void, Void, Void>() {
                // Максимум, сколько ждем до прихода токена сессии
                private final long MAX_WAITING_TIME = 3 * 60 * 1000L;
                // Время между повторными проверками на получение токена сессии
                private final long WAITING_TIME = 60 * 1000L;
                // Итоговое время ожидания
                private long totalWaitingTime = 0L;

                @Override
                protected Void doInBackground(Void... params) {
                    String result;
                    if (checkPlayServices(activity)) {
                        try {
                            // Получаем токен GCM
                            InstanceID instanceID = InstanceID.getInstance(activity.getApplicationContext());
                            result = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                            Log.d(TAG, "GCM Registration Token: " + result);

                            // Ждем получения токена сессии
                            while(totalWaitingTime < MAX_WAITING_TIME && ClientDao.getInstance().getClient().getToken() == null){
                                try{
                                    totalWaitingTime += WAITING_TIME;
                                    Thread.sleep(WAITING_TIME);
                                }catch (InterruptedException ex){
                                    Log.e(TAG, "registerInBackground()#AsyncTask<>#doInBackground() error "+ ex.getMessage());
                                }
                            }

                            // Если токен сессии получен, сохраняем токен и отправляем в бэкенд
                            if(ClientDao.getInstance().getClient().getToken() != null){
                                sendRegistrationIdToBackend(result);
                                storeRegistrationId(activity, result);
                            }else{
                                Log.e(TAG, "registerInBackground()#AsyncTask<>#doInBackground() timeOut");
                            }

                            // Подписываем приложение на получение особых сообщений
                            subscribeTopics(activity.getApplicationContext(), result);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to complete token", e);
                        }
                    } else {
                        Log.i(TAG, "No valid Google Play Services APK found.");
                    }

                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * При изменении GCM токена, посылаем новый токен в бэкенд
     */
    public static void refreshInBackground(final Context context) {
        final String oldGCMToken = getRegistrationId(context);
        Log.d(TAG, "Already saved token: " + oldGCMToken);
        new AsyncTask<Void, Void, Void>() {
            // Максимум, сколько ждем до прихода токена сессии
            private final long MAX_WAITING_TIME = 3 * 60 * 1000L;
            // Время между повторными проверками на получение токена сессии
            private final long WAITING_TIME = 60 * 1000L;
            // Итоговое время ожидания
            private long totalWaitingTime = 0L;

            @Override
            protected Void doInBackground(Void... params) {
                String result;
                try {
                    // Получаем токен GCM
                    InstanceID instanceID = InstanceID.getInstance(context);
                    result = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.d(TAG, "GCM Registration Token: " + result);

                    // Ждем получения токена сессии
                    while(totalWaitingTime < MAX_WAITING_TIME && ClientDao.getInstance().getClient().getToken() == null){
                        try{
                            totalWaitingTime += WAITING_TIME;
                            Thread.sleep(WAITING_TIME);
                        }catch (InterruptedException ex){
                            Log.e(TAG, "registerInBackground()#AsyncTask<>#doInBackground() error "+ ex.getMessage());
                        }
                    }

                    // Проверяем, действительно ли нужно обновить GCM токен
                    if(oldGCMToken.length() > 0 && oldGCMToken.equals(result)) return null;

                    // Если токен сессии получен, сохраняем токен и отправляем в бэкенд
                    if(ClientDao.getInstance().getClient().getToken() != null){
                        sendRegistrationIdToBackend(result);
                        storeRegistrationId(context, result);
                    }else{
                        Log.e(TAG, "registerInBackground()#AsyncTask<>#doInBackground() timeOut");
                    }

                    // Подписываем приложение на получение особых сообщений
                    subscribeTopics(context, result);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to complete token refresh", e);
                }

                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     *
     * @param pushToken - GCM registration Id
     */
    private static void sendRegistrationIdToBackend(String pushToken) {
        Api.sendMessage(new NewPushTokenMessage(pushToken));
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private static void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "storeRegistrationId() regId=" + regId);
        Log.i(TAG, "Saving regId on app version " + appVersion);

        PrefUtil.addStringProperty(context, PREF_PROPERTY_REG_ID, regId);
        PrefUtil.addIntProperty(context, PREF_PROPERTY_APP_VERSION, appVersion);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing regID.
     */
    private static String getRegistrationId(Context context) {
        String registrationId = PrefUtil.getStringProperty(context, PREF_PROPERTY_REG_ID);
        if (registrationId == null || registrationId.length() == 0) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
