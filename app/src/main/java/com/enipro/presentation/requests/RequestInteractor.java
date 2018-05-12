package com.enipro.presentation.requests;

import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.User;

public interface RequestInteractor {

    /**
     * Accepts a request at the specified position that was sent by a user
     * @param request the request Information
     * @param user the user that sent the request
     * @param position position of the request in the request list.
     */
    void processAcceptance(Request request, User user, int position);

    void processDecline(Request request, int position);
}
