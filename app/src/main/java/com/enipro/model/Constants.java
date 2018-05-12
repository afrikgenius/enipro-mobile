package com.enipro.model;


import java.util.Calendar;

public class Constants {

    public static final int OPEN_EXTERNAL_STORAGE = 1;
    public static final String ARG_USERS = "users";
    public static final String ARG_RECEIVER = "receiver";
    public static final String ARG_RECEIVER_UID = "receiver_uid";
    public static final String ARG_CHAT_ROOMS = "chat_rooms";
    public static final String ARG_FIREBASE_TOKEN = "firebaseToken";
    public static final String ARG_FRIENDS = "friends";
    public static final String ARG_UID = "uid";
    public static final int MESSAGE_SEARCH = 0x812;
    public static final int MESSAGE_CHAT = 0x813;

    public static final String MESSAGE_SEARCH_RETURN_KEY = "com.enipro.model.Constants.MESSAGE_SEARCH_RETURN_KEY";
    public static final String MESSAGE_CHAT_RETURN_KEY = "com.enipro.model.Constants.MESSAGE_CHAT_RETURN_KEY";

    public static final String INTENT_SEARCH_USER_PROFILE = "com.enipro.model.Constants.INTENT_SEARCH_USER_PROFILE";
    public static final String APPLICATION_USER = "com.enipro.presentation.ProfileActivity.APPLICATION_USER";

    public static final String STUDENT = "STUDENT";
    public static final String PROFESSIONAL = "PROFESSIONAL";
    public static final String COMPANY = "COMPANY";
    public static final String SCHOOL = "SCHOOL";

    public static final String FEED_EXTRA = "feed_extra_post_activity";
    public static final String FEED_BUNDLE = "feed_bundle_class";

    public static final String CONNECTION_ADD_CIRCLE = "ADD TO CIRCLE";
    public static final String CONNECTION_IN_CIRCLE = "IN CIRCLE";
    public static final String CONNECTION_ADD_NETWORK = "ADD TO NETWORK";
    public static final String CONNECTION_IN_NETWORK = "IN NETWORK";
    public static final String CONNECTION_MENTORING = "MENTORING";
    public static final String CONNECTION_TUTORING = "TUTORING";
    public static final String CONNECTION_REQUEST_MENTORING = "REQUEST MENTORING";
    public static final String CONNECTION_REQUEST_TUTORING = "REQUEST_TUTORING";
    public static final String CONNECTON_REQUEST_PENDING = "REQUEST PENDING";
    public static final String CONNECTION_CONNECTED = "Connected";
    public static final String NO_CONNECTION = "NO_CONNECTION";
    public static final String CONNECTION_PENDING = "PENDING";
    public static final String CONNECTION_ACCEPTED = "ACCEPTED";
    public static final String AVAILABLE_FOR_MENTORING = "AVAILABLE FOR MENTORING";
    public static final String MENTORING_REQUEST_SENT = "SENT";

    public static final int FEED_CONTENT_MAX_LINES_IMAGE = 3;
    public static final int FEED_CONTENT_MAX_LINES_TEXT = 5;

    /**
     * Types of request that can be sent in the application.
     * {Circle, Network, Mentoring, Tutoring}
     */
    public static final String TYPE_CIRCLE = "circle";
    public static final String TYPE_NETWORK = "network";
    public static final String TYPE_MENTORING = "Mentoring";
    public static final String TYPE_TUTORING = "Tutoring";

    public static final String CATEGORY_SEND = "send";
    public static final String CATEGORY_AVAILABLE = "available";
    public static final String CATEGORY_CIRCLE = "circle";
    public static final String CATEGORY_NETWORK = "network";


    public static final long DOC_LIMIT_BYTES = 10485760;
    public static final long VIDEO_LIMIT_MILLIS = 480000;
    public static final int VIDEO_SEEK_TO = 200;

    public static final String FIREBASE_CHATS_IMAGES_LOCATION = "chats/";

    public static final String VIDEO_PATH = "com.enipro.model.Constants.VIDEO_PATH";

    public static final String FIREBASE_UPLOAD_REF = "reference";

    public static final String FIREBASE_PROFILE_REF = "images/profile_avatar/";

    /**
     * Location where the feed images are stored in firebase storage.
     */
    public static final String FIREBASE_FEED_IMAGES_LOCATION = "images/feeds/";

    /**
     * Location where the feed videos are stored in firebase storage.
     */
    public static final String FIREBASE_FEED_VIDEO_LOCATION = "videos/feeds/";

    /**
     * Location where the documents added to post as a premium content are stored in firebase storage.
     */
    public static final String FIREBASE_FEED_DOCUMENT_LOCATION = "docs/feeds/";

    /**
     * URL of the default profile image used in the application stored in Firebase Storage.
     */
    public static final String DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/enipro-56ea8.appspot.com/o/profile_image.png?alt=media&token=26a1181a-bcf0-4f63-abda-8d78d65f12f4";

    /**
     * URL of the default profile cover image used in the application stored in Firebase Storage.
     */
    public static final String DEFAULT_PROFILE_COVER_URL = "https://firebasestorage.googleapis.com/v0/b/enipro-56ea8.appspot.com/o/Avatar_Cover.png?alt=media&token=4314280f-c04a-494b-97e6-5b60d060e803";

    public static final int PROFILE_EDIT = 0x0001111;

    public static final int ADD_EDUCATION = 0x011;
    public static final int ADD_EXPERIENCE = 0x012;
    public static final String EDUCATION_EXTRA = "com.enipro.model.Constants.EDUCATION_EXTRA";
    public static final String EXPERIENCE_EXTRA = "com.enipro.model.Constants.EXPERIENCE_EXTRA";

    /**
     * Unique Identifiers for different push notification requests
     */
    public static final String MENTORING_REQUEST_REC = "request_rec";
    public static final String MENTORING_REQUEST_ID = "mentoring_request";
    public static final String MESSAGE_ID = "message";
    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String DATA = "data";
    public static final String USERNAME = "username";
    public static final String UID = "uid";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String UNIQUE_IDENTIFIER = "unique_id";

    public static final String ACCEPT_REQUEST = "accepted";
    public static final String DECLINE_REQUEST = "rejected";

    public static final String MESSAGE = "MESSAGE USER";

    /**
     * Request identifier for a add to circle request.
     */
    public static final String CIRCLE_REQUEST = "circle_request";

    /**
     * Request identifier for a add to network request.
     */
    public static final String NETWORK_REQUEST = "network_request";

    /**
     * Notification channels for all notifications
     */
    public static final String MESSAGE_NOTIFICATION_CHANNEL = "com.enipro.model.Constants.MESSAGE_NOTIFICATION_CHANNEL";
    public static final String MENTORING_REQ_NOTIFICATION_CHANNEL = "com.enipro.model.Constants.MENTORING_REQ_NOTIFICATION_CHANNEL";
    public static final String CIRCLE_REQUEST_NOTIFICATION_CHANNEL = "com.enipro.model.Constants.CIRCLE_REQUEST_NOTIFICATION_CHANNEL";
    public static final String NETWORK_REQUEST_NOTIFICATION_CHANNEL = "com.enipro.model.Constants.NETWORK_REQUEST_NOTIFICATION_CHANNEL";

    /**
     * Broadcast receiver request code for circle, network, mentoring and tutoring requests.
     */
    public static final int CIRCLE_REQUEST_BROADCAST = 0x00;
    public static final int NETWORK_REQUEST_BROADCAST = 0x01;
    public static final int MENTORING_REQUEST_BROADCAST = 0x02;
    public static final int TUTORING_REQUEST_BROADCAST = 0x03;


    // Key for the string that's delivered in the action's intent.
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final String BROADCAST_REQUEST_EXTRA = "com.enipro.model.Constants.BROADCAST_REQUEST_EXTRA";


    /**
     * Days for the week.
     */
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";

    // Request code
    public static final int SCHEDULE_DATA_REQUESTCODE = 0x089;
    public static final String SESSION_SCHEDULE_DATA = "";

    /**
     * Keys for extras in a request object being passed in the application.
     */
    public static final String USER_INFORMATION = "com.enipro.model.Constants.USER_INFORMATION";
    public static final String REQUEST_INFORMATION = "com.enipro.model.Constants.USER_REQUEST";
//    public static final String


}
