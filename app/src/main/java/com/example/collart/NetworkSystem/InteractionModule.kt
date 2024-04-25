package com.example.collart.NetworkSystem

import com.example.collart.Auth.User
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class Interaction(
    @SerializedName("status") val status: String,
    @SerializedName("id") val id: String,
    @SerializedName("order") val order: Object,
    @SerializedName("getter") val getter: Object,
    @SerializedName("sender") val sender: Object
)

data class Object(
    @SerializedName("id") val id: String
)

data class PostInteraction(
    @SerializedName("senderID") val senderId: String,
    @SerializedName("orderID") val orderId: String,
    @SerializedName("getterID") val getterId: String
)

data class InteractionResponse(
    @SerializedName("sender") val sender: User,
    @SerializedName("order") val order: OrderResponse,
    @SerializedName("getter") val getter: User,
    @SerializedName("status") val status: String,
    @SerializedName("id") val id: String
)

data class AcceptInvite(
    @SerializedName("getterID") val id: String
)


object InteractionModule {

    suspend fun createInteraction(token: String, senderId: String, getterId: String, orderId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val interactionRequest = PostInteraction(senderId, orderId, getterId)
                val response = NetworkClient.apiService.createInteraction("Bearer $token", interactionRequest)

                if (response.isSuccessful) {
                    if (response.body() == null){
                        "Error on server"
                    }
                    else {
                        "ok"
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
                    reason
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                error
            }
        }
    }

    suspend fun getResponsesOnMyProjects(token: String): List<InteractionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.getResponsesOnMyProjects("Bearer $token")

                if (response.isSuccessful) {
                    if (response.body() == null){
                        emptyList<InteractionResponse>()
                    }
                    else {
                        response.body()!!
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
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

    suspend fun getInvitesOnOtherProjects(token: String): List<InteractionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.getInvitesOnOtherProjects("Bearer $token")

                if (response.isSuccessful) {
                    if (response.body() == null){
                        emptyList<InteractionResponse>()
                    }
                    else {
                        response.body()!!
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
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

    suspend fun acceptInteraction(token: String, interactionId: String, getterId: String) : Boolean{
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.acceptInteraction("Bearer $token", interactionId, AcceptInvite(getterId))

                response.isSuccessful
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                false
            }
        }
    }

    suspend fun rejectInteraction(token: String, interactionId: String, getterId: String) : Boolean{
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.rejectInteraction("Bearer $token", interactionId, AcceptInvite(getterId))

                response.isSuccessful
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                false
            }
        }
    }
}