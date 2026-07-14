package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val role: String, // "FARMER", "DEALER", "DELIVERY", "ADMIN", "SUPERADMIN"
    val email: String = "",
    val phone: String = "",
    val verificationStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED", "SUSPENDED"
    val businessName: String = "",
    val businessAddress: String = "",
    val businessDocUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sku: String,
    val name: String,
    val category: String, // "Seeds", "Fertilizers", "Pesticides", "Farm Tools", "Tractors", "Used Machinery", "Irrigation", "Dairy", "Organic", "Safety"
    val subCategory: String = "",
    val dealerId: Long,
    val price: Double,
    val discountPrice: Double = 0.0,
    val stock: Int,
    val description: String,
    val imageUrl: String = "",
    val specifications: String = "", // JSON or semi-colon separated key:value
    val status: String = "ACTIVE", // "ACTIVE", "ARCHIVED"
    val approvalState: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val buyerId: Long,
    val buyerName: String,
    val buyerPhone: String,
    val deliveryAddress: String,
    val totalAmount: Double,
    val platformFee: Double,
    val deliveryCharge: Double = 0.0,
    val tax: Double = 0.0,
    val status: String, // "PLACED", "ACCEPTED", "PACKED", "SHIPPED", "DELIVERED", "CANCELLED", "RETURNED", "REFUNDED"
    val paymentStatus: String = "PENDING", // "PENDING", "COMPLETED", "REFUNDED"
    val paymentMethod: String = "Cash on Delivery",
    val transactionId: String = "",
    val deliveryPartnerId: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: Double
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val transactionId: String,
    val amount: Double,
    val platformFee: Double,
    val sellerEarnings: Double,
    val status: String = "COMPLETED", // "COMPLETED", "REFUNDED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mandi_rates")
data class MandiRate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cropName: String,
    val state: String,
    val district: String,
    val market: String,
    val minPrice: Double,
    val maxPrice: Double,
    val modalPrice: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "government_schemes")
data class GovernmentScheme(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val eligibility: String,
    val benefit: String,
    val applicationUrl: String = ""
)

@Entity(tableName = "support_tickets")
data class SupportTicket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val username: String,
    val userRole: String,
    val subject: String,
    val message: String,
    val status: String = "OPEN", // "OPEN", "IN_PROGRESS", "RESOLVED"
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long? = null, // null means broadcast to all
    val title: String,
    val message: String,
    val type: String = "GENERAL", // "GENERAL", "ORDER", "WEATHER", "SCHEME"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val username: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val platformFeePercent: Double = 5.0, // e.g. 5%
    val platformFeeFixed: Double = 10.0, // e.g. 10 Rs flat fee
    val isMaintenanceMode: Boolean = false,
    val isWeatherServiceActive: Boolean = true,
    val isMandiServiceActive: Boolean = true
)
