package com.medhelp.medhelp.helpers;

public class ApiKeyHelper {

    private static String ApiKey;

    public static String getApiKey() {
        if (ApiKey == null) {
            return "";
        }
        return ApiKey;
    }

    public static void setApiKey(String apiKey) {
        ApiKey = apiKey;
    }
}
