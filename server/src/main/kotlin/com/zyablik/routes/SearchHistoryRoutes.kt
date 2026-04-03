package com.zyablik.routes

import com.zyablik.database.DatabaseFactory.dbQuery
import com.zyablik.models.SearchHistory
import com.zyablik.models.Users
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

fun Route.searchHistoryRoutes() {
    authenticate {
        post("/search-history") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Int::class) ?: return@post call.respond(HttpStatusCode.Unauthorized)
            
            val queryText = call.receiveText()
            
            dbQuery {
                SearchHistory.insert {
                    it[SearchHistory.userId] = EntityID(userId, Users)
                    it[query] = queryText
                }
            }
            call.respond(HttpStatusCode.Created)
        }

        get("/search-history") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Int::class) ?: return@get call.respond(HttpStatusCode.Unauthorized)

            val history = dbQuery {
                SearchHistory.select { SearchHistory.userId eq userId }
                    .orderBy(SearchHistory.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
                    .limit(10)
                    .map { it[SearchHistory.query] }
            }
            call.respond(history)
        }
    }
}
