package com.raspisaniyevuzov.app.api;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.raspisaniyevuzov.app.event.SyncEvent;
import com.raspisaniyevuzov.app.util.DeviceUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;

import de.greenrobot.event.EventBus;

public class SyncService extends Service {

    private static final int MIN_EXCHANGE_INTERVAL = (int) (TimeUtil.SECOND);
    private int interval = MIN_EXCHANGE_INTERVAL;
    private static final int MAX_EXCHANGE_INTERVAL = (int) (TimeUtil.MINUTE);
    private Handler handler;

    public void onCreate() {
        super.onCreate();
        HandlerThread hThread = new HandlerThread("HandlerThread");
        hThread.start();
        handler = new Handler(hThread.getLooper());
        EventBus.getDefault().register(this);
        handler.post(task);
    }

    public void onEvent(SyncEvent e) {
        interval = MIN_EXCHANGE_INTERVAL;
        handler.removeCallbacks(task);
        handler.post(DeviceUtil.hasNetworkConnection() ? task : periodicTask);
    }

    private Runnable periodicTask = new Runnable() {
        public void run() {
            Api.sync();
            handler.postDelayed(periodicTask, interval);
            interval = Math.min(MAX_EXCHANGE_INTERVAL, interval * 2);
        }
    };

    private Runnable task = new Runnable() {
        public void run() {
            Api.sync();
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }

}