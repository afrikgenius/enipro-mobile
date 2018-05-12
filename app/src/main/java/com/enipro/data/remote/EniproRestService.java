package com.enipro.data.remote;

import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.Login;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface EniproRestService {

    /**
     * @param user
     * @return
     */
    // Sign up for an Enipro Account
    @POST("/api/users")
    Call<User> signup(@Body User user);

    /**
     * @param user_id
     * @return
     */
    @GET("/api/users/{user_id}")
    Call<User> getUser(@Path("user_id") String user_id);

    @PATCH("/api/users/{user_id}")
    Observable<User> updateUser(@Body User user, @Path("user_id") String user_id);

    // Login to an account
    @POST("/api/users/login")
    Call<User> login(@Body Login loginCred);

    @GET("/api/users/forgot_password")
    Call<String> forgotPassword(@Query("email") String email);

    @GET("/api/users/search")
    Observable<List<User>> searchEniporUsers(@Query("username") String searchTerm);

    @GET("/api/users/search?type=student")
    Observable<List<User>> searchEniproStudents(@Query("username") String searchTerm);

    @GET("/api/users/search?type=student")
    Observable<List<User>> searchEniproProfessionals(@Query("username") String searchTerm);

    @POST("/api/users/{user_id}/education")
    Observable<User> addEducation(@Body Education education, @Path("user_id") String user_id);

    @DELETE("/api/users/{user_id}/education/{education_id}")
    Observable<User> deleteEducation(@Path("user_id") String user_id, @Path("education_id") String education_id);

    @POST("/api/users/{user_id}/experience")
    Observable<User> addExperience(@Body Experience experience, @Path("user_id") String user_id);

    @DELETE("/api/users/{user_id}/experience/{experience_id}")
    Observable<User> deleteExperience(@Path("user_id") String user_id, @Path("experience_id") String experience_id);


    /*
        Rest Endpoints for News Feed Feature in the application.
     */
    @POST("/api/users/{user_id}/newsfeed")
    Call<Feed> createFeedItem(@Body Feed feed, @Path("user_id") String user_id); // Create a news feed item

    @GET("/api/users/{user_id}/newsfeed")
    Observable<List<Feed>> getFeeds(@Path("user_id") String user_id); // Get list of feeds for a particular user.

    @GET("/api/newsfeed/{feed_id}")
    Call<Feed> getFeedItem(@Path("feed_id") String feed_id); // Get a feed item corresponding to the feed id

    @DELETE("/api/{user_id}/newsfeed/{feed_id}")
    Observable<Void> deleteFeedItem(@Path("user_id") String user_id, @Path("feed_id") String feed_id); // Delete a feed item from the application.]

    @PATCH("/api/newsfeed/{feed_id}")
    void updateFeedItem(@Body Feed feed, @Path("feed_id") String feed_id); // Update feed item to persist state of the feed.

    @POST("/api/users/{user_id}/savefeed")
    Observable<User> saveFeed(@Body SavedFeed feed, @Path("user_id") String user_id);

    @DELETE("/api/users/{user_id}/savefeed")
    Observable<User> deleteSavedFeed(@Path("user_id") String user_id, @Query("id") String feedId);

    @GET("/api/users/{user_id}/savefeed")
    Observable<List<Feed>> getSavedFeeds(@Path("user_id") String user_id);

    // Feed Comments
    @POST("/api/users/{user_id}/newsfeed/{feed_id}/comments")
    Call<FeedComment> createFeedComment(@Body FeedComment comment, @Path("user_id") String user_id, @Path("feed_id") String feed_id); // Create a comment on the feed with the feed_id

    /**
     * @param user_id
     * @param feed_id
     * @return
     */
    @GET("/api/users/{user_id}/newsfeed/{feed_id}/comments")
    Observable<List<FeedComment>> getFeedComments(@Path("user_id") String user_id, @Path("feed_id") String feed_id); // Get all comments under the feed with the feed_id

    @DELETE("/api/newsfeed/{feed_id}/comments/{comment_id}")
    Observable<Void> deleteFeedComment(@Path("feed_id") String feed_id, @Path("comment_id") String comment_id); // Delete a comment.


    /****
     * Endpoints for network and circles feature of the application
     ***/

    /**
     * @param user_id
     * @return
     */
    @GET("/api/users/{user_id}/circles")
    Call<List<User>> getCircleUsers(@Path("user_id") String user_id);

    /**
     * @param user_id
     * @param userConnection
     * @return
     */
    @POST("/api/users/{user_id}/circles")
    Observable<User> addUserToCircle(@Path("user_id") String user_id, @Body UserConnection userConnection);

    /**
     * @param user_id
     * @param circle_user_id
     * @return
     */
    @DELETE("/api/users/{user_id}/circles/{circle_user_id}")
    Observable<User> deleteUserFromCircle(@Path("user_id") String user_id, @Path("circle_user_id") String circle_user_id);

    /**
     * @param user_id
     * @return
     */
    @GET("/api/users/{user_id}/networks")
    Call<List<User>> getNetworkUsers(@Path("user_id") String user_id);

    /**
     * @param user_id
     * @param userConnection
     * @return
     */
    @POST("/api/users/{user_id}/networks")
    Observable<User> addUsersToNetwork(@Path("user_id") String user_id, @Body UserConnection userConnection);

    /**
     * @param user_id
     * @param network_user_id
     * @return
     */
    @DELETE("/api/users/{user_id}/networks/{network_user_id}")
    Observable<User> deleteUserFromNetwork(@Path("user_id") String user_id, @Path("network_user_id") String network_user_id);


    /**
     * CHATS
     */
    @GET("/api/users/{user_id}/chat")
    Observable<List<User>> getChats(@Path("user_id") String user_id);

    @POST("/api/users/{user_id}/chat")
    Observable<User> createChat(@Path("user_id") String user_id, @Body UserConnection userConnection);

    @DELETE("/api/users/{user_id}/chat/{chat_user_id}")
    Observable<User> deleteChat(@Path("user_id") String user_id, @Path("chat_user_id") String chat_user_id);

    @POST("/api/requests")
    Observable<Request> createRequest(@Body Request request);

    @GET("/api/requests/{request_id}")
    Observable<Request> getRequest(@Path("request_id") String request_id);

    @GET("/api/requests")
    Observable<List<Request>> getRequest(@Query("sender") String sender, @Query("recipient") String recipient);

    @GET("/api/requests")
    Observable<List<Request>> getRequestSender(@Query("sender") String sender);

    @GET("/api/requests")
    Observable<List<Request>> getRequestRecipient(@Query("recipient") String recipient);

    @GET("/api/requests")
    Observable<List<Request>> getRequestWithSide(@Query("user_id") String user_id, @Query("side") String side);

    @PATCH("/api/requests/{request_id}")
    Observable<Request> updateRequest(@Body Request request, @Path("request_id") String request_id);

    @DELETE("/api/requests/{request_id}")
    Observable<Request> deleteRequest(@Path("request_id") String request_id);


    /***
     * paystack Endpoints for payments within the application.
     */


}