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
import java.time.LocalDateTime

fun Route.orderRoutes() {
    authenticate {
        post("/orders") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Int::class) ?: return@post call.respond(HttpStatusCode.Unauthorized)
            
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
            
            call.respond(HttpStatusCode.Created, "Заказ #$orderId создан")
        }

        get("/orders") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Int::class) ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val role = principal.getClaim("role", String::class)

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
    }
}
