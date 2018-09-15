package com.enipro.services.firebase;


import android.support.annotation.NonNull;

import com.enipro.model.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseNotificationBuilder {

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "FcmNotificationBuilder";
    private static final String SERVER_API_KEY = "AIzaSyDIUCGpCmkaGUoKt7NL6e_XKHc68TcOxh4";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";

    private String mTitle;
    private String mMessage;
    private String mUsername;
    private String mUid;
    private String mFirebaseToken;
    private String mReceiverFirebaseToken;
    private String mUniqueIdentifier;

    private FirebaseNotificationBuilder() {
    }

    public static FirebaseNotificationBuilder initialize() {
        return new FirebaseNotificationBuilder();
    }

    public FirebaseNotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public FirebaseNotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }


    public FirebaseNotificationBuilder username(String username) {
        mUsername = username;
        return this;
    }

    public FirebaseNotificationBuilder uid(String uid) {
        mUid = uid;
        return this;
    }

    public FirebaseNotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }

    public FirebaseNotificationBuilder receiverFirebaseToken(String receiverFirebaseToken) {
        mReceiverFirebaseToken = receiverFirebaseToken;
        return this;
    }

    public FirebaseNotificationBuilder uniqueIdentifier(String uniqueIdentifier) {
        mUniqueIdentifier = uniqueIdentifier;
        return this;
    }


    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Check status code of response for 200 OK and notify UI that message has been sent.
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseToken);

        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(Constants.TITLE, mTitle);
        jsonObjectData.put(Constants.TEXT, mMessage);
        jsonObjectData.put(Constants.USERNAME, mUsername);
        jsonObjectData.put(Constants.UID, mUid);
        jsonObjectData.put(Constants.FCM_TOKEN, mFirebaseToken);
        jsonObjectData.put(Constants.UNIQUE_IDENTIFIER, mUniqueIdentifier);
        jsonObjectBody.put(Constants.DATA, jsonObjectData);

        return jsonObjectBody;
    }
}
