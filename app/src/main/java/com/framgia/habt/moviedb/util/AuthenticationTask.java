package com.framgia.habt.moviedb.util;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.framgia.habt.moviedb.AppController;
import com.framgia.habt.moviedb.data.model.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.lang.ref.WeakReference;

/**
 * Created by habt on 9/26/16.
 */
public class AuthenticationTask {
    public static final String SUCCESS_RETURN = "true";
    public static final String APPROVED_RETURN = "approved";
    public static final String REQUEST_TOKEN = "request_token";

    private WeakReference<GetSessionIdListener> mListener;

    public AuthenticationTask(GetSessionIdListener listener) {
        mListener = new WeakReference<GetSessionIdListener>(listener);
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
                            if (mListener.get() != null) {
                                mListener.get().onGetRequestTokenResponse(url);
                            }
                        } else {
                            showLoginFailed();
                        }
                        dismissProgressDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingFailed();
                dismissProgressDialog();
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
                    if (mListener.get() != null) {
                        mListener.get().onGetSessionIdResponse(sessionId);
                    }
                } else {
                    showLoadingFailed();
                }
                dismissProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingFailed();
                dismissProgressDialog();
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
                if (mListener.get() != null) {
                    mListener.get().onLoginSuccess(acc);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (mListener.get() != null) {
                    mListener.get().showGetInfoFailed();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void showLoginFailed() {
        if (mListener.get() == null) return;
        mListener.get().showLoginFailed();
    }

    private void showLoadingFailed() {
        if (mListener.get() == null) return;
        mListener.get().showLoadingFailed();
    }

    private void dismissProgressDialog() {
        if (mListener.get() == null) return;
        mListener.get().dismissProgressDialog();
    }

    public interface GetSessionIdListener {
        void dismissProgressDialog();

        void onLoginSuccess(Account account);

        void onGetRequestTokenResponse(String authenticationUrl);

        void onGetSessionIdResponse(String sessionId);

        void showLoginFailed();

        void showLoadingFailed();

        void showGetInfoFailed();
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
