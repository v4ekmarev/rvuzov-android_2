package com.raspisaniyevuzov.app.util;

import android.content.Context;

import com.raspisaniyevuzov.app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SAPOZHKOV on 22.09.2015.
 */
public class JsonUtil {

    /**
     * Get Json file from server or from local resources
     *
     * @param context
     * @param jsonCallback
     */
    public static void getScheduleJsonResource(Context context, JsonCallback jsonCallback) {
        BufferedReader reader = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.test);
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonCallback.proceed(reader);
    }

}
