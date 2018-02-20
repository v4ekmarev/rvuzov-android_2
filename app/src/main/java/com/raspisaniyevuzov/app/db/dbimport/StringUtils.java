package com.raspisaniyevuzov.app.db.dbimport;

public class StringUtils {
    public static final String STRINGS_SEPARATOR = ",";

    public static String wrapToJSONFormat(String src){
        String result = src;
        if(!(result.startsWith("{") && result.endsWith("}"))){
            result = "{\"data\":" + result + "}";
        }
        return result;
    }
    
    public static String ellipsize(String input, int maxCharacters, int charactersAfterEllipsis) {
        if(maxCharacters < 3) {
            throw new IllegalArgumentException("maxCharacters must be at least 3 because the ellipsis already take up 3 characters");
        }
        if(maxCharacters - 3 < charactersAfterEllipsis) {
            throw new IllegalArgumentException("charactersAfterEllipsis must be less than maxCharacters");
        }
        if (input == null || input.length() < maxCharacters) {
            return input;
        }
        return input.substring(0, maxCharacters - 3 - charactersAfterEllipsis) + "..." + input.substring(input.length() - charactersAfterEllipsis);
    }
    
    public static String fromStringArray(String[] strings){
        String result = "";
        for(int i = 0; i < strings.length; i++ ){
            if(i < strings.length - 1){
                result += strings[i] + STRINGS_SEPARATOR;
            } else {
                result += strings[i];
            }
        }
        return result;
    }
    
    public static String arrayToString(String[] arr) {
        if (arr == null)
            return "";

        String res = "";
        for (int i = 0; i < arr.length; i++) {
            res += arr[i];
            if (i != arr.length - 1)
                res += "\n";
        }
        return res;
    }
}
