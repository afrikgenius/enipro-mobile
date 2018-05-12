package com.enipro.firebase;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.enipro.Application;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.model.Enipro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(Enipro.APPLICATION, "Refreshed token:" + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     * Persist users firebase FCM Token.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        // Send to shared preference
        saveTokenSharedPref(token);
        if (Application.getActiveUser() != null) {
            // Persist the firebase token with the user data on local and API end
            Application.getActiveUser().setFirebaseToken(token);
            new AppExecutors().diskIO().execute(() -> EniproDatabase.getInstance(getApplicationContext()).userDao().updateUser(Application.getActiveUser()));

            // TODO Send new token to the web service.

        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.ARG_USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(Constants.ARG_FIREBASE_TOKEN)
                    .setValue(token);
        }
    }


    /**
     * Saves the token generated to shared preference for retrieval later.
     *
     * @param _token the token to store
     */
    private void saveTokenSharedPref(String _token) {
        // Access Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        // Save to SharedPreferences
        editor.putString(Constants.ARG_FIREBASE_TOKEN, _token);
        editor.apply();
    }
}
