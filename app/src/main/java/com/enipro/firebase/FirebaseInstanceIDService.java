package com.enipro.firebase;


import android.util.Log;

import com.enipro.model.Enipro;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(Enipro.APPLICATION, "Refreshed token:" + refreshedToken);
    }
}
