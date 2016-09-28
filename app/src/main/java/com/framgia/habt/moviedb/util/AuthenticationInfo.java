package com.framgia.habt.moviedb.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by habt on 9/28/16.
 */
public class AuthenticationInfo {
    public static final String SESSION_ID_PREF_KEY = "SESSION_ID_PREF_KEY";
    public static final String ACCOUNT_ID_PREF_KEY = "ACCOUNT_ID_PREF_KEY";

    private static SharedPreferences sPrefInstance;

    private AuthenticationInfo() {

    }

    private static synchronized SharedPreferences getPrefInstance(Context context) {
        if (sPrefInstance == null) {
            sPrefInstance = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sPrefInstance;
    }

    public static String getSessionId(Context context) {
        return getPrefInstance(context).getString(SESSION_ID_PREF_KEY, null);
    }

    public static void saveSessionId(Context context, String sessionId) {
        getPrefInstance(context).edit().putString(SESSION_ID_PREF_KEY, sessionId).apply();
    }

    public static String getAccountId(Context context) {
        return getPrefInstance(context).getString(ACCOUNT_ID_PREF_KEY, null);
    }

    public static void saveAccountId(Context context, String id) {
        getPrefInstance(context).edit().putString(ACCOUNT_ID_PREF_KEY, id).apply();
    }

    public static void removeSessionId(Context context) {
        if (getPrefInstance(context).getString(SESSION_ID_PREF_KEY, null) == null) return;
        getPrefInstance(context).edit().remove(SESSION_ID_PREF_KEY).apply();
    }

    public static void removeAccountId(Context context) {
        if (getPrefInstance(context).getString(ACCOUNT_ID_PREF_KEY, null) == null) return;
        getPrefInstance(context).edit().remove(ACCOUNT_ID_PREF_KEY).apply();
    }
}
