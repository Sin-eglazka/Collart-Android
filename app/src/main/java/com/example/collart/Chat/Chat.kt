package com.example.collart.Chat

import com.example.collart.NetworkSystem.Object
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Chat (
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("lastMessage") val lastMessage: String,
    @SerializedName("userPhotoURL") val userPhoto: String,
    @SerializedName("userID") val userId: String,
    @SerializedName("messageTime") val messageTime: String,
    @SerializedName("unreadMessagesCount") val unreadMessages: Int,
    @SerializedName("userName") val userName: String
) : Serializable