package com.zyablik.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.zyablik.database.DatabaseFactory.dbQuery
import com.zyablik.dto.AuthRequest
import com.zyablik.dto.AuthResponse
import com.zyablik.dto.RegisterRequest
import com.zyablik.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import java.util.*

fun Route.authRoutes(secret: String, issuer: String, audience: String) {
    post("/register") {
        val request = call.receive<RegisterRequest>()
        
        // В реальном приложении здесь должно быть хеширование пароля
        val passwordHash = request.password 
        
        try {
            val userId = dbQuery {
                Users.insertAndGetId {
                    it[name] = request.name
                    it[phone] = request.phone
                    it[Users.passwordHash] = passwordHash
                    it[role] = request.role
                }.value
            }
            
            val token = generateToken(userId, request.role, secret, issuer, audience)
            call.respond(AuthResponse(token, request.name, request.role, userId))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, "Пользователь с таким номером уже существует")
        }
    }

    post("/login") {
        val request = call.receive<AuthRequest>()
        
        val user = dbQuery {
            Users.select { Users.phone eq request.phone }
                .map {
                    AuthResponse(
                        token = "", // Заполним позже
                        name = it[Users.name],
                        role = it[Users.role],
                        userId = it[Users.id].value
                    ) to it[Users.passwordHash]
                }.singleOrNull()
        }

        if (user != null && user.second == request.password) {
            val token = generateToken(user.first.userId, user.first.role, secret, issuer, audience)
            call.respond(user.first.copy(token = token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Неверный логин или пароль")
        }
    }
}

private fun generateToken(userId: Int, role: String, secret: String, issuer: String, audience: String): String {
    return JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("userId", userId)
        .withClaim("role", role)
        .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 24 hours
        .sign(Algorithm.HMAC256(secret))
}
