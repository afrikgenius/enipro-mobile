package com.enipro.data.remote.model


import com.stfalcon.chatkit.commons.models.IUser

data class MessageUser(private var id: String?, private var name: String?, private var avatar: String?) : IUser {


    override fun getName(): String? {
        return name
    }

    override fun getId(): String? {
        return id
    }

    override fun getAvatar(): String? {
        return avatar
    }

}
