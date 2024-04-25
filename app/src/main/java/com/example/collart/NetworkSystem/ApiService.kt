package com.example.collart.NetworkSystem

import com.example.collart.Auth.User
import com.example.collart.Auth.LoginRequest
import com.example.collart.Auth.RegisterUserRequest
import com.example.collart.Chat.Chat
import com.example.collart.Chat.Message
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


    @Headers("Content-Type: application/json")
    @POST("search/filteredOrders")
    suspend fun getFilteredOrders(
        @Header("Authorization") authorization: String,
        @Body request: FilterRequest
    ): Response<List<OrderResponse>>

    @Headers("Content-Type: application/json")
    @POST("search/filteredOrders")
    suspend fun getAllOrders(
        @Header("Authorization") authorization: String
    ): Response<List<OrderResponse>>

    @GET("tab/active/{userId}")
    suspend fun getActiveOrders(@Header("Authorization") authorization: String, @Path("userId") userId: String): Response<List<OrderResponse>>

    @GET("tab/collaborations/{userId}")
    suspend fun getCollaborationOrders(@Header("Authorization") authorization: String, @Path("userId") userId: String): Response<List<OrderResponse>>

    @GET("orders/myOrders/{orderId}")
    suspend fun getOrder(@Header("Authorization") authorization: String, @Path("orderId") orderId: String): Response<OrderResponse>

    // specialists module

    @Headers("Content-Type: application/json")
    @POST("search/filteredUsers")
    suspend fun getFilteredSpecialists(
        @Header("Authorization") authorization: String,
        @Body request: FilterRequest
    ): Response<List<SpecialistResponse>>

    @Headers("Content-Type: application/json")
    @POST("search/filteredUsers")
    suspend fun getAllSpecialists(
        @Header("Authorization") authorization: String
    ): Response<List<SpecialistResponse>>

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

    @GET("tab/portfolio/{userId}")
    suspend fun getPortfolios(@Header("Authorization") authorization: String, @Path("userId") userId: String): Response<List<Portfolio>>

    // favorite module

    @GET("tab/favorites/{userId}")
    suspend fun getFavoriteOrders(@Header("Authorization") authorization: String, @Path("userId") userId: String): Response<List<OrderResponse>>

    @POST("orders/addOrderToFavorite/{orderId}")
    suspend fun addOrderToFavorite(@Header("Authorization") authorization: String, @Path("orderId") orderId: String): Response<Void>


    // chat module

    @Multipart
    @POST("messages/send")
    @JvmSuppressWildcards
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Part("senderID") senderID: RequestBody,
        @Part("receiverID") receiverID: RequestBody,
        @Part("message") message: RequestBody,
        @Part("isRead") isRead: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<Message>

    @GET("messages/chats/{userId}")
    suspend fun getChats(@Header("Authorization") authorization: String, @Path("userId") userId: String): Response<List<Chat>>


    @Headers("Content-Type: application/json")
    @POST("messages/between")
    suspend fun getChatMessages(
            @Header("Authorization") authorization: String,
            @Body request: ChatRequest
        ): Response<List<Message>>

    @Multipart
    @POST("messages/markRead")
    @JvmSuppressWildcards
    suspend fun readMessages(
        @Header("Authorization") authorization: String,
        @Part("senderID") senderID: RequestBody,
        @Part("receiverID") receiverID: RequestBody,
    ): Response<Void>
}

data class LoginResponse(val token: String)
