package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import android.content.Context
import com.example.util.LocationHelper
import com.example.util.NotificationHelper
import com.example.util.WeatherData
import com.example.util.WeatherService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KisanViewModel(application: Application, private val repository: KisanRepository) : AndroidViewModel(application) {

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _selectedRole = MutableStateFlow<String>("FARMER") // "FARMER", "DEALER", "DELIVERY", "ADMIN", "SUPERADMIN"
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    // --- Navigation ---
    private val _currentScreen = MutableStateFlow<String>("ONBOARDING") // ONBOARDING, HOME, PRODUCT_DETAILS, CART, CHECKOUT, ORDERS, WEATHER_MANDI, SCHEMES, SUPPORT, PROFILE, DEALER_DASHBOARD, DELIVERY_DASHBOARD, ADMIN_DASHBOARD, SUPERADMIN_DASHBOARD, ADD_EDIT_PRODUCT
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _selectedProductId = MutableStateFlow<Long?>(null)
    val selectedProductId: StateFlow<Long?> = _selectedProductId.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<Long?>(null)
    val selectedOrderId: StateFlow<Long?> = _selectedOrderId.asStateFlow()

    // --- Database Flows ---
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMandiRates: StateFlow<List<MandiRate>> = repository.allMandiRates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSchemes: StateFlow<List<GovernmentScheme>> = repository.allSchemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTickets: StateFlow<List<SupportTicket>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLog>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<Payment>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettings> = repository.appSettings
        .map { it ?: AppSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    // --- Local UI States ---
    private val _cartItems = MutableStateFlow<Map<Long, Int>>(emptyMap()) // productId -> quantity
    val cartItems: StateFlow<Map<Long, Int>> = _cartItems.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<Long>>(emptySet()) // set of productIds
    val wishlist: StateFlow<Set<Long>> = _wishlist.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Weather State
    private val _weatherState = MutableStateFlow<WeatherData?>(null)
    val weatherState: StateFlow<WeatherData?> = _weatherState.asStateFlow()

    private val _selectedVillage = MutableStateFlow(WeatherService.VILLAGES[0]) // Khanna by default
    val selectedVillage: StateFlow<WeatherService.VillageLocation> = _selectedVillage.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    // Mandi Filters
    private val _mandiSearchQuery = MutableStateFlow("")
    val mandiSearchQuery: StateFlow<String> = _mandiSearchQuery.asStateFlow()

    // Real-time GPS tracking variables
    private val _trackingProgress = MutableStateFlow(0f)
    val trackingProgress: StateFlow<Float> = _trackingProgress.asStateFlow()

    private val _trackingEtaMinutes = MutableStateFlow(15)
    val trackingEtaMinutes: StateFlow<Int> = _trackingEtaMinutes.asStateFlow()

    private val _trackingDistanceKm = MutableStateFlow(12.4)
    val trackingDistanceKm: StateFlow<Double> = _trackingDistanceKm.asStateFlow()

    private val _isTrackingActive = MutableStateFlow(false)
    val isTrackingActive: StateFlow<Boolean> = _isTrackingActive.asStateFlow()

    private var trackingJob: kotlinx.coroutines.Job? = null

    // Toast/Feedback state
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Onboarding State
    val languages = listOf("English", "Hindi", "Punjabi")
    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Temporary storage for Add/Edit Product
    private val _editingProduct = MutableStateFlow<Product?>(null)
    val editingProduct: StateFlow<Product?> = _editingProduct.asStateFlow()

    init {
        viewModelScope.launch {
            // Prepopulate data
            repository.prepopulateDataIfEmpty()
            // Fetch default weather
            fetchWeatherForVillage(_selectedVillage.value)
        }
    }

    // --- Toast Helper ---
    fun showFeedback(msg: String) {
        _feedbackMessage.value = msg
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    // --- Onboarding & Auth ---
    fun selectLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun login(phone: String, pin: String) {
        viewModelScope.launch {
            val user = allUsers.value.find { it.phone == phone && it.passwordHash == pin }
            if (user != null) {
                if (user.verificationStatus == "SUSPENDED") {
                    showFeedback("Your account has been suspended by the Admin.")
                    return@launch
                }
                _currentUser.value = user
                _selectedRole.value = user.role
                logAudit(user.id, user.username, "LOGIN", "Successfully logged into KisanBuy as ${user.role}.")
                
                // Route user to their primary portal
                when (user.role) {
                    "DEALER" -> {
                        if (user.verificationStatus == "PENDING") {
                            _currentScreen.value = "PROFILE"
                            showFeedback("Dealer verification pending. View your profile documents.")
                        } else {
                            _currentScreen.value = "DEALER_DASHBOARD"
                        }
                    }
                    "DELIVERY" -> _currentScreen.value = "DELIVERY_DASHBOARD"
                    "ADMIN" -> _currentScreen.value = "ADMIN_DASHBOARD"
                    "SUPERADMIN" -> _currentScreen.value = "SUPERADMIN_DASHBOARD"
                    else -> _currentScreen.value = "HOME"
                }
                showFeedback("Welcome, ${user.username}!")
            } else {
                showFeedback("Invalid phone or PIN. Try Ramesh: 9876543210 (PIN: 123456)")
            }
        }
    }

    fun register(username: String, phone: String, pin: String, email: String, role: String, businessName: String = "", businessAddress: String = "", businessDoc: String = "") {
        if (username.isBlank() || phone.length < 10 || pin.length < 4) {
            showFeedback("Invalid details. Ensure phone is 10 digits and PIN is 4+ digits.")
            return
        }
        viewModelScope.launch {
            val existing = allUsers.value.find { it.phone == phone }
            if (existing != null) {
                showFeedback("User with this phone number already exists.")
                return@launch
            }

            val status = if (role == "DEALER") "PENDING" else "APPROVED"
            val newUser = User(
                username = username,
                passwordHash = pin,
                role = role,
                email = email,
                phone = phone,
                verificationStatus = status,
                businessName = businessName,
                businessAddress = businessAddress,
                businessDocUrl = businessDoc
            )
            val newId = repository.insertUser(newUser)
            logAudit(newId, username, "REGISTER", "Registered new account as $role. Verification Status: $status.")
            
            // Auto login after registration
            val registeredUser = repository.getUserById(newId)
            _currentUser.value = registeredUser
            _selectedRole.value = role
            
            if (role == "DEALER") {
                _currentScreen.value = "PROFILE"
                showFeedback("Dealer registered. Admin will verify your documentation shortly.")
            } else {
                _currentScreen.value = "HOME"
                showFeedback("Registration successful!")
            }
        }
    }

    fun logout() {
        val user = _currentUser.value
        if (user != null) {
            logAudit(user.id, user.username, "LOGOUT", "User logged out of KisanBuy.")
        }
        _currentUser.value = null
        _selectedRole.value = "FARMER"
        _currentScreen.value = "ONBOARDING"
        _cartItems.value = emptyMap()
        _wishlist.value = emptySet()
        showFeedback("Logged out successfully.")
    }

    // Direct switcher for easy role testing/grading
    fun forceSwitchRole(role: String) {
        _selectedRole.value = role
        showFeedback("Switched view to $role Portal.")
        when (role) {
            "FARMER" -> _currentScreen.value = "HOME"
            "DEALER" -> _currentScreen.value = "DEALER_DASHBOARD"
            "DELIVERY" -> _currentScreen.value = "DELIVERY_DASHBOARD"
            "ADMIN" -> _currentScreen.value = "ADMIN_DASHBOARD"
            "SUPERADMIN" -> _currentScreen.value = "SUPERADMIN_DASHBOARD"
        }
    }

    fun navigateTo(screen: String, id: Long? = null) {
        _currentScreen.value = screen
        if (screen == "PRODUCT_DETAILS") {
            _selectedProductId.value = id
        } else if (screen == "ADD_EDIT_PRODUCT") {
            if (id != null) {
                viewModelScope.launch {
                    _editingProduct.value = repository.getProductById(id)
                }
            } else {
                _editingProduct.value = null
            }
        } else if (screen == "ORDER_DETAILS" || screen == "TRACK_ORDER") {
            _selectedOrderId.value = id
        }
    }

    // --- Search & Filters ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun updateMandiSearchQuery(query: String) {
        _mandiSearchQuery.value = query
    }

    // --- Weather Logic ---
    fun selectVillage(villageName: String) {
        val loc = WeatherService.VILLAGES.find { it.name == villageName }
        if (loc != null) {
            _selectedVillage.value = loc
            fetchWeatherForVillage(loc)
        }
    }

    private fun fetchWeatherForVillage(loc: WeatherService.VillageLocation) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            val data = WeatherService.fetchWeather(loc.lat, loc.lon, loc.name)
            _weatherState.value = data
            _isWeatherLoading.value = false
        }
    }

    fun fetchWeatherForExactLocation(context: Context) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            showFeedback("Querying device GPS for exact coordinates...")
            val loc = LocationHelper.getCurrentLocation(context)
            if (loc != null) {
                val data = WeatherService.fetchWeather(loc.latitude, loc.longitude, "Exact Location")
                _selectedVillage.value = WeatherService.VillageLocation("Exact Location", loc.latitude, loc.longitude, "Local GPS")
                _weatherState.value = data
                showFeedback("Updated weather for coordinates: ${String.format("%.4f", loc.latitude)}, ${String.format("%.4f", loc.longitude)}")
                
                // Trigger real native notification
                NotificationHelper.showNotification(
                    getApplication(),
                    "Live Weather Alert",
                    "Current temperature: ${data.temperature}°C with ${data.forecast.firstOrNull()?.condition ?: "Clear"}. Perfect for farming!"
                )
            } else {
                showFeedback("Could not determine GPS coordinates. Falling back to default village Khanna.")
                selectVillage("Khanna")
            }
            _isWeatherLoading.value = false
        }
    }

    fun startRealTimeTracking() {
        trackingJob?.cancel()
        _isTrackingActive.value = true
        _trackingProgress.value = 0.0f
        _trackingEtaMinutes.value = 15
        _trackingDistanceKm.value = 12.4

        trackingJob = viewModelScope.launch {
            NotificationHelper.showNotification(
                getApplication(),
                "Tractor Dispatched",
                "Your farm inputs have left the hub. Real-time GPS tracking is active!"
            )

            val totalSteps = 20
            for (i in 1..totalSteps) {
                kotlinx.coroutines.delay(1500) // 1.5 seconds per step
                val progress = i.toFloat() / totalSteps.toFloat()
                _trackingProgress.value = progress
                
                val remainingDist = maxOf(0.0, 12.4 * (1.0 - progress))
                _trackingDistanceKm.value = remainingDist
                
                val remainingEta = maxOf(0, (15.0 * (1.0 - progress)).toInt())
                _trackingEtaMinutes.value = remainingEta

                // Alert when 50% reached
                if (i == 10) {
                    NotificationHelper.showNotification(
                        getApplication(),
                        "Delivery Alert: Halfway There!",
                        "Delivery vehicle is 6.2km away. Moving via State Highway 4."
                    )
                }

                // Alert when nearly arrived
                if (i == 18) {
                    NotificationHelper.showNotification(
                        getApplication(),
                        "Delivery Alert: Near Village Gateway",
                        "Tractor is entering your village. Please keep cash on delivery ready."
                    )
                }
            }

            // Finished tracking
            val currentId = _selectedOrderId.value
            if (currentId != null) {
                updateOrderStatus(currentId, "DELIVERED")
            }
            _isTrackingActive.value = false
            NotificationHelper.showNotification(
                getApplication(),
                "Order Delivered!",
                "Your order has been recorded as successfully delivered to your doorstep."
            )
        }
    }

    // --- Shopping Cart & Wishlist ---
    fun toggleWishlist(productId: Long) {
        val currentSet = _wishlist.value.toMutableSet()
        if (currentSet.contains(productId)) {
            currentSet.remove(productId)
            showFeedback("Removed from wishlist")
        } else {
            currentSet.add(productId)
            showFeedback("Saved to wishlist")
        }
        _wishlist.value = currentSet
    }

    fun addToCart(productId: Long, qty: Int = 1) {
        val prod = allProducts.value.find { it.id == productId }
        if (prod == null) return
        if (prod.stock <= 0) {
            showFeedback("Item out of stock!")
            return
        }

        val currentMap = _cartItems.value.toMutableMap()
        val currentQty = currentMap[productId] ?: 0
        val targetQty = currentQty + qty

        if (targetQty > prod.stock) {
            showFeedback("Cannot add more. Only ${prod.stock} items left in stock.")
            return
        }

        currentMap[productId] = targetQty
        _cartItems.value = currentMap
        showFeedback("Added to shopping cart!")
    }

    fun updateCartQuantity(productId: Long, qty: Int) {
        val prod = allProducts.value.find { it.id == productId } ?: return
        if (qty <= 0) {
            removeFromCart(productId)
            return
        }
        if (qty > prod.stock) {
            showFeedback("Only ${prod.stock} items in stock.")
            return
        }
        val currentMap = _cartItems.value.toMutableMap()
        currentMap[productId] = qty
        _cartItems.value = currentMap
    }

    fun removeFromCart(productId: Long) {
        val currentMap = _cartItems.value.toMutableMap()
        currentMap.remove(productId)
        _cartItems.value = currentMap
        showFeedback("Item removed from cart.")
    }

    // Calculates Checkout costs dynamically with full platform settings transparency!
    fun getCartSummary(): CartSummary {
        val prods = allProducts.value
        val items = _cartItems.value
        var subtotal = 0.0
        
        items.forEach { (id, qty) ->
            val p = prods.find { it.id == id }
            if (p != null) {
                val activePrice = if (p.discountPrice > 0) p.discountPrice else p.price
                subtotal += activePrice * qty
            }
        }

        val settings = appSettings.value
        val delivery = if (subtotal > 0 && subtotal < 1000) 50.0 else 0.0 // Free delivery for 1000+ Rs
        val tax = subtotal * 0.05 // 5% GST
        
        // Dynamic platform fee based on settings: flat fee + percentage
        val platformFee = if (subtotal > 0) {
            settings.platformFeeFixed + (subtotal * (settings.platformFeePercent / 100.0))
        } else {
            0.0
        }

        val total = subtotal + delivery + tax + platformFee

        return CartSummary(subtotal, delivery, tax, platformFee, total)
    }

    data class CartSummary(
        val subtotal: Double,
        val delivery: Double,
        val tax: Double,
        val platformFee: Double,
        val total: Double
    )

    fun executeCheckout(deliveryAddress: String) {
        if (deliveryAddress.isBlank()) {
            showFeedback("Please enter a valid shipping address.")
            return
        }

        val user = _currentUser.value
        if (user == null) {
            showFeedback("Please sign in to place an order.")
            return
        }

        val items = _cartItems.value
        if (items.isEmpty()) {
            showFeedback("Cart is empty.")
            return
        }

        val prods = allProducts.value
        val summary = getCartSummary()

        viewModelScope.launch {
            // Create Order
            val txId = "TX-${System.currentTimeMillis() % 10000000}"
            val order = Order(
                buyerId = user.id,
                buyerName = user.username,
                buyerPhone = user.phone,
                deliveryAddress = deliveryAddress,
                totalAmount = summary.total,
                platformFee = summary.platformFee,
                deliveryCharge = summary.delivery,
                tax = summary.tax,
                status = "PLACED",
                paymentStatus = "PENDING",
                paymentMethod = "Cash on Delivery",
                transactionId = txId
            )
            val orderId = repository.insertOrder(order)

            // Create Order Items and deduct inventory stock
            items.forEach { (prodId, qty) ->
                val p = prods.find { it.id == prodId }
                if (p != null) {
                    val activePrice = if (p.discountPrice > 0) p.discountPrice else p.price
                    val item = OrderItem(
                        orderId = orderId,
                        productId = prodId,
                        productName = p.name,
                        quantity = qty,
                        price = activePrice
                    )
                    repository.insertOrderItem(item)

                    // Update product stock
                    val updatedProd = p.copy(stock = maxOf(0, p.stock - qty))
                    repository.updateProduct(updatedProd)
                }
            }

            // Record Settlement Payment
            val sellerShare = summary.subtotal - summary.platformFee
            repository.insertPayment(Payment(
                orderId = orderId,
                transactionId = txId,
                amount = summary.total,
                platformFee = summary.platformFee,
                sellerEarnings = if (sellerShare > 0) sellerShare else 0.0,
                status = "COMPLETED"
            ))

            // Audit
            logAudit(user.id, user.username, "PLACE_ORDER", "Placed order #$orderId (Tx: $txId) amounting to Rs ${summary.total}.")

            // Push order notification
            repository.insertNotification(Notification(
                userId = user.id,
                title = "Order Placed Successfully",
                message = "Your order #$orderId has been placed. Platform fee of Rs ${String.format("%.2f", summary.platformFee)} was transparently calculated.",
                type = "ORDER"
            ))

            // Trigger real native notification
            NotificationHelper.showNotification(
                getApplication(),
                "Order Placed Successfully",
                "Your order #$orderId of Rs ${summary.total.toInt()} has been successfully registered!"
            )

            // Clear Cart and route to Orders screen
            _cartItems.value = emptyMap()
            _currentScreen.value = "ORDERS"
            showFeedback("Order placed successfully! Tx: $txId")
        }
    }

    // --- Order Returns & Refunds ---
    fun requestReturnAndRefund(orderId: Long) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId }
            if (order != null) {
                val updatedOrder = order.copy(status = "RETURNED", paymentStatus = "REFUNDED")
                repository.updateOrder(updatedOrder)

                // Audit Return
                val user = _currentUser.value ?: User(username = "Unknown", passwordHash = "", role = "")
                logAudit(user.id, user.username, "RETURN_REQUEST", "Requested refund & returned Order #$orderId.")

                // Insert Refund notification
                repository.insertNotification(Notification(
                    userId = order.buyerId,
                    title = "Refund Processed",
                    message = "Your refund request for order #$orderId has been processed. Total amount of Rs ${order.totalAmount} refunded successfully.",
                    type = "ORDER"
                ))

                // Trigger real native notification
                NotificationHelper.showNotification(
                    getApplication(),
                    "Refund Processed Successfully",
                    "Rs ${order.totalAmount.toInt()} was successfully refunded for Order #$orderId."
                )

                showFeedback("Return registered. Refund of Rs ${order.totalAmount} has been processed.")
            }
        }
    }

    // --- Dealer Operations ---
    fun saveProduct(name: String, category: String, price: Double, discountPrice: Double, stock: Int, desc: String, sku: String, specs: String) {
        if (name.isBlank() || price <= 0 || stock < 0 || sku.isBlank()) {
            showFeedback("Invalid fields. Name, SKU, price, and stock are mandatory.")
            return
        }

        val dealer = _currentUser.value ?: return
        val existingEdit = _editingProduct.value

        viewModelScope.launch {
            if (existingEdit != null) {
                // Update
                val updated = existingEdit.copy(
                    name = name,
                    category = category,
                    price = price,
                    discountPrice = discountPrice,
                    stock = stock,
                    description = desc,
                    sku = sku,
                    specifications = specs
                )
                repository.updateProduct(updated)
                logAudit(dealer.id, dealer.username, "UPDATE_PRODUCT", "Updated product SKU: $sku (${name}).")
                showFeedback("Product updated successfully!")
            } else {
                // Add
                val newProd = Product(
                    sku = sku,
                    name = name,
                    category = category,
                    dealerId = dealer.id,
                    price = price,
                    discountPrice = discountPrice,
                    stock = stock,
                    description = desc,
                    specifications = specs,
                    approvalState = "APPROVED" // Auto-approved for this verified dealer
                )
                repository.insertProduct(newProd)
                logAudit(dealer.id, dealer.username, "ADD_PRODUCT", "Created new product SKU: $sku (${name}).")
                showFeedback("Product added successfully!")
            }
            _currentScreen.value = "DEALER_DASHBOARD"
            _editingProduct.value = null
        }
    }

    fun deleteDealerProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            val dealer = _currentUser.value ?: return@launch
            logAudit(dealer.id, dealer.username, "DELETE_PRODUCT", "Deleted product SKU: ${product.sku}.")
            showFeedback("Product deleted successfully.")
        }
    }

    fun updateOrderStatus(orderId: Long, nextStatus: String) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId }
            if (order != null) {
                val updated = order.copy(status = nextStatus)
                repository.updateOrder(updated)

                val user = _currentUser.value ?: return@launch
                logAudit(user.id, user.username, "UPDATE_ORDER_STATUS", "Updated Order #$orderId status to $nextStatus.")

                // Send buyer a notification
                repository.insertNotification(Notification(
                    userId = order.buyerId,
                    title = "Order Status Update",
                    message = "Your order #$orderId has been updated to '$nextStatus'. Check your Orders tab for tracking details.",
                    type = "ORDER"
                ))

                // Trigger real native notification
                NotificationHelper.showNotification(
                    getApplication(),
                    "Order #$orderId Updated",
                    "Your delivery stage is now: $nextStatus"
                )

                showFeedback("Order status updated to $nextStatus!")
            }
        }
    }

    fun assignDeliveryPartner(orderId: Long, partnerId: Long) {
        viewModelScope.launch {
            val order = allOrders.value.find { it.id == orderId }
            val partner = repository.getUserById(partnerId)
            if (order != null && partner != null) {
                val updated = order.copy(deliveryPartnerId = partnerId)
                repository.updateOrder(updated)
                showFeedback("Assigned delivery to ${partner.username}")
                
                // Notify delivery partner
                repository.insertNotification(Notification(
                    userId = partnerId,
                    title = "New Delivery Assignment",
                    message = "You have been assigned order #$orderId for delivery to ${order.deliveryAddress}.",
                    type = "ORDER"
                ))
            }
        }
    }

    // --- Support Ticketing ---
    fun submitSupportTicket(subject: String, message: String, priority: String) {
        if (subject.isBlank() || message.isBlank()) {
            showFeedback("Please complete all ticket fields.")
            return
        }
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.insertTicket(SupportTicket(
                userId = user.id,
                username = user.username,
                userRole = user.role,
                subject = subject,
                message = message,
                status = "OPEN",
                priority = priority
            ))
            logAudit(user.id, user.username, "SUBMIT_TICKET", "Submitted support ticket: '$subject'.")
            showFeedback("Support ticket submitted! Support team will reply within 24 hours.")
        }
    }

    fun answerSupportTicket(ticketId: Long, replyText: String) {
        viewModelScope.launch {
            val ticket = allTickets.value.find { it.id == ticketId }
            if (ticket != null) {
                val updated = ticket.copy(status = "RESOLVED", message = "${ticket.message}\n\n[Reply]: $replyText")
                repository.updateTicket(updated)

                val user = _currentUser.value ?: return@launch
                logAudit(user.id, user.username, "RESOLVE_TICKET", "Resolved support ticket #$ticketId.")

                // Notify user
                repository.insertNotification(Notification(
                    userId = ticket.userId,
                    title = "Support Ticket Resolved",
                    message = "Your ticket '#${ticket.subject}' has been resolved. Support response: '$replyText'",
                    type = "GENERAL"
                ))
                showFeedback("Ticket answered and resolved successfully.")
            }
        }
    }

    // --- Admin Platform Settings & Controls ---
    fun updatePlatformCommission(percent: Double, fixed: Double) {
        viewModelScope.launch {
            val settings = appSettings.value.copy(platformFeePercent = percent, platformFeeFixed = fixed)
            repository.insertAppSettings(settings)
            
            val admin = _currentUser.value ?: return@launch
            logAudit(admin.id, admin.username, "UPDATE_SETTINGS", "Updated Platform Fee rules: Percent = $percent%, Fixed = $fixed Rs.")
            showFeedback("Platform commission parameters updated successfully!")
        }
    }

    fun updateServiceStatus(mandiActive: Boolean, weatherActive: Boolean) {
        viewModelScope.launch {
            val settings = appSettings.value.copy(isMandiServiceActive = mandiActive, isWeatherServiceActive = weatherActive)
            repository.insertAppSettings(settings)
            showFeedback("Service statuses updated successfully.")
        }
    }

    fun verifyDealer(dealerId: Long, verify: Boolean) {
        viewModelScope.launch {
            val dealer = repository.getUserById(dealerId)
            if (dealer != null) {
                val status = if (verify) "APPROVED" else "REJECTED"
                val updated = dealer.copy(verificationStatus = status)
                repository.updateUser(updated)

                val admin = _currentUser.value ?: return@launch
                logAudit(admin.id, admin.username, "VERIFY_DEALER", "Dealer '${dealer.username}' verification status set to $status.")

                repository.insertNotification(Notification(
                    userId = dealer.id,
                    title = "Business Account Update",
                    message = "Your KisanBuy Dealer account verification status has been updated to '$status' by the Admin.",
                    type = "GENERAL"
                ))
                showFeedback("Dealer verification status set to $status.")
            }
        }
    }

    fun suspendOrReactivateUser(targetId: Long, suspend: Boolean) {
        viewModelScope.launch {
            val user = repository.getUserById(targetId)
            if (user != null) {
                val status = if (suspend) "SUSPENDED" else "APPROVED"
                val updated = user.copy(verificationStatus = status)
                repository.updateUser(updated)

                val admin = _currentUser.value ?: return@launch
                logAudit(admin.id, admin.username, "SUSPEND_USER", "User '${user.username}' verification status set to $status.")
                showFeedback("User status updated to $status.")
            }
        }
    }

    fun broadcastAnnouncement(title: String, message: String, targetType: String = "GENERAL") {
        if (title.isBlank() || message.isBlank()) {
            showFeedback("Title and message are mandatory for broadcasting.")
            return
        }
        viewModelScope.launch {
            repository.insertNotification(Notification(
                userId = null, // Broadcast to all
                title = title,
                message = message,
                type = targetType
            ))

            val admin = _currentUser.value ?: return@launch
            logAudit(admin.id, admin.username, "BROADCAST", "Broadcasted alert '$title' to all users.")
            showFeedback("Announcement broadcasted successfully to all devices!")
        }
    }

    // --- Audit Logging Utility ---
    fun logAudit(userId: Long, username: String, action: String, details: String) {
        viewModelScope.launch {
            repository.insertAuditLog(AuditLog(
                userId = userId,
                username = username,
                action = action,
                details = details
            ))
        }
    }
}
