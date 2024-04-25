package com.example.collart.NetworkSystem

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

data class Portfolio(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("files") val files: List<String>,
    @SerializedName("id") val id: String,
    @SerializedName("user") val user: Object
)

object PortfolioModule {
    suspend fun uploadProject(
        token: String,
        name: String,
        description: String,
        image: File?,
        files: List<File>
    ) : String {

        // Convert strings to RequestBody
        val nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val descriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), description)


        // Convert image file to MultipartBody.Part
        val imagePart = image?.let {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), it)
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }

        // Convert list of file to list of MultipartBody.Part
        val fileParts = files.map {
            val requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), it)
            MultipartBody.Part.createFormData("files[]", it.name, requestFile)
        }


        return try {
            // Execute the request
            val call = NetworkClient.apiService.uploadPortfolio(
                "Bearer $token",
                nameRequestBody,
                descriptionRequestBody,
                imagePart,
                fileParts
            )
            if (call.isSuccessful) {
                return "ok"
            } else {
                val errorBody = call.errorBody()?.string()
                val errorJson = JSONObject(errorBody)
                return errorJson.optString("reason")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val error = "Create order failed: ${e.message}"
            return error
        }
    }

    suspend fun getMyPortfolios(token: String, userId: String): List<Portfolio>{
        return withContext(Dispatchers.IO) {
            try {
                val response = NetworkClient.apiService.getPortfolios("Bearer $token", userId)

                if (response.isSuccessful) {
                    if (response.body() != null){
                        val li = response.body()
                        val r = 0
                        li!!
                    }
                    else {
                        emptyList()
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