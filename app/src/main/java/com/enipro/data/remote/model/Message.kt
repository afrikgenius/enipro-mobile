package com.enipro.data.remote.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.MessageContentType

import java.lang.reflect.Type
import java.util.Random

class Message : IMessage, MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    @Exclude
    private var id: String? = null
    private var text: String? = null
    var status = "Sent"

    @Exclude
    private var createdAt: java.util.Date? = null
    private var image: Image? = null
    @Exclude
    var voice: Voice? = null

    var sender: String? = null
    var receiver: String? = null
    var senderUid: String? = null
    var receiverUid: String? = null
    private var timestamp: Long = 0
    private var user: User? = null
    private var userJSON: String? = null
    @get:Exclude
    val timestampLong: Long = 0

    val firebaseTimestamp: Map<String, String>
        get() = ServerValue.TIMESTAMP

    @JvmOverloads constructor(id: String, user: User, text: String, createdAt: java.util.Date = java.util.Date()) {
        this.id = id
        this.text = text
        this.user = user
        this.createdAt = createdAt
    }

    constructor(id: String?, sender: String?, receiver: String?, senderUid: String?, receiverUid: String?, message: String) {
        this.id = id ?: Random().nextLong().toString()
        this.sender = sender
        this.receiver = receiver
        this.senderUid = senderUid
        this.receiverUid = receiverUid
        this.text = message
        this.createdAt = java.util.Date()
        this.timestamp = createdAt!!.time
    }

    override fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    @Exclude
    override fun getCreatedAt(): java.util.Date? {
        return createdAt
    }

    fun setCreatedAt(createdAt: java.util.Date) {
        this.createdAt = createdAt
    }

    @Exclude
    override fun getUser(): User? {
        return this.user
    }

    @Exclude
    fun setUser(user: User) {
        this.user = user
        // Convert user Object to JSON Object
        val gson = Gson()
        val type = object : TypeToken<User>() {

        }.type
        this.userJSON = gson.toJson(user, type)
    }

    fun getUserJSON(): String? {
        return userJSON
    }

    fun setUserJSON(userJSON: String) {
        this.userJSON = userJSON
        // Convert json object to User object.
        val gson = Gson()
        val type = object : TypeToken<User>() {

        }.type
        this.user = gson.fromJson<User>(userJSON, type)
    }

    fun getTimestamp(): Long {
        return this.timestamp
    }

    fun setTimestamp(timestamp: Long) {
        this.timestamp = timestamp
        this.createdAt = java.util.Date(timestamp)
    }

    override fun getImageUrl(): String? {
        return if (image == null) null else image!!.url
    }

    override fun getText(): String? {
        return text
    }

    fun setText(text: String) {
        this.text = text
    }

    fun setImage(image: Image) {
        this.image = image
    }

    class Image(val url: String)

    class Voice(val url: String, val duration: Int)
}
