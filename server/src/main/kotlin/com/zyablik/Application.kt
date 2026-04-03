package com.zyablik

import com.zyablik.database.DatabaseFactory
import com.zyablik.routes.authRoutes
import com.zyablik.routes.orderRoutes
import com.zyablik.routes.searchHistoryRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(this)
    
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        jwt {
            realm = myRealm
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("TruckLog API is up and running!")
        }
        authRoutes(secret, issuer, audience)
        orderRoutes()
        searchHistoryRoutes()
    }
}
