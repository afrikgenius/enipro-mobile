package com.enipro.data.remote;

import com.enipro.data.remote.model.AuthResponse;
import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.PaginatedResponse;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.data.remote.model.UserCred;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableCache;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EniproRestService {

    // Sign up for an Enipro Account
    @POST("/api/users")
    Call<User> createAccount(@Body User user);

    @GET("/api/users/{user_id}")
    Observable<User> getUser(@Path("user_id") String user_id, @Header("Authorization") String auth_token);

    @PATCH("/api/users/{user_id}")
    Observable<User> updateUser(@Body User user, @Path("user_id") String user_id, @Header("Authorization") String auth_token);

    @POST("/api/oauth_token")
    Call<AuthResponse> auth_token(@Body UserCred cred);

    @GET("/api/users/forgot_password")
    Call<String> forgotPassword(@Query("email") String email);

    @GET("/api/users/search")
    Observable<List<User>> searchEniporUsers(@Query("username") String searchTerm, @Header("Authorization") String auth_token);

    @GET("/api/users/search?type=student")
    Observable<List<User>> searchEniproStudents(@Query("username") String searchTerm, @Header("Authorization") String auth_token);

    @GET("/api/users/search?type=student")
    Observable<List<User>> searchEniproProfessionals(@Query("username") String searchTerm);

    @POST("/api/education")
    Observable<User> addEducation(@Body Education education);

    @DELETE("/api/education/{education_id}")
    Observable<User> deleteEducation(@Path("education_id") String education_id);

    @POST("/api/experience")
    Observable<User> addExperience(@Body Experience experience);

    @DELETE("/api/experience/{experience_id}")
    Observable<User> deleteExperience(@Path("experience_id") String experience_id);

    @POST("/api/feed")
    Observable<Feed> createFeedItem(@Body Feed feed, @Header("Authorization") String auth_token);

    @GET("/api/feed")
    Observable<PaginatedResponse<Feed>> fetchFeed(@Header("Authorization") String auth_token, @Query("page") long page, @Query("per_page") int page_size);

    @GET("/api/feed/{feed_id}")
    Call<Feed> getFeedItem(@Path("feed_id") String feed_id, @Header("Authorization") String auth_token);

    @DELETE("/api/feed/{feed_id}")
    Observable<Void> deleteFeedItem(@Path("feed_id") String feed_id, @Header("Authorization") String auth_token);

    @PATCH("/api/feed/{feed_id}")
    void updateFeedItem(@Body Feed feed, @Path("feed_id") String feed_id, @Header("Authorization") String auth_token);

    @POST("/api/savefeed")
    Observable<User> saveFeed(@Body SavedFeed feed, @Header("Authorization") String auth_token);

    @DELETE("/api/savefeed")
    Observable<User> deleteSavedFeed(@Query("id") String feedId, @Header("Authorization") String auth_token);

    @GET("/api/savefeed")
    Observable<PaginatedResponse<Feed>> getSavedFeeds(@Header("Authorization") String auth_token);

    // Feed Comments
    @POST("/api/feed/{feed_id}/comments")
    Observable<FeedComment> createFeedComment(@Body FeedComment comment, @Path("feed_id") String feed_id);

    @GET("/api/feed/{feed_id}/comments")
    Observable<PaginatedResponse<FeedComment>> fetchComments(@Path("feed_id") String feed_id, @Query("page") long page, @Header("Authorization") String auth_token);

    @DELETE("/api/feed/{feed_id}/comments/{comment_id}")
    Observable<Void> deleteFeedComment(@Path("feed_id") String feed_id, @Path("comment_id") String comment_id);

    @GET("/api/feed/{feed_id}/likes")
    Observable<PaginatedResponse<User>> getFeedLikeUsers(@Path("feed_id") String feed_id, @Header("Authorization") String auth_token);

    @POST("/api/feed/{feed_id}/likes")
    Observable<Feed> likeFeed(@Path("feed_id") String feed_id, @Header("Authorization") String auth_token);

    @DELETE("/api/feed/{feed_id}/likes")
    Observable<Feed> unlikeFeed(@Path("feed_id") String feed_id, @Header("Authorization") String auth_token);

    @GET("/api/feed/{feed_id}/comment/{comment_id}/likes")
    Observable<PaginatedResponse<User>> getCommentLikeUsers(@Path("feed_id") String feed_id, @Path("comment_id") String comment_id, @Header("Authorization") String auth_token);

    @POST("/api/feed/{feed_id}/comment/{comment_id}/likes")
    Observable<FeedComment> likeComment(@Path("feed_id") String feed_id, @Path("comment_id") String comment_id, @Header("Authorization") String auth_token);

    @DELETE("/api/feed/{feed_id}/comment/{comment_id}/likes")
    Observable<FeedComment> unlikeComment(@Path("feed_id") String feed_id, @Path("comment_id") String comment_id, @Header("Authorization") String auth_token);


    ///////////////
    /// CIRCLES
    ///////////////

    @POST("/api/circles/request")
    Observable<Void> sendCircleRequest(@Query("user_id") String user_id, @Header("Authorization") String auth_token);

    @GET("/api/circles/requests")
    Observable<Void> getCircleRequests(@Query("category") String category, @Header("Authorization") String auth_token);

    @POST("/api/circles/requests/{request_id}/accept")
    Observable<Void> acceptCircleRequest(@Path("request_id") String request_id, @Header("Authorization") String auth_token);

    @POST("/api/circles/requests/{request_id}/decline")
    Observable<Void> declineCircleRequest(@Path("request_id") String request_id, @Header("Authorization") String auth_token);

    @GET("/api/circles")
    Call<List<User>> getCircleUsers(@Header("Authorization") String auth_token);

    @POST("/api/circles")
    Observable<User> addUserToCircle(@Body UserConnection userConnection, @Header("Authorization") String auth_token);

    @DELETE("/api/circles/{circle_user_id}")
    Observable<User> deleteUserFromCircle(@Path("circle_user_id") String circle_user_id, @Header("Authorization") String auth_token);

    /////////////////
    /// NETWORKS
    /////////////////

    @POST("/api/networks/request")
    Observable<Void> sendNetworkRequest(@Query("user_id") String user_id, @Header("Authorization") String auth_token);

    @GET("/api/networks/requests")
    Observable<Void> getNetworkRequests(@Query("category") String category, @Header("Authorization") String auth_token);

    @POST("/api/networks/requests/{request_id}/accept")
    Observable<Void> acceptNetworkRequest(@Path("request_id") String request_id, @Header("Authorization") String auth_token);

    @POST("/api/networks/requests/{request_id}/decline")
    Observable<Void> declineNetworkRequest(@Path("request_id") String request_id, @Header("Authorization") String auth_token);

    @GET("/api/networks")
    Call<List<User>> getNetworkUsers(@Header("Authorization") String auth_token);

    @POST("/api/networks")
    Observable<User> addUsersToNetwork(@Body UserConnection userConnection, @Header("Authorization") String auth_token);

    @DELETE("/api/networks/{network_user_id}")
    Observable<User> deleteUserFromNetwork(@Path("network_user_id") String network_user_id, @Header("Authorization") String auth_token);


    /////////////////
    /// MENTORING
    ////////////////

    // TODO Change Void to appropriate return values

    @GET("/api/mentoring/sessions")
    Observable<Void> getMentoringSessions(@Header("Authorization") String auth_token);

    @GET("/api/mentoring/sessions/{session_id}")
    Observable<Void> getMentoringSession(@Path("session_id") String session_id, @Query("mentor_id") String mentor_id, @Header("Authorization") String auth_token);

    @DELETE("/api/mentoring/sessions/{session_id}")
    Observable<Void> deleteMentoringSession(@Path("session_id") String session_id, @Header("Authorization") String auth_token);

    // TODO Change Void body to something appropriate
    @POST("/api/mentoring/sessions/{session_id}/join")
    Observable<Void> joinMentoringSession(@Path("session_id") String session_id, @Body Void v, @Header("Authorization") String auth_token);

    @DELETE("/api/mentoring/sessions/{session_id}/leave")
    Observable<Void> leaveMentoringSession(@Path("session_id") String session_id, @Body Void v, @Header("Authorization") String auth_token);

    @POST("/api/mentoring/requests")
    Observable<Void> createMentoringRequest(@Body Void v, @Header("Authorization") String auth_token);

    @GET("/api/mentoring/requests")
    Observable<Void> getMentoringRequests(@Query("category") String category);

    @POST("/api/mentoring/requests/{request_id}/accept")
    Observable<Void> acceptMentoringRequest(@Path("request_id") String request_id, @Body Void v);

    @DELETE("/api/mentoring/requests/{request_id}/decline")
    Observable<Void> declineMentoringRequest(@Path("request_id") String request_id);

    @GET("/api/mentoring/available")
    Observable<Void> getMentoringAvailableRequests(@Query("interest") String interest, @Header("Authorization") String auth_token);

    @POST("/api/mentoring/available")
    Observable<Void> createMentoringAvailable(@Body Void v);


    /////////////////////
    ///  CHATS
    ////////////////////


    @GET("/api/chat")
    Observable<List<User>> getChats(@Header("Authorization") String auth_token);

    @POST("/api/chat")
    Observable<User> createChat(@Body UserConnection userConnection, @Header("Authorization") String auth_token);

    @DELETE("/api/chat/{chat_user_id}")
    Observable<User> deleteChat(@Path("chat_user_id") String chat_user_id, @Header("Authorization") String auth_token);

}