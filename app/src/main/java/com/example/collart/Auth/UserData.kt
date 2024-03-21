package com.example.collart.Auth

import com.google.gson.annotations.SerializedName
import java.io.Serializable

object CurrentUser{
    var user: User? = null
    var token: String = ""
}

data class UserData(
    @SerializedName("userPhoto") val userPhoto: String,
    @SerializedName("searchable") val searchable: Boolean,
    @SerializedName("experience") val experience: String,
    @SerializedName("email") val email: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("description") val description: String,
    @SerializedName("id") val id: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("name") val name: String
) : Serializable

data class RegisterUserRequest (
    @SerializedName("email") val email: String,
    @SerializedName("passwordHash") val password: String,
    @SerializedName("confirmPasswordHash") val repeatPassword: String,
    @SerializedName("name") val name: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("description") val description: String,
    @SerializedName("userPhoto") val userPhoto: String,
    @SerializedName("cover") val cover: String,
    @SerializedName("searchable") val searchable: Boolean,
    @SerializedName("experience") val experience: String,
    @SerializedName("skills") val skills: List<String>,
    @SerializedName("tools") val tools: List<String>,
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class User(
    @SerializedName("user") val userData: UserData,
    @SerializedName("skills") val skills: List<UserSkill>,
    @SerializedName("tools") val tools: List<String>
) : Serializable

data class UserSkill(
    @SerializedName("nameEn") val nameEn: String,
    @SerializedName("primary") val primary: Boolean,
    @SerializedName("nameRu") val nameRu: String
) : Serializable