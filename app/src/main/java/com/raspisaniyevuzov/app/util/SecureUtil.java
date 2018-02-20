package com.raspisaniyevuzov.app.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by SAPOZHKOV on 22.10.2015.
 */
public class SecureUtil {

    public static String md5(String str, boolean upperCase) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] b = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        String format = upperCase ? "%02X" : "%02x";
        for (int i = 0; i < b.length; i++)
            hexString.append(String.format(Locale.US, format, 0xFF & b[i]));
        return hexString.toString();
    }

}
