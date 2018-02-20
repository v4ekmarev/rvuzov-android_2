package com.raspisaniyevuzov.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class TextUtil {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static String uppercase1stLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getCorrectValuesByCount(int count, String[] searchResult) {
        if (count == 0)
            return searchResult[0];
        if (count == 1) return String.format(searchResult[1], count);
        if (count < 11 || count > 15) {
            int n1 = count % 10;
            if (n1 > 1 && n1 < 5) return String.format(searchResult[2], count);
        }
        return String.format(searchResult[3], count);
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    public static boolean isValid(final String hex) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();

    }

}
