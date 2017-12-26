package com.enipro.data.remote;

import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.Login;
import com.enipro.data.remote.model.Post;
import com.enipro.data.remote.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

public interface EniproRestService {

    /**
     *
     * @param user
     * @return
     */
    // Sign up for an Enipro Account
    @POST("/api/users")
    Call<User> signup(@Body User user);

    /**
     *
     * @param user_id
     * @return
     */
    @GET("/api/users/{user_id}")
    Call<User> getUser(@Path("user_id") String user_id);

    // Login to an account
    @POST("/api/users/login")
    Call<User> login(@Body Login loginCred);

    @PUT("/api/users/{user_id}")
    Call<User> updateStudent();

    Call<User> updateProfessional();

    @GET("/api/users/forgot_password")
    Call<String> forgotPassword(@Query("email") String email);

    /*
        Rest Endpoints for Forum feature in the application
     */
    @GET("/api/forum/threads")
    Call<List<Thread>> getThreads();

    @POST("/api/forum/threads")
    Call<Thread> createThread(@Body Thread thread);

    @GET("/api/forum/threads/{threadId}")
    Call<Thread> getThread(@Path("threadId") String threadId);

    @DELETE("/api/forum/threads/{threadId}")
    Call<Thread> deleteThread(@Path("threadId") String threadId);

    /* Forum Posts */


    Call<Post> createPost(@Body Post post); // Create a post.

    /* Post Comments */


    /*
        Rest Endpoints for News Feed Feature in the application.
     */
    @POST("/api/users/{user_id}/newsfeed")
    Call<Feed> createFeedItem(@Body Feed feed, @Path("user_id") String user_id); // Create a news feed item

    @GET("/api/users/{user_id}/newsfeed")
    Observable<List<Feed>> getFeeds(@Body User user); // Get list of feeds for a particular user.

    @GET("/api/newsfeed/{feed_id}")
    Call<Feed> getFeedItem(@Path("feed_id") String feed_id); // Get a feed item corresponding to the feed id

    @DELETE("/api/newsfeed/{feed_id}")
    Call<Void> deleteFeedItem(@Path("feed_id") String feed_id); // Delete a feed item from the application.]

    @PATCH("/api/newsfeed/{feed_id}")
    void updateFeedItem(@Body Feed feed, @Path("feed_id") String feed_id); // Update feed item to persist state of the feed.


    // Feed Comments
    @POST("/api/newsfeed/{feed_id}/comments")
    Call<FeedComment> createFeedComment(@Path("feed_id") String feed_id); // Create a comment on the feed with the feed_id

    @GET("/api/newsfeed/{feed_id}/comments")
    Call<List<FeedComment>> getFeedComments(@Path("feed_id") String feed_id); // Get all comments under the feed with the feed_id

    @DELETE("/api/newsfeed/{feed_id}/comments/{comment_id}")
    Call<Void> deleteFeedComment(@Path("feed_id") String feed_id, @Path("comment_id") String comment_id); // Delete a comment.


    /****
     * Endpoints for network and circles feature of the application
     ***/

    /**
     *
     * @param user_id
     * @return
     */
    @GET("/api/users/{user_id}/circles")
    Call<List<User>> getCircleUsers(@Path("user_id") String user_id);

    /**
     *
     * @param user_id
     * @param user
     * @return
     */
    @POST("/api/users/{user_id}/circles")
    Call<Void> addUserToCircle(@Path("user_id") String user_id, @Body User user);

    /**
     *
     * @param user_id
     * @param circle_user_id
     * @return
     */
    @DELETE("/api/users/{user_id}/circles/{circle_user_id}")
    Observable<List<User>> deleteUserFromCircle(@Path("user_id") String user_id, @Path("circle_user_id") String circle_user_id);

    /**
     *
     * @param user_id
     * @return
     */
    @GET("/api/users/{user_id}/networks")
    Call<List<User>> getNetworkUsers(@Path("user_id") String user_id);

    /**
     *
     * @param user_id
     * @param user
     * @return
     */
    @POST("/api/users/{user_id}/networks")
    Call<Void> addUsersToNetwork(@Path("user_id") String user_id, @Body User user);

    /**
     *
     * @param user_id
     * @param network_user_id
     * @return
     */
    @DELETE("/api/users/{user_id}/networks/{network_user_id}")
    Call<Void> deleteUserFromNetwork(@Path("user_id") String user_id, @Path("network_user_id") String network_user_id);


}