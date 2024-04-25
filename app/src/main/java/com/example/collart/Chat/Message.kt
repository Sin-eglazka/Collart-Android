package com.example.collart.Chat

import com.example.collart.NetworkSystem.Object
import com.google.gson.annotations.SerializedName

data class Message (
        @SerializedName("isRead") val isRead: Boolean,
        @SerializedName("id") val id: String,
        @SerializedName("receiver") val receiver: Object,
        @SerializedName("sender") val sender: Object,
        @SerializedName("message") val message: String,
        @SerializedName("createdAt") val createTime: String,
        @SerializedName("updatedAt") val updateTime: String,
        @SerializedName("files") val files: List<String>
)


