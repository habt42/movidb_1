package com.framgia.habt.moviedb.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.R;
import com.framgia.habt.moviedb.data.model.Account;
import com.framgia.habt.moviedb.ui.activity.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.lang.ref.WeakReference;

/**
 * Created by habt on 9/26/16.
 */
public class AuthenticationTask {
    public static final String SESSION_IN_PREF_KEY = "SESSION_IN_PREF_KEY";
    public static final String SUCCESS_RETURN = "true";
    public static final String APPROVED_RETURN = "approved";
    public static final String REQUEST_TOKEN = "request_token";

    private WeakReference<MainActivity> mActivity;
    private GetSessionIdListener mListener;

    public AuthenticationTask(MainActivity activity, GetSessionIdListener listener) {
        mActivity = new WeakReference<MainActivity>(activity);
        mListener = listener;
    }

    public void getRequestToken() {
        StringRequest strReq = new StringRequest(ApiConst.REQUEST_NEW_TOKEN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        ParseAuthen result = gson.fromJson(response, ParseAuthen.class);
                        if (result.getSuccess().equalsIgnoreCase(SUCCESS_RETURN)) {
                            String url = String.format(ApiConst.AUTHENTICATE_URL,
                                    result.getRequestToken());
                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                            CustomTabsIntent intent = builder.build();
                            if (mActivity != null) {
                                intent.launchUrl(mActivity.get(), Uri.parse(url));
                            }
                        } else {
                            showLoginFailed();
                        }
                        mListener.dismissProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingFailed();
                mListener.dismissProgressDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void getSessionId(String reqToken) {
        String url = String.format(ApiConst.REQUEST_SESSION_ID, reqToken);
        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().create();
                ParseAuthen result = gson.fromJson(response, ParseAuthen.class);
                if (result.getSuccess().equalsIgnoreCase(SUCCESS_RETURN)) {
                    String sessionId = result.getSessionId();
                    if (mActivity != null) {
                        saveSessionId(mActivity.get(), sessionId);
                        getAccountInfo(sessionId);
                    }
                } else {
                    showLoadingFailed();
                }
                mListener.dismissProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingFailed();
                mListener.dismissProgressDialog();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public void getAccountInfo(String sessionId) {
        String url = String.format(ApiConst.GET_ACCOUNT_INFO_URL, sessionId);
        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new GsonBuilder().create();
                Account acc = gson.fromJson(response, Account.class);
                JsonObject gravatarObj = acc.getAvatar().getAsJsonObject(Account.GRAVATAR);
                String hash = gravatarObj.get(Account.HASH).getAsString();
                acc.setGravatarHash(hash);
                mListener.onLoginSuccess(acc);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showGetInfoFailed();
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    public static String getSessionId(Context context) {
        try {
            return PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(SESSION_IN_PREF_KEY, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveSessionId(Context context, String sessionId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SESSION_IN_PREF_KEY, sessionId);
        editor.apply();
    }

    private void showLoginFailed() {
        if (mActivity != null) {
            Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }
    }

    private void showLoadingFailed() {
        if (mActivity != null) {
            Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.json_load_error), Toast.LENGTH_LONG).show();
        }
    }

    private void showGetInfoFailed() {
        if (mActivity != null) {
            Toast.makeText(mActivity.get(), mActivity.get().getResources().getString(R.string.get_info_failed), Toast.LENGTH_LONG).show();
        }
    }

    public interface GetSessionIdListener {
        void dismissProgressDialog();

        void onLoginSuccess(Account account);
    }

    private static class ParseAuthen {
        @SerializedName("success")
        private String mSuccess;
        @SerializedName("request_token")
        private String mRequestToken;
        @SerializedName("session_id")
        private String mSessionId;

        public String getSuccess() {
            return mSuccess;
        }

        public String getRequestToken() {
            return mRequestToken;
        }

        public String getSessionId() {
            return mSessionId;
        }
    }
}
