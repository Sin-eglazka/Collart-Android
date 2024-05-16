package com.example.collart.NetworkSystem

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class Tool(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String
)

object ToolsModule {

    suspend fun getAllTools(token: String): List<Tool> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.getAllTools("Bearer $token")

                if (response.isSuccessful) {
                    if (response.body() == null){
                        emptyList()
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
}