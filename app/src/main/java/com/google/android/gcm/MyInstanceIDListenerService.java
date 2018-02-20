package com.google.android.gcm;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.raspisaniyevuzov.app.util.GcmUtil;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh()");
        GcmUtil.refreshInBackground(getApplicationContext());
    }
    // [END refresh_token]
}
