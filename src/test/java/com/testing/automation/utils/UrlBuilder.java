package com.testing.automation.utils;

import com.testing.automation.resources.Constants;

public class UrlBuilder implements Constants {

    static String base_url = BASE_URL;

    public static String gameLogin() {
        return base_url + "/login";
    }

    public static String gameCreate(String session_id) {
        return base_url + "/" + session_id + "/create";
    }

    public static String gameMarkSquare(String sessionId, String gameToken, Integer index) {
        return base_url + "/" + sessionId + "/mark_square/" + gameToken + "/" + index;
    }

    public static String gameGetState(String sessionId, String gameToken) {
        return base_url + "/" + sessionId + "/state/" + gameToken;
    }
}
