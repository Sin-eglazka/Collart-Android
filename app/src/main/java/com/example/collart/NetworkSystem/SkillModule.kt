package com.example.collart.NetworkSystem

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable

data class Skill(
    @SerializedName("nameRu") val nameRu: String,
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("id") val id: String
): Serializable

object SkillModule {
    suspend fun getAllSkillsRu(): List<String> {
        return withContext(Dispatchers.IO) {
            try {

                val response = NetworkClient.apiService.getAllSkillsRu()

                if (response.isSuccessful) {
                    val responseBody: List<String>? = response.body()
                    responseBody

                } else {
                    val errorCode: Int = response.code()
                    null
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Get all skills failed: ${e.message}"
                null
            }!!
        }
    }
}