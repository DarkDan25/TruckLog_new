package com.zyablik.trucklognew.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class AuthRequest(val phone: String, val password: String)
data class RegisterRequest(val name: String, val phone: String, val password: String, val role: String)
data class AuthResponse(val token: String, val name: String, val role: String, val userId: Int)

data class OrderRequest(
    val type: String,
    val weight: Double,
    val destination: String,
    val deliveryDate: String,
    val comment: String?
)

data class OrderResponse(
    val id: Int,
    val type: String,
    val weight: Double,
    val destination: String,
    val deliveryDate: String,
    val status: String,
    val customerId: Int,
    val driverId: Int?,
    val comment: String?,
    val createdAt: String
)

interface TruckLogApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("orders")
    suspend fun createOrder(@Header("Authorization") token: String, @Body request: OrderRequest): Response<String>

    @GET("orders")
    suspend fun getOrders(@Header("Authorization") token: String): Response<List<OrderResponse>>

    @POST("orders/{id}/accept")
    suspend fun acceptOrder(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    @POST("orders/{id}/status")
    suspend fun updateOrderStatus(@Header("Authorization") token: String, @Path("id") id: Int, @Query("status") status: String): Response<Unit>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    @POST("search-history")
    suspend fun saveSearchQuery(@Header("Authorization") token: String, @Body query: String): Response<Unit>

    @GET("search-history")
    suspend fun getSearchHistory(@Header("Authorization") token: String): Response<List<String>>
}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.78:8080/" // IP for Android Emulator

    val instance: TruckLogApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TruckLogApiService::class.java)
    }
}
