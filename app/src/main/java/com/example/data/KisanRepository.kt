package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class KisanRepository(private val kisanDao: KisanDao) {

    // --- Users ---
    val allUsers: Flow<List<User>> = kisanDao.getAllUsers()
    
    suspend fun getUserById(id: Long): User? = kisanDao.getUserById(id)
    suspend fun getUserByUsername(username: String): User? = kisanDao.getUserByUsername(username)
    suspend fun insertUser(user: User): Long = kisanDao.insertUser(user)
    suspend fun updateUser(user: User) = kisanDao.updateUser(user)
    suspend fun deleteUser(user: User) = kisanDao.deleteUser(user)

    // --- Products ---
    val allProducts: Flow<List<Product>> = kisanDao.getAllProducts()
    
    suspend fun getProductById(id: Long): Product? = kisanDao.getProductById(id)
    suspend fun insertProduct(product: Product): Long = kisanDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = kisanDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = kisanDao.deleteProduct(product)

    // --- Orders ---
    val allOrders: Flow<List<Order>> = kisanDao.getAllOrders()
    
    fun getOrdersForBuyer(buyerId: Long): Flow<List<Order>> = kisanDao.getOrdersForBuyer(buyerId)
    fun getOrdersForDeliveryPartner(partnerId: Long): Flow<List<Order>> = kisanDao.getOrdersForDeliveryPartner(partnerId)
    suspend fun insertOrder(order: Order): Long = kisanDao.insertOrder(order)
    suspend fun updateOrder(order: Order) = kisanDao.updateOrder(order)

    // --- Order Items ---
    fun getOrderItemsForOrder(orderId: Long): Flow<List<OrderItem>> = kisanDao.getOrderItemsForOrder(orderId)
    suspend fun insertOrderItem(item: OrderItem): Long = kisanDao.insertOrderItem(item)

    // --- Payments ---
    val allPayments: Flow<List<Payment>> = kisanDao.getAllPayments()
    suspend fun insertPayment(payment: Payment): Long = kisanDao.insertPayment(payment)

    // --- Mandi Rates ---
    val allMandiRates: Flow<List<MandiRate>> = kisanDao.getAllMandiRates()
    suspend fun insertMandiRate(rate: MandiRate): Long = kisanDao.insertMandiRate(rate)

    // --- Government Schemes ---
    val allSchemes: Flow<List<GovernmentScheme>> = kisanDao.getAllSchemes()
    suspend fun insertScheme(scheme: GovernmentScheme): Long = kisanDao.insertScheme(scheme)

    // --- Support Tickets ---
    val allTickets: Flow<List<SupportTicket>> = kisanDao.getAllTickets()
    fun getTicketsForUser(userId: Long): Flow<List<SupportTicket>> = kisanDao.getTicketsForUser(userId)
    suspend fun insertTicket(ticket: SupportTicket): Long = kisanDao.insertTicket(ticket)
    suspend fun updateTicket(ticket: SupportTicket) = kisanDao.updateTicket(ticket)

    // --- Notifications ---
    val allNotifications: Flow<List<Notification>> = kisanDao.getAllNotifications()
    fun getNotificationsForUser(userId: Long): Flow<List<Notification>> = kisanDao.getNotificationsForUser(userId)
    suspend fun insertNotification(notification: Notification): Long = kisanDao.insertNotification(notification)
    suspend fun updateNotification(notification: Notification) = kisanDao.updateNotification(notification)

    // --- Audit Logs ---
    val allAuditLogs: Flow<List<AuditLog>> = kisanDao.getAllAuditLogs()
    suspend fun insertAuditLog(log: AuditLog): Long = kisanDao.insertAuditLog(log)

    // --- App Settings ---
    val appSettings: Flow<AppSettings?> = kisanDao.getAppSettings()
    suspend fun insertAppSettings(settings: AppSettings): Long = kisanDao.insertAppSettings(settings)

    // --- Prepopulation Logic ---
    suspend fun prepopulateDataIfEmpty() {
        // We use firstOrNull() to inspect users list
        val existingUsers = allUsers.firstOrNull() ?: emptyList()
        if (existingUsers.isNotEmpty()) return // Already prepopulated

        // 1. Create Default Users
        val farmerId = kisanDao.insertUser(User(
            username = "Ramesh Kumar", passwordHash = "123456", role = "FARMER",
            email = "ramesh@kisanbuy.com", phone = "9876543210", verificationStatus = "APPROVED",
            businessName = "Kumar Organic Farm", businessAddress = "Ludhiana, Punjab"
        ))

        val dealerId = kisanDao.insertUser(User(
            username = "Suraj Agro", passwordHash = "123456", role = "DEALER",
            email = "suraj@agro.com", phone = "8765432109", verificationStatus = "APPROVED",
            businessName = "Suraj Agro Fertilizers & Seeds", businessAddress = "Agra Market, Uttar Pradesh",
            businessDocUrl = "GSTIN: 09AAFCS8732K1Z9"
        ))

        val pendingDealerId = kisanDao.insertUser(User(
            username = "Bharat Tractors", passwordHash = "123456", role = "DEALER",
            email = "contact@bharat.com", phone = "8123456789", verificationStatus = "PENDING",
            businessName = "Bharat Tractors & Machinery Ltd.", businessAddress = "Jaipur Bypass, Rajasthan",
            businessDocUrl = "PAN: AABC1234F"
        ))

        val deliveryId = kisanDao.insertUser(User(
            username = "Rajesh Delivery", passwordHash = "123456", role = "DELIVERY",
            email = "rajesh@kisanbuy.com", phone = "7654321098", verificationStatus = "APPROVED",
            businessAddress = "Khanna Hub, Punjab"
        ))

        val adminId = kisanDao.insertUser(User(
            username = "KisanBuy Admin", passwordHash = "123456", role = "ADMIN",
            email = "admin@kisanbuy.com", phone = "6543210987"
        ))

        val superAdminId = kisanDao.insertUser(User(
            username = "KisanBuy Super", passwordHash = "123456", role = "SUPERADMIN",
            email = "super@kisanbuy.com", phone = "5432109876"
        ))

        // 2. Insert App Settings
        kisanDao.insertAppSettings(AppSettings(
            id = 1, platformFeePercent = 5.0, platformFeeFixed = 10.0, isMaintenanceMode = false
        ))

        // 3. Create Default Products (Linked to suraj dealerId)
        val defaultProducts = listOf(
            Product(
                sku = "SEED-MUST-001", name = "Pioneer Hybrid Mustard Seeds (1kg)",
                category = "Seeds", subCategory = "Mustard", dealerId = dealerId,
                price = 750.0, discountPrice = 680.0, stock = 120,
                description = "High yielding premium hybrid mustard seeds. Resistant to major diseases like white rust. Suitable for timely sowing across India.",
                isFeatured = true
            ),
            Product(
                sku = "SEED-WHEA-002", name = "Sonalika Quality Wheat Seeds (10kg)",
                category = "Seeds", subCategory = "Wheat", dealerId = dealerId,
                price = 450.0, discountPrice = 420.0, stock = 300,
                description = "Original Sonalika HD-2967 certified wheat seeds. Superior germination rate and grain quality. Best for Northern plains."
            ),
            Product(
                sku = "FERT-NPK-001", name = "IFFCO NPK Fertilizer 12:32:16 (50kg)",
                category = "Fertilizers", subCategory = "NPK", dealerId = dealerId,
                price = 1450.0, discountPrice = 1350.0, stock = 80,
                description = "High quality chemical NPK complex fertilizer. Highly soluble and ensures balanced nutrient delivery to crops during vegetative growth.",
                isFeatured = true
            ),
            Product(
                sku = "FERT-UREA-002", name = "IFFCO Premium Urea (45kg)",
                category = "Fertilizers", subCategory = "Urea", dealerId = dealerId,
                price = 280.0, discountPrice = 266.0, stock = 150,
                description = "Standard white crystalline nitrogenous chemical fertilizer. Promotes rapid vegetative and lush green canopy growth."
            ),
            Product(
                sku = "PEST-AMIS-001", name = "Syngenta Amistar Fungicide (500ml)",
                category = "Pesticides", subCategory = "Fungicide", dealerId = dealerId,
                price = 1550.0, discountPrice = 1420.0, stock = 45,
                description = "Broad-spectrum systemic fungicide containing Azoxystrobin. Protects crops against early and late blights, powdery mildew, and rusts."
            ),
            Product(
                sku = "TOOL-CULT-001", name = "Falcon Garden Hand Cultivator",
                category = "Farm Tools", subCategory = "Hand Tools", dealerId = dealerId,
                price = 499.0, discountPrice = 450.0, stock = 90,
                description = "Ergonomically designed high-carbon steel three-prong cultivator. Ideal for aeration, weeding, and preparing seedbeds in small plots."
            ),
            Product(
                sku = "TRAC-ARJU-001", name = "Mahindra Arjun 555 DI Tractor (Second Hand)",
                category = "Used Machinery", subCategory = "Tractors", dealerId = dealerId,
                price = 480000.0, discountPrice = 450000.0, stock = 1,
                description = "Excellent working condition, 2021 model Mahindra Arjun. 50 HP category. Only 1200 hours run. Fully serviced, immediate papers handover.",
                specifications = "Engine Power: 50 HP; Hours Run: 1200 hrs; Year: 2021"
            ),
            Product(
                sku = "IRRI-PUMP-001", name = "Texmo 5HP Submersible Pump",
                category = "Irrigation", subCategory = "Pumps", dealerId = dealerId,
                price = 19500.0, discountPrice = 18200.0, stock = 15,
                description = "Heavy duty single-phase water pump with copper rotor. Designed for continuous farming irrigation water lift.",
                specifications = "Power: 5 HP; Phase: Single Phase; Outlet: 2.5 inches"
            ),
            Product(
                sku = "ORGA-NEEM-001", name = "Pure Organic Cold-Pressed Neem Oil (1L)",
                category = "Organic", subCategory = "Pest Control", dealerId = dealerId,
                price = 399.0, discountPrice = 350.0, stock = 120,
                description = "100% natural cold-pressed neem oil pest repellent. Broad spectrum bio-pesticide, completely eco-friendly and safe for pollinators.",
                isFeatured = true
            ),
            Product(
                sku = "SAFE-MASK-001", name = "Chemical Spray Safety Respirator Mask",
                category = "Safety", subCategory = "Protective Wear", dealerId = dealerId,
                price = 750.0, discountPrice = 620.0, stock = 75,
                description = "Dual filter chemical spray breathing protection mask. Essential when applying pesticides and spray treatments."
            )
        )
        for (prod in defaultProducts) {
            kisanDao.insertProduct(prod)
        }

        // 4. Create Mandi Rates
        val mandiRates = listOf(
            MandiRate(cropName = "Wheat (Kanak)", state = "Punjab", district = "Ludhiana", market = "Khanna", minPrice = 2125.0, maxPrice = 2275.0, modalPrice = 2200.0),
            MandiRate(cropName = "Paddy (Dhan)", state = "Haryana", district = "Karnal", market = "Karnal", minPrice = 2183.0, maxPrice = 2350.0, modalPrice = 2250.0),
            MandiRate(cropName = "Mustard (Sarson)", state = "Rajasthan", district = "Alwar", market = "Alwar", minPrice = 5200.0, maxPrice = 5650.0, modalPrice = 5450.0),
            MandiRate(cropName = "Potato (Aloo)", state = "Uttar Pradesh", district = "Agra", market = "Agra", minPrice = 1100.0, maxPrice = 1350.0, modalPrice = 1220.0),
            MandiRate(cropName = "Onion (Pyaz)", state = "Maharashtra", district = "Nashik", market = "Lasalgaon", minPrice = 1400.0, maxPrice = 1950.0, modalPrice = 1700.0),
            MandiRate(cropName = "Cotton (Kapas)", state = "Gujarat", district = "Rajkot", market = "Rajkot", minPrice = 6800.0, maxPrice = 7400.0, modalPrice = 7100.0)
        )
        for (rate in mandiRates) {
            kisanDao.insertMandiRate(rate)
        }

        // 5. Create Government Schemes
        val schemes = listOf(
            GovernmentScheme(
                title = "PM-KISAN Samman Nidhi",
                description = "Financial support scheme to provide direct income transfer of Rs. 6000 per year in three equal installments directly into bank accounts of landholder farmer families.",
                eligibility = "All small and marginal landholding farmer families in India who own cultivable land.",
                benefit = "Direct benefit transfer of Rs. 6,000 per annum in three installments of Rs. 2,000 each.",
                applicationUrl = "https://pmkisan.gov.in"
            ),
            GovernmentScheme(
                title = "Pradhan Mantri Fasal Bima Yojana (PMFBY)",
                description = "Comprehensive yield-based crop insurance scheme that protects farmers from financial loss caused by crop failure due to pests, diseases, droughts, floods, or heavy rainfall.",
                eligibility = "All farmers including sharecroppers and tenant farmers growing notified crops in notified areas are eligible.",
                benefit = "Extremely low flat premium rate: 2% for Kharif, 1.5% for Rabi, and 5% for commercial/horticultural crops. Balance premium subsidised by Govt.",
                applicationUrl = "https://pmfby.gov.in"
            ),
            GovernmentScheme(
                title = "Kisan Credit Card (KCC) Scheme",
                description = "Institutional short-term credit scheme designed to protect farmers from high interest rates of local money lenders by providing quick, easy, and affordable bank credit for farm operational expenses.",
                eligibility = "All owner farmers, tenant farmers, cultivators, or self-help groups of farmers.",
                benefit = "Affordable crop loan credit up to Rs. 3 Lakhs at nominal interest rate of 4% per annum (after 3% prompt repayment incentive).",
                applicationUrl = "https://www.rbi.org.in"
            ),
            GovernmentScheme(
                title = "SMAM - Sub-Mission on Agricultural Mechanization",
                description = "Government program focused on promoting the use of modern farm implements and machinery among small and marginal farmers to improve productivity and reduce labor intensive farm efforts.",
                eligibility = "Individuals, self-help groups of farmers, cooperative societies, and custom hiring centers.",
                benefit = "Subsidies ranging from 40% to 50% for purchasing tractors, power tillers, rotavators, sprayers, harvesters, and drip kits.",
                applicationUrl = "https://agrimachinery.nic.in"
            )
        )
        for (scheme in schemes) {
            kisanDao.insertScheme(scheme)
        }

        // 6. Create Support Tickets
        kisanDao.insertTicket(SupportTicket(
            userId = farmerId, username = "Ramesh Kumar", userRole = "FARMER",
            subject = "Order delay SEED-MUST-001",
            message = "My seed order from Khanna is delayed by 2 days. Can someone check the delivery partner assignment status?",
            status = "OPEN", priority = "HIGH"
        ))
        kisanDao.insertTicket(SupportTicket(
            userId = dealerId, username = "Suraj Agro", userRole = "DEALER",
            subject = "GST invoice configuration",
            message = "Where can I upload my GST details so that it appears on the client's downloadable invoice automatically?",
            status = "RESOLVED", priority = "LOW"
        ))

        // 7. Create Default Notifications
        kisanDao.insertNotification(Notification(
            title = "Welcome to KisanBuy App!",
            message = "India's trusted agriculture marketplace is now live on your phone. Connect directly with verified dealers, check real-time mandi rates, and view personalized weather alerts.",
            type = "GENERAL"
        ))
        kisanDao.insertNotification(Notification(
            title = "PM-KISAN 17th Installment Out",
            message = "The government has released the 17th installment under the PM-KISAN scheme. Check the schemes directory to verify if your name is listed.",
            type = "SCHEME"
        ))
        kisanDao.insertNotification(Notification(
            title = "Weather Alert: Heavy Rain Warning",
            message = "Khanna region is expected to receive moderate to heavy rainfall over the next 48 hours. Secure harvested wheat stacks to prevent moisture damage.",
            type = "WEATHER"
        ))

        // 8. Create Audit Logs
        kisanDao.insertAuditLog(AuditLog(
            userId = adminId, username = "KisanBuy Admin",
            action = "PREPOPULATION", details = "Prepopulated database with high fidelity users, products, mandi rates, schemes, and default parameters on first launch."
        ))
    }
}
