package com.example.collart.NetworkSystem

import com.example.collart.Chat.Chat
import com.example.collart.Chat.Message
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

data class ChatRequest(
    @SerializedName("senderID") val senderID: String,
    @SerializedName("receiverID") val receiverID: String,
    @SerializedName("offset") val offset: Int,
    @SerializedName("limit") val limit: Int
)

object ChatModule {


    suspend fun getMyChats(token: String, userId: String): List<Chat>{
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.getChats("Bearer $token", userId)

                if (response.isSuccessful) {
                    response.body() ?: emptyList()

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = errorBody?.let { JSONObject(it) }
                    val reason = errorJson?.optString("reason")
                    emptyList()
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                emptyList()
            }
        }
    }

    suspend fun sendMessage(
        token: String,
        message: String,
        senderId: String,
        receiverId: String,
        files: List<File>
    ) : Message? {

        // Convert strings to RequestBody
        val messageRequestBody = RequestBody.create(MediaType.parse("text/plain"), message)
        val senderIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), senderId)
        val receiverIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), receiverId)
        val isReadRequestBody = RequestBody.create(MediaType.parse("text/plain"), "false")


        // Convert list of file to list of MultipartBody.Part
        val fileParts = files.map {
            val requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), it)
            MultipartBody.Part.createFormData("files[]", it.name, requestFile)
        }


        return try {
            // Execute the request
            val response = NetworkClient.apiService.sendMessage(
                "Bearer $token",
                senderIdRequestBody,
                receiverIdRequestBody,
                messageRequestBody,
                isReadRequestBody,
                fileParts
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = errorBody?.let { JSONObject(it) }
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = "Create order failed: ${e.message}"
            return null
        }
    }

    suspend fun readMessages(
        token: String,
        senderId: String,
        receiverId: String,
    )  {

        val senderIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), senderId)
        val receiverIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), receiverId)

        return try {
            // Execute the request
            val response = NetworkClient.apiService.readMessages(
                "Bearer $token",
                senderIdRequestBody,
                receiverIdRequestBody,
            )
            if (response.isSuccessful) {

            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = errorBody?.let { JSONObject(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = "Create order failed: ${e.message}"
        }
    }

    suspend fun getChatMessages(
        token: String,
        senderId: String,
        receiverId: String,
        offset: Int,
        limit: Int
    ) : List<Message> {

        val chatRequest = ChatRequest(senderId, receiverId, offset, limit)

        return try {
            // Execute the request
            val response = NetworkClient.apiService.getChatMessages(
                "Bearer $token",
                chatRequest
            )
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                val errorBody = response.errorBody()?.string()
                val errorJson = errorBody?.let { JSONObject(it) }
                return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = "Create order failed: ${e.message}"
            return emptyList()
        }
    }

}