package com.zyablik.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    LOADING,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED_BY_CUSTOMER,
    CANCELLED_BY_DRIVER
}

object Users : IntIdTable("users") {
    val name = varchar("name", 100)
    val phone = varchar("phone", 20).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20) // 'customer', 'driver'
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

object Customers : Table("customers") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val favoriteAddresses = text("favorite_addresses").nullable()
    override val primaryKey = PrimaryKey(userId)
}

object Orders : IntIdTable("orders") {
    val type = varchar("type", 100)
    val weight = decimal("weight", 10, 2)
    val destination = varchar("destination", 255)
    val deliveryDate = datetime("delivery_date")
    val comment = text("comment").nullable()
    val status = enumerationByName("status", 50, OrderStatus::class).default(OrderStatus.PENDING)
    val customerId = reference("customer_id", Users, onDelete = ReferenceOption.CASCADE)
    val driverId = reference("driver_id", Users, onDelete = ReferenceOption.SET_NULL).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val price = decimal("price", 10, 2).nullable()
}

object Drivers : Table("drivers") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val rating = decimal("rating", 3, 2).default(java.math.BigDecimal.ZERO)
    val currentOrderId = reference("current_order_id", Orders, onDelete = ReferenceOption.SET_NULL).nullable()
    val completedOrdersCount = integer("completed_orders_count").default(0)
    override val primaryKey = PrimaryKey(userId)
}

object SearchHistory : IntIdTable("search_history") {
    val userId = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val query = varchar("query", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
