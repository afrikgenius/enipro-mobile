package com.enipro.data.remote.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;

public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    @Exclude
    private String id;
    private String text;
    private String status = "Sent";

    @Exclude
    private java.util.Date createdAt;
    private Image image;
    @Exclude
    private Voice voice;

    private String sender;
    private String receiver;
    private String senderUid;
    private String receiverUid;
    private long timestamp;
    private User user;
    private String userJSON;
    private long firebaseTimestamp;

    public Message(String id, User user, String text) {
        this(id, user, text, new java.util.Date());
    }

    public Message(String id, User user, String text, java.util.Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Message() {
    }

    public Message(String id, String sender, String receiver, String senderUid, String receiverUid, String message) {
        this.id = (id == null) ? String.valueOf(new Random().nextLong()) : id;
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.text = message;
        this.createdAt = new java.util.Date();
        this.timestamp = createdAt.getTime();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getFirebaseTimestamp(){ return ServerValue.TIMESTAMP; }

    @Exclude
    public long getTimestampLong(){
        return firebaseTimestamp;
    }

    @Override
    @Exclude
    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    @Exclude
    public User getUser() {
        return this.user;
    }

    @Exclude
    public void setUser(User user) {
        this.user = user;
        // Convert user Object to JSON Object
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        this.userJSON = gson.toJson(user, type);
    }

    public String getUserJSON() {
        return userJSON;
    }

    public void setUserJSON(String userJSON) {
        this.userJSON = userJSON;
        // Convert json object to User object.
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        this.user = gson.fromJson(userJSON, type);
    }

    public String getSenderUid() {
        return this.senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiverUid() {
        return this.receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        this.createdAt = new java.util.Date(timestamp);
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public Voice getVoice() {
        return voice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }
}
