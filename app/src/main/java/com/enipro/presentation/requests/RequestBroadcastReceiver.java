package com.enipro.presentation.requests;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.enipro.model.Constants;

/**
 * Broadcast receiver that receives broadcast messages from notification requests for
 * circle, network, mentoring and tutoring requests.
 */
public class RequestBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Get intent extra to identify the type of request and so appropriate action will
        // be triggered for the type of request based on the information.
        String requestCode = intent.getStringExtra(Constants.BROADCAST_REQUEST_EXTRA);

    }
}
