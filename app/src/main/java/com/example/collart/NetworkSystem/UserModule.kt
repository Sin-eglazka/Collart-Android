package com.example.collart.NetworkSystem

import com.example.collart.Auth.User
import com.example.collart.Auth.LoginRequest
import com.example.collart.Auth.RegisterUserRequest
import com.example.collart.Auth.UserData
import com.example.collart.Auth.UserSkill
import com.example.collart.MainPage.Home.Projects.Experience
import com.example.collart.MainPage.Home.Projects.toStringValue
import com.example.collart.MainPage.Home.Specialists.Specialist
import com.example.collart.NetworkSystem.NetworkClient.apiService
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.Part
import java.io.File

data class SpecialistResponse(
    @SerializedName("user") val userData: UserData,
    @SerializedName("tools") val tools: List<String>,
    @SerializedName("skills") val skills: List<UserSkill>
)


object UserModule {
    suspend fun loginUser(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email, password)
                val response = apiService.login(request)

                if (response.isSuccessful) {
                    val responseBody: LoginResponse? = response.body()
                    responseBody?.token

                } else {
                    val errorCode: Int = response.code()
                    null
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Login failed: ${e.message}"
                null
            }
        }
    }

    suspend fun registerUser(email: String, password: String, repeatPassword: String, name: String, surname: String,
                             description: String, userPhoto: String, cover: String, searchable: Boolean,
                             experience: Experience, skills: List<String>, tools: List<String>): String? {
        return withContext(Dispatchers.IO) {
            try {

                var experienceString = experience.toStringValue()
                val request = RegisterUserRequest(email, password, repeatPassword, name, surname, description, userPhoto, cover, searchable, experienceString, skills, tools);
                val response = apiService.register(request)

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

    suspend fun getCurrentUser(token: String): User{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentUser("Bearer $token")

                if (response.isSuccessful) {
                    response.body() ?: User(UserData("", false, "", "", "", "", "", "", ""), emptyList(), emptyList())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody)
                    val reason = errorJson.optString("reason")
                    User(UserData("", false, "", "", "", "", "", "", ""), emptyList(), emptyList())
                }
            } catch (e: Exception) {
                // Handle exception
                e.printStackTrace()
                val error = "Register failed: ${e.message}"
                User(UserData("", false, "", "", "", "", "", "", ""), emptyList(), emptyList())
            }
        }
    }

    suspend fun getAllSpecialist(token: String, skills: Array<String>, tools: Array<String>, experience: Array<String>): List<Specialist>{
        return withContext(Dispatchers.IO) {
            try {
                val response = if (skills.isEmpty() && tools.isEmpty() && experience.isEmpty()) {
                    apiService.getAllSpecialists("Bearer $token")
                }
                else{
                    apiService.getFilteredSpecialists("Bearer $token",
                        FilterRequest(skills.toList(), tools.toList(), experience.toList())
                    )
                }

                if (response.isSuccessful) {
                    val responseSpecialists = response.body()
                    responseSpecialists?.map { responseSpec ->

                        Specialist(
                            id = responseSpec.userData.id,
                            name = responseSpec.userData.surname + " " + responseSpec.userData.name,
                            backGroundImage = responseSpec.userData.cover,
                            profession = responseSpec.skills.map { userSkill ->  userSkill.nameRu},
                            avatarImage = responseSpec.userData.userPhoto,
                            experience = Experience.fromString(responseSpec.userData.experience),
                            programs = responseSpec.tools
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

    suspend fun getSpecialist(token: String, userId: String): User?{
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSpecialist("Bearer $token", userId)

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


    suspend fun updateUser(
        token: String,
        email: String?,
        name: String?,
        surname: String?,
        searchable: String?,
        experience: String?,
        passwordHash: String?,
        description: String?,
        tools: List<String>?,
        skills: List<String>?,
        avatar: File?,
        cover: File?
    ) : String {

        var emailRequestBody:RequestBody? = null
        var nameRequestBody:RequestBody? = null
        var surnameRequestBody:RequestBody? = null
        var searchableRequestBody:RequestBody? = null
        var experienceRequestBody:RequestBody? = null
        var descriptionRequestBody:RequestBody? = null

        // Convert strings to RequestBody
        if (email != null) {
            emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), email)
        }
        if (name != null) {
            nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        }
        if (surname != null) {
            surnameRequestBody = RequestBody.create(MediaType.parse("text/plain"), surname)
        }
        if (searchable != null) {
            searchableRequestBody = RequestBody.create(MediaType.parse("text/plain"), searchable)
        }
        if (experience != null) {
            experienceRequestBody = RequestBody.create(MediaType.parse("text/plain"), experience)
        }
        if (description != null) {
            descriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), description)
        }

        var passwordRequestBody:RequestBody? = null
        if (passwordHash != null){
            passwordRequestBody = RequestBody.create(MediaType.parse("text/plain"), passwordHash)
        }


        // Convert list of tools to list of RequestBody
        var toolsRequestBody: List<RequestBody>? = null
        if (tools != null) {
            if (tools.size > 0){
                toolsRequestBody = tools.map {
                    RequestBody.create(MediaType.parse("text/plain"), it)
                }
            }
        }

        var skillsRequestBody: List<RequestBody>? = null
        if (skills != null){
            if (skills.size > 0){
                skillsRequestBody = skills.map {
                    RequestBody.create(MediaType.parse("text/plain"), it)
                }
            }
        }

        // Convert image file to MultipartBody.Part
        val avatarPart = avatar?.let {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), it)
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }

        val coverPart = cover?.let {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), it)
            MultipartBody.Part.createFormData("cover", it.name, requestFile)
        }

        return try {
            // Execute the request
            val call = apiService.updateUser(
                "Bearer $token",
                emailRequestBody,
                nameRequestBody,
                surnameRequestBody,
                searchableRequestBody,
                experienceRequestBody,
                passwordRequestBody,
                passwordRequestBody,
                descriptionRequestBody,
                toolsRequestBody,
                skillsRequestBody,
                avatarPart,
                coverPart
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