package com.raspisaniyevuzov.app.util;

import com.raspisaniyevuzov.app.Flags;

import io.realm.RealmConfiguration;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class Settings {

    private static final String RVUZOV_SERVER_URL = Flags.DEBUG ? "http://api.dev.rvuzov.ru/v2/" : "http://api.rvuzov.ru/v2/";

    public static final String RVUZOV_SERVER_API_URL = RVUZOV_SERVER_URL + "mq/";

    public static final String RVUZOV_FILE_UPLOAD_URL = RVUZOV_SERVER_URL + "upload/";

    public static final String SUPPORT_URL = "http://rvuzov.ru/third.html";

    public static final String TERMS_OF_USE_URL = "http://rvuzov.ru/terms/";

    public static final int AVATAR_PHOTO_IMAGE_SIZE = 300;

    // avatar image quality (JPG)
    public static final int AVATR_PHOTO_IMAGE_QUALITY = 50;

    // Realm db scheme version
    public static final int DATABASE_SCHEME_VERSION = 2;
}
