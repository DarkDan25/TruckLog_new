package com.zyablik.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zyablik.models.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init(config: Application) {
        val driverClassName = config.environment.config.property("database.driver").getString()
        val jdbcURL = config.environment.config.property("database.url").getString()
        val user = config.environment.config.property("database.user").getString()
        val password = config.environment.config.property("database.password").getString()

        val database = Database.connect(createHikariDataSource(jdbcURL, driverClassName, user, password))
        
        transaction(database) {
            // Создаем таблицы, если их нет
            SchemaUtils.create(Users, Customers, Orders, Drivers, SearchHistory)
            
            // Принудительно увеличиваем длину колонки статуса для существующих БД
            TransactionManager.current().exec("ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(50)")
        }
        logger.info("Database initialized successfully")
    }

    private fun createHikariDataSource(
        url: String,
        driver: String,
        user: String,
        pass: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        username = user
        password = pass
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
