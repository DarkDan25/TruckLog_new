package com.zyablik.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val phone: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val phone: String,
    val password: String,
    val role: String // "customer" or "driver"
)

@Serializable
data class AuthResponse(
    val token: String,
    val name: String,
    val role: String,
    val userId: Int
)

@Serializable
data class OrderRequest(
    val type: String,
    val weight: Double,
    val destination: String,
    val deliveryDate: String, // ISO date string
    val comment: String? = null
)

@Serializable
data class OrderResponse(
    val id: Int,
    val type: String,
    val weight: Double,
    val destination: String,
    val deliveryDate: String,
    val status: String,
    val customerId: Int,
    val driverId: Int? = null,
    val comment: String? = null,
    val createdAt: String
)
