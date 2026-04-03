package com.zyablik.routes

import com.zyablik.database.DatabaseFactory.dbQuery
import com.zyablik.dto.OrderRequest
import com.zyablik.dto.OrderResponse
import com.zyablik.models.Orders
import com.zyablik.models.OrderStatus
import com.zyablik.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

fun Route.orderRoutes() {
    authenticate {
        post("/orders") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            
            val request = call.receive<OrderRequest>()
            
            val orderId = dbQuery {
                Orders.insertAndGetId {
                    it[type] = request.type
                    it[weight] = request.weight.toBigDecimal()
                    it[destination] = request.destination
                    it[deliveryDate] = LocalDateTime.parse(request.deliveryDate).toKotlinLocalDateTime()
                    it[comment] = request.comment
                    it[customerId] = EntityID(userId, Users)
                    it[status] = OrderStatus.PENDING
                }.value
            }
            
            call.respond(HttpStatusCode.Created, mapOf("id" to orderId, "message" to "Заказ #$orderId создан"))
        }

        get("/orders") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val role = principal.payload.getClaim("role").asString() ?: ""

            val orders = dbQuery {
                val query = if (role == "customer") {
                    Orders.select { Orders.customerId eq userId }
                } else {
                    Orders.select { (Orders.driverId eq userId) or (Orders.driverId.isNull()) }
                }
                
                query.map {
                    OrderResponse(
                        id = it[Orders.id].value,
                        type = it[Orders.type],
                        weight = it[Orders.weight].toDouble(),
                        destination = it[Orders.destination],
                        deliveryDate = it[Orders.deliveryDate].toString(),
                        status = it[Orders.status].name,
                        customerId = it[Orders.customerId].value,
                        driverId = it[Orders.driverId]?.value,
                        comment = it[Orders.comment],
                        createdAt = it[Orders.createdAt].toString()
                    )
                }
            }
            call.respond(orders)
        }

        post("/orders/{id}/accept") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            
            val orderId = call.parameters["id"]?.toIntOrNull()
            if (orderId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val result = dbQuery {
                val order = Orders.select { Orders.id eq orderId }.singleOrNull()
                if (order == null) return@dbQuery HttpStatusCode.NotFound
                if (order[Orders.status] != OrderStatus.PENDING) return@dbQuery HttpStatusCode.Conflict
                
                Orders.update({ Orders.id eq orderId }) {
                    it[status] = OrderStatus.ACCEPTED
                    it[driverId] = EntityID(userId, Users)
                }
                HttpStatusCode.OK
            }
            call.respond(result)
        }

        post("/orders/{id}/status") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt()
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            
            val orderId = call.parameters["id"]?.toIntOrNull()
            if (orderId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            
            val newStatusStr = call.request.queryParameters["status"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            
            val newStatus = try { OrderStatus.valueOf(newStatusStr.uppercase()) } catch (e: Exception) { return@post call.respond(HttpStatusCode.BadRequest) }

            val result = dbQuery {
                val order = Orders.select { Orders.id eq orderId }.singleOrNull()
                if (order == null) return@dbQuery HttpStatusCode.NotFound
                if (order[Orders.driverId]?.value != userId) return@dbQuery HttpStatusCode.Forbidden
                
                Orders.update({ Orders.id eq orderId }) {
                    it[status] = newStatus
                }
                HttpStatusCode.OK
            }
            call.respond(result)
        }

        post("/orders/{id}/cancel") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                val role = principal.payload.getClaim("role").asString()
                
                val orderId = call.parameters["id"]?.toIntOrNull()
                if (orderId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val result = dbQuery {
                    val order = Orders.select { Orders.id eq orderId }.singleOrNull()
                    if (order == null) return@dbQuery HttpStatusCode.NotFound
                    
                    val currentStatus = order[Orders.status]
                    if (currentStatus == OrderStatus.DELIVERED || currentStatus == OrderStatus.CANCELLED_BY_CUSTOMER || currentStatus == OrderStatus.CANCELLED_BY_DRIVER) {
                        return@dbQuery HttpStatusCode.Conflict
                    }

                    // Логируем для отладки
                    println("DEBUG: role=$role, userId=$userId, orderCustomerId=${order[Orders.customerId].value}, orderDriverId=${order[Orders.driverId]?.value}")

                    if (role.equals("customer", ignoreCase = true)) {
                        if (order[Orders.customerId].value != userId) {
                            println("DEBUG: Forbidden - customerId mismatch")
                            return@dbQuery HttpStatusCode.Forbidden
                        }
                    } else if (role.equals("driver", ignoreCase = true)) {
                        if (order[Orders.driverId]?.value != userId) {
                            println("DEBUG: Forbidden - driverId mismatch")
                            return@dbQuery HttpStatusCode.Forbidden
                        }
                    }

                    Orders.update({ Orders.id eq orderId }) {
                        it[status] = if (role.equals("customer", ignoreCase = true)) OrderStatus.CANCELLED_BY_CUSTOMER else OrderStatus.CANCELLED_BY_DRIVER
                    }
                    HttpStatusCode.OK
                }
                call.respond(result)
            } catch (e: Exception) {
                // Логируем ошибку для отладки
                println("CANCEL ERROR: ${e.message}")
                e.printStackTrace()
                
                // Если ошибка произошла внутри respond, это может быть критично.
                // Но скорее всего ошибка в логике выше.
                if (!call.response.isCommitted) {
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
                }
            }
        }
    }
}
