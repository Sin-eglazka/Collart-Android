package com.example.collart.NetworkSystem

import com.example.collart.Auth.UserData
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.MainPage.Home.Projects.Project
import com.example.collart.NetworkSystem.NetworkClient.apiService
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.Serializable

data class Order(
    @SerializedName("skill") val skill: String,
    @SerializedName("dataStart") val dataStart: String,
    @SerializedName("owner") val owner: UserData,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("experience") val experience: String,
    @SerializedName("image") val image: String,
    @SerializedName("projectDescription") val projectDescription: String,
    @SerializedName("files") val files: List<String>,
    @SerializedName("id") val id: String,
    @SerializedName("dataEnd") val dataEnd: String,
    @SerializedName("taskDescription") val taskDescription: String,
    @SerializedName("title") val title: String
) : Serializable

data class OrderResponse(
    @SerializedName("user") val userData: UserData,
    @SerializedName("tools") val tools: List<String>,
    @SerializedName("order") val order: Order,
    @SerializedName("skill") val skill: Skill?
) : Serializable

data class FilterRequest(
    val skills: List<String>,
    val tools: List<String>,
    val experience: List<String>
)

object OrderModule {

    suspend fun getAllOrders(token: String, skills: Array<String>, tools: Array<String>, experience: Array<String>): List<Project>{
        return withContext(Dispatchers.IO) {
            try {

                val response = if (skills.isEmpty() && tools.isEmpty() && experience.isEmpty()) {
                    apiService.getAllOrders("Bearer $token")
                }
                else{
                    apiService.getFilteredOrders("Bearer $token",
                        FilterRequest(skills.toList(), tools.toList(), experience.toList())
                    )
                }

                if (response.isSuccessful) {
                    val responseProjects = response.body()


                    responseProjects?.map { responseProject ->

                        var skillName = responseProject.skill?.nameRu
                        if (skillName == null){
                            skillName = ""
                        }
                        Project(
                            id = responseProject.order.id,
                            name = responseProject.order.title,
                            image = responseProject.order.image,
                            profession = skillName,
                            description = responseProject.order.taskDescription,
                            experience = Experience.fromString(responseProject.order.experience),
                            authorImage = responseProject.order.owner.userPhoto,
                            programs = responseProject.tools,
                            authorName = responseProject.order.owner.surname + " " + responseProject.order.owner.name,
                            authorId = responseProject.order.owner.id,
                            isActive = responseProject.order.isActive
                        )
                    } ?: emptyList()

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
                    emptyList()
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Get orders failed: ${e.message}"
                emptyList()
            }
        }
    }

    suspend fun getMyOrders(token: String, userId: String): List<Project>{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getActiveOrders("Bearer $token", userId)

                if (response.isSuccessful) {
                    val responseProjects = response.body()
                    responseProjects?.map { responseProject ->

                        Project(
                            id = responseProject.order.id,
                            name = responseProject.order.title,
                            image = responseProject.order.image,
                            profession = responseProject.skill!!.nameRu,
                            description = responseProject.order.taskDescription,
                            experience = Experience.fromString(responseProject.order.experience),
                            authorImage = responseProject.order.owner.userPhoto,
                            programs = responseProject.tools,
                            authorName = responseProject.order.owner.surname + " " + responseProject.order.owner.name,
                            authorId = responseProject.order.owner.id,
                            isActive = responseProject.order.isActive
                        )
                    } ?: emptyList()

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

    suspend fun getMyCollaborationOrders(token: String, userId: String): List<Project>{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCollaborationOrders("Bearer $token", userId)

                if (response.isSuccessful) {
                    val responseProjects = response.body()
                    responseProjects?.map { responseProject ->

                        Project(
                            id = responseProject.order.id,
                            name = responseProject.order.title,
                            image = responseProject.order.image,
                            profession = responseProject.skill!!.nameRu,
                            description = responseProject.order.taskDescription,
                            experience = Experience.fromString(responseProject.order.experience),
                            authorImage = responseProject.order.owner.userPhoto,
                            programs = responseProject.tools,
                            authorName = responseProject.order.owner.surname + " " + responseProject.order.owner.name,
                            authorId = responseProject.order.owner.id,
                            isActive = responseProject.order.isActive
                        )
                    } ?: emptyList()

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

    suspend fun getMyFavoriteOrders(token: String, userId: String): List<Project>{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFavoriteOrders("Bearer $token", userId)

                if (response.isSuccessful) {
                    val responseProjects = response.body()
                    responseProjects?.map { responseProject ->

                        Project(
                            id = responseProject.order.id,
                            name = responseProject.order.title,
                            image = responseProject.order.image,
                            profession = responseProject.skill!!.nameRu,
                            description = responseProject.order.taskDescription,
                            experience = Experience.fromString(responseProject.order.experience),
                            authorImage = responseProject.order.owner.userPhoto,
                            programs = responseProject.tools,
                            authorName = responseProject.order.owner.surname + " " + responseProject.order.owner.name,
                            authorId = responseProject.order.owner.id,
                            isActive = responseProject.order.isActive
                        )
                    } ?: emptyList()

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

    suspend fun getOrder(token: String, orderId: String): OrderResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrder("Bearer $token", orderId)

                if (response.isSuccessful) {
                    response.body()

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
                    null
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                null
            }
        }
    }

    suspend fun isOrderFavorite(token: String, orderId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.isOrderFavorite("Bearer $token", orderId)

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val isLike = response.body()!!
                        if (isLike == "true"){
                            true
                        }
                        else{
                            false
                        }
                    }
                    else{
                        false
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
                    false
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                false
            }
        }
    }

    suspend fun addOrderToFavorite(token: String, orderId: String): String{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.addOrderToFavorite("Bearer $token", orderId)

                if (response.isSuccessful) {
                    "ok"

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

    suspend fun removeOrderFromFavorite(token: String, orderId: String): String{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.removeOrderFromFavorite("Bearer $token", orderId)

                if (response.isSuccessful) {
                    "ok"

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

    suspend fun deleteOrder(token: String, orderId: String): String{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteOrder("Bearer $token", orderId)

                if (response.isSuccessful) {
                    "ok"

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

    suspend fun uploadProject(
        token: String,
        title: String,
        skill: String,
        taskDescription: String,
        projectDescription: String,
        experience: String,
        dataStart: String,
        dataEnd: String,
        imageFile: File?,
        files: List<File>,
        tools: List<String>
    ) : String {

        // Convert strings to RequestBody
        val titleRequestBody = RequestBody.create(MediaType.parse("text/plain"), title)
        val skillRequestBody = RequestBody.create(MediaType.parse("text/plain"), skill)
        val taskDescriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), taskDescription)
        val projectDescriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectDescription)
        val experienceRequestBody = RequestBody.create(MediaType.parse("text/plain"), experience)
        val dataStartRequestBody = RequestBody.create(MediaType.parse("text/plain"), dataStart)
        val dataEndRequestBody = RequestBody.create(MediaType.parse("text/plain"), dataEnd)

        // Convert image file to MultipartBody.Part
        val imagePart = imageFile?.let {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), it)
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }

        // Convert list of file to list of MultipartBody.Part
        val fileParts = files.map {
            val requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), it)
            MultipartBody.Part.createFormData("files[]", it.name, requestFile)
        }

        // Convert list of tools to list of RequestBody
        val toolsRequestBodies = tools.map {
            RequestBody.create(MediaType.parse("text/plain"), it)
        }

        return try {
            // Execute the request
            val call = apiService.uploadProject(
                "Bearer $token",
                titleRequestBody,
                skillRequestBody,
                taskDescriptionRequestBody,
                projectDescriptionRequestBody,
                experienceRequestBody,
                dataStartRequestBody,
                dataEndRequestBody,
                imagePart,
                fileParts,
                toolsRequestBodies
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

}