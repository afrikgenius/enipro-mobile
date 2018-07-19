package com.enipro.presentation.profile;


import com.enipro.Application;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.model.Constants;
import com.enipro.model.LocalCallback;

import java.util.List;

/**
 * Determines profile interaction between the logged in application user and a user being viewed
 * in the profile activity.
 * Based on the user type of both users, it determines what actions can be done on the profile user page.
 */
public class ProfileInteractor {

    private User applicationUser;
    private User profileUser;
    private ProfileContract.Presenter presenter;
    private LocalCallback<String> callback;

    ProfileInteractor(User profileUser, ProfileContract.Presenter presenter, LocalCallback<String> callback) {
        this.applicationUser = Application.getActiveUser();
        this.profileUser = profileUser;
        this.presenter = presenter;
        this.callback = callback;
    }

    /**
     * Checks if the user is connected to the application user.
     */
    private boolean isConnected() {
        return true;
    }


    private boolean isMentoring() {
        return true;
    }

    public boolean isMentored() {
        return false;
    }

    /**
     * Checks if the profile user exists in the application users circle or network.
     *
     * @return
     */
    private boolean isInCircleOrNetwork() {
        List<UserConnection> connections = null;
        if (applicationUser.getUserType().toUpperCase().equals(Constants.STUDENT)) {
            connections = applicationUser.getCircle();
        } else
            connections = applicationUser.getNetwork();

        // No users in application user's network or circle as the case may be.
        if (connections == null || connections.size() == 0)
            return false;

        // Check if profile user exists in connections list
        String profileUserId = profileUser.get_id().getOid();
        for (int i = 0, size = connections.size(); i < size; i++) {
            if (profileUserId.equals(connections.get(i).getUserId()))
                return true;
        }
        return false;
    }


    /***
     * Processes both profiles of the application user and profile user.
     */
    public void process() {
        switch (applicationUser.getUserType().toUpperCase()) {
            case Constants.STUDENT:
                switch (profileUser.getUserType().toUpperCase()) {
                    case Constants.STUDENT:
                        if (isInCircleOrNetwork())
                            callback.respond(Constants.CONNECTION_IN_CIRCLE);
                        else
                            callback.respond(Constants.CONNECTION_ADD_CIRCLE);
                        break;
                    case Constants.PROFESSIONAL:
                        // TODO This should not be made here, Should be a part of application user data.
                        // In this case, either a request for mentoring, pending, or mentoring is gotten back as the string
                        presenter.getRequest(applicationUser.get_id().getOid(), profileUser.get_id().getOid(), requests -> {
                            if(requests.size() == 0) {
                                callback.respond(Constants.CONNECTION_REQUEST_MENTORING);
                            } else if (requests.get(0).getStatus().toUpperCase().equals(Constants.CONNECTION_PENDING))
                                callback.respond(Constants.CONNECTION_PENDING);
                            else if(requests.get(0).getStatus().toUpperCase().equals(Constants.CONNECTION_ACCEPTED))
                                callback.respond(Constants.CONNECTION_MENTORING);
                        });
                        break;
                    // TODO Support should be added for both company and schools
                }
                break;
            case Constants.PROFESSIONAL:
                switch (profileUser.getUserType().toUpperCase()) {
                    case Constants.STUDENT:
                        // Check if the student is being mentored by the professional
                        callback.respond(Constants.NO_CONNECTION);
                        break;
                    case Constants.PROFESSIONAL:
                        if (isInCircleOrNetwork())
                            callback.respond(Constants.CONNECTION_IN_NETWORK);
                        else
                            callback.respond(Constants.CONNECTION_ADD_NETWORK);
                        break;
                }
                break;
            // TODO Support should be added for both company and school.
        }
    }
}
