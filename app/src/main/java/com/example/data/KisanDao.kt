package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KisanDao {

    // --- Users ---
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)


    // --- Products ---
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)


    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE buyerId = :buyerId ORDER BY id DESC")
    fun getOrdersForBuyer(buyerId: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE deliveryPartnerId = :partnerId ORDER BY id DESC")
    fun getOrdersForDeliveryPartner(partnerId: Long): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)


    // --- Order Items ---
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItem): Long


    // --- Payments ---
    @Query("SELECT * FROM payments ORDER BY id DESC")
    fun getAllPayments(): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: Payment): Long


    // --- Mandi Rates ---
    @Query("SELECT * FROM mandi_rates ORDER BY cropName ASC")
    fun getAllMandiRates(): Flow<List<MandiRate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMandiRate(rate: MandiRate): Long


    // --- Government Schemes ---
    @Query("SELECT * FROM government_schemes ORDER BY id DESC")
    fun getAllSchemes(): Flow<List<GovernmentScheme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheme(scheme: GovernmentScheme): Long


    // --- Support Tickets ---
    @Query("SELECT * FROM support_tickets ORDER BY id DESC")
    fun getAllTickets(): Flow<List<SupportTicket>>

    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY id DESC")
    fun getTicketsForUser(userId: Long): Flow<List<SupportTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicket): Long

    @Update
    suspend fun updateTicket(ticket: SupportTicket)


    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId IS NULL ORDER BY id DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification): Long

    @Update
    suspend fun updateNotification(notification: Notification)


    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY id DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long


    // --- App Settings ---
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getAppSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppSettings(settings: AppSettings): Long
}
