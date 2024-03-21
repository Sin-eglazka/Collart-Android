package com.example.collart.NetworkSystem

import com.example.collart.Auth.User
import com.example.collart.Auth.LoginRequest
import com.example.collart.Auth.RegisterUserRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // user module
    @POST("authentication/register")
    suspend fun register(@Body request: RegisterUserRequest): Response<Void>

    @POST("authentication/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("authentication/user")
    suspend fun getCurrentUser(@Header("Authorization") authorization: String): Response<User>

    @Multipart
    @PUT("users/updateUser")
    @JvmSuppressWildcards
    suspend fun updateUser(
        @Header("Authorization") authorization: String,
        @Part("email") email: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("surname") surname: RequestBody?,
        @Part("searchable") searchable: RequestBody?,
        @Part("experience") experience: RequestBody?,
        @Part("passwordHash") passwordHash: RequestBody?,
        @Part("confirmPasswordHash") confirmPasswordHash: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("tools[]") tools: List<RequestBody>?,
        @Part("skills[]") skills: List<RequestBody>?,
        @Part image: MultipartBody.Part?,
        @Part cover: MultipartBody.Part?
        ): Response<Void>

    // skills module
    @GET("skills/ru")
    suspend fun getAllSkillsRu(): Response<List<String>>


    // order module

    @Multipart
    @POST("orders/addOrder")
    @JvmSuppressWildcards
    suspend fun uploadProject(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("skill") skill: RequestBody,
        @Part("taskDescription") taskDescription: RequestBody,
        @Part("projectDescription") projectDescription: RequestBody,
        @Part("experience") experience: RequestBody,
        @Part("dataStart") dataStart: RequestBody,
        @Part("dataEnd") dataEnd: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part files: List<MultipartBody.Part>,
        @Part("tools[]") tools: List<RequestBody>
    ): Response<ResponseBody>

    @GET("search/filteredOrders")
    suspend fun getAllOrders(@Header("Authorization") authorization: String): Response<List<AllOrdersResponse>>

    @GET("tab/active")
    suspend fun getMyActiveOrders(@Header("Authorization") authorization: String): Response<List<AllOrdersResponse>>

    @GET("tab/collaborations")
    suspend fun getMyCollaborationOrders(@Header("Authorization") authorization: String): Response<List<AllOrdersResponse>>

    @GET("tab/favorites")
    suspend fun getMyFavoriteOrders(@Header("Authorization") authorization: String): Response<List<AllOrdersResponse>>

    @GET("orders/myOrders/{orderId}")
    suspend fun getOrder(@Header("Authorization") authorization: String, @Path("orderId") orderId: String): Response<AllOrdersResponse>

    // specialists module

    @GET("/search/users/all")
    suspend fun getAllSpecialists(@Header("Authorization") authorization: String): Response<List<SpecialistResponse>>

    @GET("users/{userId}")
    suspend fun getSpecialist(@Header("Authorization") authorization: String, @Path("userId") userId: String): Response<User>


    // interactions module

    @Headers("Content-Type: application/json")
    @POST("interactions")
    suspend fun createInteraction(@Header("Authorization") authorization: String, @Body request: PostInteraction): Response<Interaction>

    @GET("interactions/owned/")
    suspend fun getResponsesOnMyProjects(@Header("Authorization") authorization: String) : Response<List<InteractionResponse>>

    @GET("interactions/invites/unowned")
    suspend fun getInvitesOnOtherProjects(@Header("Authorization") authorization: String) : Response<List<InteractionResponse>>

    @Headers("Content-Type: application/json")
    @POST("interactions/accept/{interactionId}")
    suspend fun acceptInteraction(@Header("Authorization") authorization: String, @Path("interactionId") interactionId: String, @Body request: AcceptInvite) : Response<Void>

    @Headers("Content-Type: application/json")
    @POST("interactions/reject/{interactionId}")
    suspend fun rejectInteraction(@Header("Authorization") authorization: String, @Path("interactionId") interactionId: String, @Body request: AcceptInvite) : Response<Void>

    // tools module

    @GET("tools")
    suspend fun getAllTools(@Header("Authorization") authorization: String): Response<List<Tool>>

    // portfolio module

    @Multipart
    @POST("projects/addPortfolio")
    @JvmSuppressWildcards
    suspend fun uploadPortfolio(
        @Header("Authorization") authorization: String,
        @Part("name") title: RequestBody,
        @Part("description") taskDescription: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part files: List<MultipartBody.Part>
    ): Response<ResponseBody>

    @GET("tab/portfolio")
    suspend fun getMyPortfolios(@Header("Authorization") authorization: String): Response<List<Portfolio>>

}

data class LoginResponse(val token: String)
