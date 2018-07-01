package com.enipro.data.remote.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ChatUser(var uid: String, var email: String, var firebaseToken: String)