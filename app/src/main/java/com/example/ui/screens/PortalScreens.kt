package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.*
import com.example.ui.KisanViewModel
import com.example.util.WeatherData
import com.example.util.WeatherService
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// ONBOARDING & LOGIN SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: KisanViewModel) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }
    
    // Form Inputs
    var phoneInput by remember { mutableStateOf("") }
    var pinInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var isDealerRole by remember { mutableStateOf(false) }
    
    // Dealer Extra Inputs
    var businessNameInput by remember { mutableStateOf("") }
    var businessAddressInput by remember { mutableStateOf("") }
    var businessDocInput by remember { mutableStateOf("") }

    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Selector Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Language, contentDescription = "Language", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                viewModel.languages.forEach { lang ->
                    FilterChip(
                        selected = selectedLanguage == lang,
                        onClick = { viewModel.selectLanguage(lang) },
                        label = { Text(lang) },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Hero Logo with custom local resource
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // We load the official KisanBuy logo we generated
                Image(
                    painter = painterResource(id = R.drawable.kisanbuy_logo),
                    contentDescription = "KisanBuy Official Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "KisanBuy",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Text(
                text = "Kisan ka Saathi, Har Khet ki Unnati",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card Form Container
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterMode) "Create Free Account" else "Verified Quick Sign-In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (isRegisterMode) {
                        // Register Fields
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, "Name") },
                            modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, "Email") },
                            modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Mobile Number (10 digits)") },
                        leadingIcon = { Icon(Icons.Default.Phone, "Phone") },
                        modifier = Modifier.fillMaxWidth().testTag("login_phone_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("Secret 6-Digit PIN") },
                        leadingIcon = { Icon(Icons.Default.Lock, "PIN") },
                        modifier = Modifier.fillMaxWidth().testTag("login_pin_input"),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                    )

                    if (isRegisterMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Role Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Register as Dealer / Merchant?", fontWeight = FontWeight.Medium)
                            Switch(
                                checked = isDealerRole,
                                onCheckedChange = { isDealerRole = it },
                                modifier = Modifier.testTag("reg_role_switch")
                            )
                        }

                        if (isDealerRole) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Merchant Documentation", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = businessNameInput,
                                onValueChange = { businessNameInput = it },
                                label = { Text("Business / Store Name") },
                                modifier = Modifier.fillMaxWidth().testTag("reg_business_name"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = businessAddressInput,
                                onValueChange = { businessAddressInput = it },
                                label = { Text("Physical Shop Address") },
                                modifier = Modifier.fillMaxWidth().testTag("reg_business_address")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = businessDocInput,
                                onValueChange = { businessDocInput = it },
                                label = { Text("GSTIN, PAN or Seed License No.") },
                                modifier = Modifier.fillMaxWidth().testTag("reg_business_doc"),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main Action button
                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                val role = if (isDealerRole) "DEALER" else "FARMER"
                                viewModel.register(
                                    username = nameInput,
                                    phone = phoneInput,
                                    pin = pinInput,
                                    email = emailInput,
                                    role = role,
                                    businessName = businessNameInput,
                                    businessAddress = businessAddressInput,
                                    businessDoc = businessDocInput
                                )
                            } else {
                                viewModel.login(phoneInput, pinInput)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (isRegisterMode) "Register & Log In" else "Secure Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Switch Mode text
                    TextButton(
                        onClick = { isRegisterMode = !isRegisterMode },
                        modifier = Modifier.testTag("switch_auth_mode_button")
                    ) {
                        Text(
                            text = if (isRegisterMode) "Already have an account? Sign In" else "New to KisanBuy? Create Free Account",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo Shortcut Section for quick evaluations
            Text("Demo Quick Access Profiles", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DemoBadge("Farmer / Buyer", "Ramesh", Color(0xFF2E7D32)) {
                    viewModel.login("9876543210", "123456")
                }
                DemoBadge("Agro Dealer", "Suraj", Color(0xFFE65100)) {
                    viewModel.login("8765432109", "123456")
                }
                DemoBadge("Delivery Partner", "Rajesh", Color(0xFF1565C0)) {
                    viewModel.login("7654321098", "123456")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DemoBadge("Admin Panel", "KisanBuy Admin", Color(0xFF4A148C)) {
                    viewModel.login("6543210987", "123456")
                }
                DemoBadge("Super Admin", "Full Controls", Color(0xFFB71C1C)) {
                    viewModel.login("5432109876", "123456")
                }
            }
        }
    }
}

@Composable
fun DemoBadge(label: String, demoUser: String, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = BorderStroke(1.dp, color),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Text("User: $demoUser", fontSize = 9.sp, color = Color.Gray)
        }
    }
}


// ==========================================
// MAIN NAV SCAFFOLD WRAPPER (Role selection)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalScaffold(
    viewModel: KisanViewModel,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val selectedRole by viewModel.selectedRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val screen by viewModel.currentScreen.collectAsState()

    Scaffold(
        topBar = {
            Column {
                // Top Role Switcher Bar (Always active for easy switching)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.kisanbuy_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Switch Portal:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row {
                        val rolesList = listOf("FARMER", "DEALER", "DELIVERY", "ADMIN", "SUPERADMIN")
                        rolesList.forEach { role ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selectedRole == role) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { viewModel.forceSwitchRole(role) }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = role.take(4),
                                    color = if (selectedRole == role) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Standard App bar
                val showBack = screen in listOf("PRODUCT_DETAILS", "ADD_EDIT_PRODUCT", "CHECKOUT", "ORDER_DETAILS", "TRACK_ORDER")
                TopAppBar(
                    title = {
                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        if (showBack) {
                            IconButton(onClick = {
                                if (screen == "PRODUCT_DETAILS" || screen == "ADD_EDIT_PRODUCT") {
                                    if (selectedRole == "DEALER") viewModel.navigateTo("DEALER_DASHBOARD")
                                    else viewModel.navigateTo("HOME")
                                } else if (screen == "CHECKOUT") {
                                    viewModel.navigateTo("CART")
                                } else if (screen == "ORDER_DETAILS" || screen == "TRACK_ORDER") {
                                    viewModel.navigateTo("ORDERS")
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            IconButton(onClick = { viewModel.navigateTo("PROFILE") }) {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                            }
                        }
                    },
                    actions = {
                        // Cart icon for Farmer app
                        if (selectedRole == "FARMER") {
                            IconButton(onClick = { viewModel.navigateTo("CART") }) {
                                BadgedBox(badge = {
                                    if (cart.isNotEmpty()) {
                                        Badge { Text(cart.values.sum().toString()) }
                                    }
                                }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                                }
                            }
                        }
                        IconButton(onClick = { viewModel.logout() }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Log out")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (selectedRole == "FARMER" && screen in listOf("HOME", "CART", "ORDERS", "WEATHER_MANDI", "SCHEMES", "SUPPORT", "PROFILE")) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = screen == "HOME",
                        onClick = { viewModel.navigateTo("HOME") },
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = screen == "WEATHER_MANDI",
                        onClick = { viewModel.navigateTo("WEATHER_MANDI") },
                        icon = { Icon(Icons.Default.Cloud, "Weather") },
                        label = { Text("Weather") }
                    )
                    NavigationBarItem(
                        selected = screen == "SCHEMES",
                        onClick = { viewModel.navigateTo("SCHEMES") },
                        icon = { Icon(Icons.Default.Assignment, "Schemes") },
                        label = { Text("Schemes") }
                    )
                    NavigationBarItem(
                        selected = screen == "ORDERS",
                        onClick = { viewModel.navigateTo("ORDERS") },
                        icon = { Icon(Icons.Default.List, "Orders") },
                        label = { Text("Orders") }
                    )
                    NavigationBarItem(
                        selected = screen == "PROFILE",
                        onClick = { viewModel.navigateTo("PROFILE") },
                        icon = { Icon(Icons.Default.Person, "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        },
        content = content
    )
}


// ==========================================
// FARMER & BUYER PORTAL (HOME)
// ==========================================
@Composable
fun FarmerHomeScreen(viewModel: KisanViewModel) {
    val products by viewModel.allProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val weather by viewModel.weatherState.collectAsState()
    val village by viewModel.selectedVillage.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()

    val categories = listOf("All", "Seeds", "Fertilizers", "Pesticides", "Farm Tools", "Tractors", "Used Machinery", "Irrigation", "Dairy", "Organic", "Safety")

    val filteredProducts = products.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        matchesSearch && matchesCategory && it.approvalState == "APPROVED" && it.status == "ACTIVE"
    }

    PortalScaffold(viewModel = viewModel, title = "KisanBuy Marketplace") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar & Weather Quick Widget
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search Seeds, Fertilizers, Tractors...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    modifier = Modifier.fillMaxWidth().testTag("product_search_bar"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick weather & Mandi ticker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .clickable { viewModel.navigateTo("WEATHER_MANDI") }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cloud, "Weather", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("${village.name} Weather", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = if (weather != null) "${weather!!.temperature}°C | ${weather!!.forecast.firstOrNull()?.condition ?: "Sunny"}" else "Loading weather...",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.secondary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Mandi Rates", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ChevronRight, "View")
                    }
                }
            }

            // Categories horizontal scroll list
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat) },
                        modifier = Modifier.testTag("cat_chip_$cat")
                    )
                }
            }

            // Products grid (Using LazyColumn with rows of 2 items)
            if (filteredProducts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Search, "No items", modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No products found matching filters.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Try checking other categories or clearing your search.", fontSize = 12.sp, color = Color.LightGray, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Split filtered products into chunks of 2 for grid layout
                    val chunked = filteredProducts.chunked(2)
                    items(chunked) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { prod ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCard(prod, isSaved = wishlist.contains(prod.id),
                                        onWishlistToggle = { viewModel.toggleWishlist(prod.id) },
                                        onClick = { viewModel.navigateTo("PRODUCT_DETAILS", prod.id) }
                                    )
                                }
                            }
                            // Fill blank space if last row has only 1 item
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, isSaved: Boolean, onWishlistToggle: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                // Product Category Icon placeholder with visual depth
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val icon = when (product.category) {
                        "Seeds" -> Icons.Default.Spa
                        "Fertilizers" -> Icons.Default.Eco
                        "Pesticides" -> Icons.Default.BugReport
                        "Farm Tools" -> Icons.Default.Build
                        "Tractors" -> Icons.Default.Agriculture
                        "Used Machinery" -> Icons.Default.Build
                        "Irrigation" -> Icons.Default.Water
                        "Dairy" -> Icons.Default.Water
                        "Organic" -> Icons.Default.Spa
                        "Safety" -> Icons.Default.Info
                        else -> Icons.Default.ShoppingCart
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(product.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }

                // Wishlist Icon overlay
                IconButton(
                    onClick = onWishlistToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .testTag("wishlist_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) Color.Red else Color.Gray
                    )
                }

                if (product.stock <= 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Red)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("OUT OF STOCK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.discountPrice > 0) {
                        Text(
                            text = "Rs ${product.discountPrice.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rs ${product.price.toInt()}",
                            style = MaterialTheme.typography.bodySmall.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = "Rs ${product.price.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stock status
                val stockText = if (product.stock <= 5 && product.stock > 0) "Only ${product.stock} left!" else "In Stock (${product.stock})"
                val stockColor = if (product.stock <= 5) Color.Red else Color.Gray
                Text(stockText, fontSize = 10.sp, color = stockColor, fontWeight = FontWeight.Medium)
            }
        }
    }
}


// ==========================================
// PRODUCT DETAILS SCREEN
// ==========================================
@Composable
fun ProductDetailsScreen(viewModel: KisanViewModel) {
    val selectedId by viewModel.selectedProductId.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val product = products.find { it.id == selectedId }
    val wishlist by viewModel.wishlist.collectAsState()

    PortalScaffold(viewModel = viewModel, title = "Product Details") { paddingValues ->
        if (product == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Product details not found.")
            }
        } else {
            val hasDiscount = product.discountPrice > 0
            val activePrice = if (hasDiscount) product.discountPrice else product.price

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header image box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (product.category) {
                        "Seeds" -> Icons.Default.Spa
                        "Fertilizers" -> Icons.Default.Eco
                        "Pesticides" -> Icons.Default.BugReport
                        "Farm Tools" -> Icons.Default.Build
                        "Tractors" -> Icons.Default.Agriculture
                        "Used Machinery" -> Icons.Default.Build
                        "Irrigation" -> Icons.Default.Water
                        "Dairy" -> Icons.Default.Water
                        "Organic" -> Icons.Default.Spa
                        "Safety" -> Icons.Default.Info
                        else -> Icons.Default.ShoppingCart
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(product.category, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }

                        IconButton(
                            onClick = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.testTag("wishlist_btn_details")
                        ) {
                            Icon(
                                imageVector = if (wishlist.contains(product.id)) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (wishlist.contains(product.id)) Color.Red else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("SKU: ${product.sku}", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rs ${activePrice.toInt()}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (hasDiscount) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Rs ${product.price.toInt()}",
                                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            val pct = ((product.price - product.discountPrice) / product.price * 100).toInt()
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFE0B2))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("$pct% OFF", color = Color(0xFFE65100), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Farming Specifications", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("In Stock Inventory", color = Color.Gray)
                                Text("${product.stock} Bags / Pieces", fontWeight = FontWeight.Bold)
                            }
                            if (product.specifications.isNotBlank()) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                product.specifications.split(";").forEach { spec ->
                                    val parts = spec.split(":")
                                    if (parts.size == 2) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(parts[0].trim(), color = Color.Gray)
                                            Text(parts[1].trim(), fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text(spec, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Product Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        product.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.addToCart(product.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("add_to_cart_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = product.stock > 0
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Cart")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (product.stock > 0) "Add to Shopping Cart" else "OUT OF STOCK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}


// ==========================================
// SHOPPING CART & TRANSPARENT CHECKOUT
// ==========================================
@Composable
fun CartScreen(viewModel: KisanViewModel) {
    val cartItems by viewModel.cartItems.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val summary = viewModel.getCartSummary()

    PortalScaffold(viewModel = viewModel, title = "My Shopping Cart") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (cartItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.ShoppingCart, "Empty", modifier = Modifier.size(80.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your cart is empty!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Explore our seeds and fertilizers list to add items.", fontSize = 13.sp, color = Color.LightGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.navigateTo("HOME") }) {
                        Text("Browse Marketplace")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val cartList = cartItems.toList()
                    items(cartList) { (prodId, qty) ->
                        val p = products.find { it.id == prodId }
                        if (p != null) {
                            CartItemRow(p, qty,
                                onQtyChange = { newQty -> viewModel.updateCartQuantity(prodId, newQty) },
                                onRemove = { viewModel.removeFromCart(prodId) }
                            )
                        }
                    }
                }

                // Bottom Checkout summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = Color.Gray)
                            Text("Rs ${summary.subtotal.toInt()}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee", color = Color.Gray)
                            Text(if (summary.delivery == 0.0) "FREE" else "Rs ${summary.delivery.toInt()}", fontWeight = FontWeight.Bold, color = if (summary.delivery == 0.0) MaterialTheme.colorScheme.primary else Color.Black)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GST Taxes (5%)", color = Color.Gray)
                            Text("Rs ${summary.tax.toInt()}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Platform Service Fee", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Info, "Info", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                            }
                            Text("Rs ${summary.platformFee.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Rs ${summary.total.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.navigateTo("CHECKOUT") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("checkout_navigate_button")
                        ) {
                            Text("Proceed to Safe Checkout", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(product: Product, qty: Int, onQtyChange: (Int) -> Unit, onRemove: () -> Unit) {
    val price = if (product.discountPrice > 0) product.discountPrice else product.price
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, "Item", tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Category: ${product.category}", fontSize = 11.sp, color = Color.Gray)
                Text("Rs ${price.toInt()} / unit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            // Quantity adjust and delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onQtyChange(qty - 1) }) {
                    Icon(Icons.Default.RemoveCircleOutline, "Minus")
                }
                Text(qty.toString(), fontWeight = FontWeight.Bold)
                IconButton(onClick = { onQtyChange(qty + 1) }) {
                    Icon(Icons.Default.AddCircleOutline, "Add")
                }
                IconButton(onClick = onRemove, modifier = Modifier.testTag("remove_cart_item_${product.id}")) {
                    Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun CheckoutScreen(viewModel: KisanViewModel) {
    val summary = viewModel.getCartSummary()
    var addressInput by remember { mutableStateOf("") }
    val currentUser by viewModel.currentUser.collectAsState()

    // Prepopulate user address if available
    LaunchedEffect(currentUser) {
        currentUser?.let {
            if (it.businessAddress.isNotBlank()) {
                addressInput = it.businessAddress
            }
        }
    }

    PortalScaffold(viewModel = viewModel, title = "Secure Payment & Fee Transparency") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Text("Shipping & Delivery Address", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = addressInput,
                onValueChange = { addressInput = it },
                placeholder = { Text("Enter your village name, post office, district, and pincode") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("shipping_address_input"),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Secure Payment Gateway details
            Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = true, onClick = {})
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Cash on Delivery (COD) / Pay on Delivery", fontWeight = FontWeight.Bold)
                        Text("No transaction fee. Pay to our delivery partner in cash or UPI upon delivery.", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Transparent Audit breakdown
            Text("Platform Audit & Price Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("As per KisanBuy Fair Pricing rules, all charges are fully audited and visible prior to payment. No hidden commissions are charged to dealers or farmers.", fontSize = 11.sp, color = Color.Gray)
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Products Subtotal", color = Color.Gray)
                        Text("Rs ${summary.subtotal.toInt()}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Local Delivery Charge", color = Color.Gray)
                        Text("Rs ${summary.delivery.toInt()}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST & Agri Cess (5%)", color = Color.Gray)
                        Text("Rs ${summary.tax.toInt()}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Platform Maintenance Fee", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, "Verified", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        Text("Rs ${summary.platformFee.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Transparent Total Price", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Rs ${summary.total.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.executeCheckout(addressInput) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("place_order_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.CheckCircle, "Confirm")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm & Place Order (Rs ${summary.total.toInt()})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}


// ==========================================
// FARMER & BUYER PORTAL (ORDERS LIST & BILLING)
// ==========================================
@Composable
fun OrdersScreen(viewModel: KisanViewModel) {
    val orders by viewModel.allOrders.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Filter orders specifically for the logged-in buyer
    val buyerOrders = orders.filter { it.buyerId == (currentUser?.id ?: 0) }

    PortalScaffold(viewModel = viewModel, title = "My Booking & Orders") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (buyerOrders.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Receipt, "No orders", modifier = Modifier.size(80.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No orders placed yet!", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("When you buy seeds, tools or fertilizers, they will appear here with dynamic delivery tracking.", fontSize = 13.sp, color = Color.LightGray, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(buyerOrders) { order ->
                        BuyerOrderCard(order, onTrackClick = { viewModel.navigateTo("TRACK_ORDER", order.id) }, onReturnClick = { viewModel.requestReturnAndRefund(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun BuyerOrderCard(order: Order, onTrackClick: () -> Unit, onReturnClick: () -> Unit) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
    val statusColor = when (order.status) {
        "PLACED" -> Color(0xFF1565C0)
        "ACCEPTED" -> Color(0xFF5E35B1)
        "PACKED" -> Color(0xFFEF6C00)
        "SHIPPED" -> Color(0xFFF9A825)
        "DELIVERED" -> Color(0xFF2E7D32)
        "RETURNED", "REFUNDED" -> Color(0xFFC62828)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(dateStr, fontSize = 11.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(order.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Payment Amount", fontSize = 13.sp, color = Color.Gray)
                Text("Rs ${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Text("Included Platform Fee: Rs ${order.platformFee.toInt()}", fontSize = 10.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (order.status == "DELIVERED") {
                    OutlinedButton(
                        onClick = onReturnClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Return & Refund")
                    }
                }
                Button(onClick = onTrackClick) {
                    Text("Track Shipment / Bill")
                }
            }
        }
    }
}

@Composable
fun OrderTrackingScreen(viewModel: KisanViewModel) {
    val selectedId by viewModel.selectedOrderId.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val order = orders.find { it.id == selectedId }

    PortalScaffold(viewModel = viewModel, title = "Order Billing & Tracking") { paddingValues ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order details not found.")
            }
        } else {
            val dateStr = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(order.createdAt))
            val currentStep = when (order.status) {
                "PLACED" -> 0
                "ACCEPTED" -> 1
                "PACKED" -> 2
                "SHIPPED" -> 3
                "DELIVERED" -> 4
                else -> 0
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp)
            ) {
                // Tracking Title Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transaction Receipt", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Tx ID: ${order.transactionId}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Order Date: $dateStr", fontSize = 12.sp)
                        Text("Deliver To: ${order.deliveryAddress}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Real-time Live Tracking Map
                val trackingProgress by viewModel.trackingProgress.collectAsState()
                val trackingDistanceKm by viewModel.trackingDistanceKm.collectAsState()
                val trackingEtaMinutes by viewModel.trackingEtaMinutes.collectAsState()
                val isTrackingActive by viewModel.isTrackingActive.collectAsState()

                if (order.status == "SHIPPED" || order.status == "DELIVERED" || isTrackingActive) {
                    Text("Live GPS Delivery Map (Real-time Tracking)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Telemetry status bar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isTrackingActive) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isTrackingActive) Color(0xFF4CAF50) else Color(0xFFFF9800))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTrackingActive) "LIVE SATELLITE GPS ACTIVE" else "GPS TELEMETRY READY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTrackingActive) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom map canvas
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F8E9)) // Light green landscape map background
                                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            ) {
                                val primaryColor = MaterialTheme.colorScheme.primary
                                val secondaryColor = MaterialTheme.colorScheme.secondary
                                
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val width = size.width
                                    val height = size.height

                                    // Draw a winding farming road path
                                    val roadPath = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(50f, height / 2f)
                                        cubicTo(
                                            width * 0.3f, height * 0.2f,
                                            width * 0.6f, height * 0.8f,
                                            width - 50f, height / 2f
                                        )
                                    }

                                    // Draw road background
                                    drawPath(
                                        path = roadPath,
                                        color = Color.LightGray.copy(alpha = 0.5f),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 16f,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    )

                                    // Draw road center line (dashed)
                                    drawPath(
                                        path = roadPath,
                                        color = Color.White,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = 2f,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                                floatArrayOf(10f, 10f), 0f
                                            )
                                        )
                                    )

                                    // Draw Start (Mandi Warehouse)
                                    drawCircle(
                                        color = secondaryColor,
                                        radius = 12f,
                                        center = Offset(50f, height / 2f)
                                    )

                                    // Draw Destination (Your Farm Gate)
                                    drawCircle(
                                        color = primaryColor,
                                        radius = 16f,
                                        center = Offset(width - 50f, height / 2f)
                                    )

                                    // Calculate active tractor position along the cubic path
                                    // A simple cubic interpolation for rendering
                                    val t = trackingProgress
                                    val p0 = Offset(50f, height / 2f)
                                    val p1 = Offset(width * 0.3f, height * 0.2f)
                                    val p2 = Offset(width * 0.6f, height * 0.8f)
                                    val p3 = Offset(width - 50f, height / 2f)

                                    // Cubic Bezier formula: B(t) = (1-t)^3 * P0 + 3(1-t)^2 * t * P1 + 3(1-t) * t^2 * P2 + t^3 * P3
                                    val mt = 1f - t
                                    val x = mt * mt * mt * p0.x + 3f * mt * mt * t * p1.x + 3f * mt * t * t * p2.x + t * t * t * p3.x
                                    val y = mt * mt * mt * p0.y + 3f * mt * mt * t * p1.y + 3f * mt * t * t * p2.y + t * t * t * p3.y

                                    // Draw tractor/truck marker position shadow
                                    drawCircle(
                                        color = Color.Black.copy(alpha = 0.2f),
                                        radius = 14f,
                                        center = Offset(x, y + 4f)
                                    )

                                    // Draw tractor marker (active moving red/primary circle representing driver)
                                    drawCircle(
                                        color = Color.Red,
                                        radius = 10f,
                                        center = Offset(x, y)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 4f,
                                        center = Offset(x, y)
                                    )
                                }

                                // Overlay text labels
                                Text(
                                    "Mandi Hub",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp, top = 40.dp)
                                )
                                Text(
                                    "Your Farm Gate",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 15.dp, top = 40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Stats Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("GPS DISTANCE LEFT", fontSize = 10.sp, color = Color.Gray)
                                    Text(
                                        text = "${String.format("%.2f", trackingDistanceKm)} km",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("LIVE ETA", fontSize = 10.sp, color = Color.Gray)
                                    Text(
                                        text = "$trackingEtaMinutes mins",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("SPEED (EST.)", fontSize = 10.sp, color = Color.Gray)
                                    Text(
                                        text = if (isTrackingActive) "42 km/h" else "0 km/h",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }

                            if (order.status == "SHIPPED" && !isTrackingActive) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.startRealTimeTracking() },
                                    modifier = Modifier.fillMaxWidth().testTag("start_gps_tracking_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.PlayArrow, "Start")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Begin Live GPS Navigation", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Visual Tracking Pipeline Stepper
                Text("Delivery Journey Status", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val steps = listOf("Placed", "Approved", "Packed", "Dispatched", "Delivered")
                steps.forEachIndexed { idx, step ->
                    val isActive = idx <= currentStep
                    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.15f))
                                .border(2.dp, color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (idx < currentStep) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = color)
                            } else {
                                Text((idx + 1).toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                            Text(step, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal, color = if (isActive) Color.Black else Color.Gray)
                            if (idx == currentStep) {
                                Text("Current active stage of your crop inputs", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Invoice Download Summary
                Text("Transparent Invoice & Billing Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Agri-Produce Total Price")
                            Text("Rs ${(order.totalAmount - order.platformFee - order.deliveryCharge - order.tax).toInt()}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Local Transportation Charge")
                            Text("Rs ${order.deliveryCharge.toInt()}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GST Agri Cess & Toll")
                            Text("Rs ${order.tax.toInt()}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Platform Administrative Fee", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                            Text("Rs ${order.platformFee.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Paid Net Bill (COD)", fontWeight = FontWeight.Bold)
                            Text("Rs ${order.totalAmount.toInt()}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { viewModel.showFeedback("Invoice downloaded successfully to '/KisanBuy/Invoices/Order_${order.id}.pdf'!") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, "Download")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download PDF Invoices & Receipt")
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// WEATHER & MANDI RATES INTEGRATION
// ==========================================
@Composable
fun WeatherMandiScreen(viewModel: KisanViewModel) {
    val weather by viewModel.weatherState.collectAsState()
    val village by viewModel.selectedVillage.collectAsState()
    val isWeatherLoading by viewModel.isWeatherLoading.collectAsState()
    val mandiRates by viewModel.allMandiRates.collectAsState()
    val mandiSearchQuery by viewModel.mandiSearchQuery.collectAsState()

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (fineGranted || coarseGranted) {
                viewModel.fetchWeatherForExactLocation(context)
            } else {
                viewModel.showFeedback("Location permission denied. Please enable location permissions in settings.")
            }
        }
    )

    val filteredMandi = mandiRates.filter {
        it.cropName.contains(mandiSearchQuery, ignoreCase = true) ||
        it.state.contains(mandiSearchQuery, ignoreCase = true) ||
        it.district.contains(mandiSearchQuery, ignoreCase = true)
    }

    PortalScaffold(viewModel = viewModel, title = "Weather & Mandi Market") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Weather Card Header
            Text("Weather Forecast - Agriculture Alert", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Active Village Profile", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            Text(village.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        
                        // Dropdown-style selector
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            WeatherService.VILLAGES.forEach { v ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (village.name == v.name) MaterialTheme.colorScheme.primary else Color.White)
                                        .clickable { viewModel.selectVillage(v.name) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(v.name, color = if (village.name == v.name) Color.White else Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // GPS WEATHER FETCH BUTTON
                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("get_gps_weather_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.MyLocation, "My Location")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Get Weather for Exact GPS Location", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isWeatherLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (weather != null) {
                        val w = weather!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Cloud, "Cloud", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("${w.temperature}°C", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(w.forecast.firstOrNull()?.condition ?: "Clear Sky", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Humidity: ${w.humidity}%", fontSize = 11.sp)
                                Text("Wind: ${w.windSpeed} km/h", fontSize = 11.sp)
                                Text("Rain Prob: ${w.rainProbability}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (w.rainProbability > 50) Color.Red else Color.Black)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sunrise: ${w.sunrise}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            Text("Sunset: ${w.sunset}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Horizontal forecast
                        Text("7-Day Agricultural Forecast", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            w.forecast.forEach { day ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(0.5.dp, Color.LightGray),
                                    modifier = Modifier.width(70.dp).padding(vertical = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(day.dayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Icon(
                                            imageVector = when (day.condition) {
                                                "Rainy" -> Icons.Default.WaterDrop
                                                "Partly Cloudy" -> Icons.Default.CloudQueue
                                                else -> Icons.Default.WbSunny
                                            },
                                            contentDescription = null,
                                            tint = if (day.condition == "Rainy") MaterialTheme.colorScheme.primary else Color(0xFFFFD54F),
                                            modifier = Modifier.padding(vertical = 4.dp).size(20.dp)
                                        )
                                        Text("${day.tempMax.toInt()}°", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("${day.tempMin.toInt()}°", fontSize = 9.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mandi Market Rates Table
            Text("Government APMC Mandi Prices (Live)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = mandiSearchQuery,
                onValueChange = { viewModel.updateMandiSearchQuery(it) },
                placeholder = { Text("Search crop (e.g. Wheat, Mustard)...") },
                leadingIcon = { Icon(Icons.Default.Storefront, null) },
                modifier = Modifier.fillMaxWidth().testTag("mandi_search_bar"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredMandi.forEach { rate ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(0.5.dp, Color.LightGray)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(rate.cropName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Rs ${rate.modalPrice.toInt()} / Quintal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Market: ${rate.market} (${rate.district}, ${rate.state})", fontSize = 11.sp, color = Color.Gray)
                                Text("Min: ${rate.minPrice.toInt()} | Max: ${rate.maxPrice.toInt()}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// GOVERNMENT SCHEMES PORTAL
// ==========================================
@Composable
fun SchemesScreen(viewModel: KisanViewModel) {
    val schemes by viewModel.allSchemes.collectAsState()

    PortalScaffold(viewModel = viewModel, title = "Central Government Schemes") { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, "Gov", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Direct Benefit Transfer (DBT)", fontWeight = FontWeight.Bold)
                            Text("All registered schemes are linked with Aadhaar card and verified mobile numbers.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(schemes) { scheme ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(scheme.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(scheme.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text("Eligibility Requirements", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(scheme.eligibility, fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Benefit Breakdown", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(scheme.benefit, fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.showFeedback("Routing safely to portal '${scheme.applicationUrl}' via secure frame.") },
                            modifier = Modifier.fillMaxWidth().testTag("apply_scheme_${scheme.id}")
                        ) {
                            Text("Apply Now Online")
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// USER PROFILE SCREEN (Farmer Details, language, notification center)
// ==========================================
@Composable
fun ProfileScreen(viewModel: KisanViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val allNotifications by viewModel.allNotifications.collectAsState()

    PortalScaffold(viewModel = viewModel, title = "My Profile & Alerts") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            // User Avatar Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountCircle, "Avatar", modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user?.username ?: "Guest Mode", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Role: ${user?.role ?: "FARMER"}", fontSize = 12.sp, color = Color.Gray)
                    Text("Phone: ${user?.phone ?: "9876543210"}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (user?.role == "DEALER") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Dealer Documentation Details", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Business: ${user?.businessName}", fontWeight = FontWeight.Bold)
                        Text("Store Address: ${user?.businessAddress}")
                        Text("Documents: ${user?.businessDocUrl}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (user?.verificationStatus) {
                                        "APPROVED" -> Color.Green.copy(alpha = 0.15f)
                                        "PENDING" -> Color.Yellow.copy(alpha = 0.15f)
                                        else -> Color.Red.copy(alpha = 0.15f)
                                    }
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            val text = when (user?.verificationStatus) {
                                "APPROVED" -> "VERIFIED & APPROVED MERCHANT"
                                "PENDING" -> "VERIFICATION UNDER REVIEW"
                                else -> "REJECTED / SUSPENDED"
                            }
                            Text(text, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = if (user?.verificationStatus == "APPROVED") Color(0xFF1B5E20) else Color.Red)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications center
            Text("Recent Alerts & Announcements (${allNotifications.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (allNotifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                        Text("No notifications received.", color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allNotifications.take(5).forEach { notif ->
                            val icon = when (notif.type) {
                                "WEATHER" -> Icons.Default.Cloud
                                "SCHEME" -> Icons.Default.Verified
                                "ORDER" -> Icons.Default.ShoppingBag
                                else -> Icons.Default.Campaign
                            }
                            val color = when (notif.type) {
                                "WEATHER" -> Color(0xFF1565C0)
                                "SCHEME" -> Color(0xFFE65100)
                                "ORDER" -> Color(0xFF2E7D32)
                                else -> Color.Gray
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(notif.message, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // General configurations
            Text("KisanBuy Application Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo("SUPPORT") }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, "Support", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Open Customer Support Ticket", fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Default.ChevronRight, null)
                    }

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, "Language", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Active Language", fontWeight = FontWeight.Medium)
                        }
                        Text(selectedLanguage, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth().testTag("profile_logout_button")
            ) {
                Text("Sign Out of KisanBuy", fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ==========================================
// DEALER ECOSYSTEM & WORKFLOWS
// ==========================================
@Composable
fun DealerDashboardScreen(viewModel: KisanViewModel) {
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val payments by viewModel.allPayments.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Filter products & orders linked to this dealer
    val dealerProducts = products.filter { it.dealerId == (currentUser?.id ?: 0) }
    
    // We assume dealer handles all orders for this demo simple flow
    val dealerOrders = orders

    PortalScaffold(viewModel = viewModel, title = "Dealer Merchant Dashboard") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Stats summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsCard("Active Products", dealerProducts.size.toString(), Icons.Default.Inventory, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                StatsCard("Pending Shipments", dealerOrders.filter { it.status in listOf("PLACED", "ACCEPTED", "PACKED") }.size.toString(), Icons.Default.PendingActions, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Calculate total sales
                val totalRevenue = payments.sumOf { it.sellerEarnings }
                StatsCard("Merchant Earnings", "Rs ${totalRevenue.toInt()}", Icons.Default.CurrencyRupee, Color(0xFF2E7D32), Modifier.weight(1f))
                
                // Low stock alerts count
                val lowStockCount = dealerProducts.filter { it.stock <= 5 }.size
                StatsCard("Low Stock Alerts", lowStockCount.toString(), Icons.Default.Warning, if (lowStockCount > 0) Color.Red else Color.Gray, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Agricultural Inventory", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Button(
                    onClick = { viewModel.navigateTo("ADD_EDIT_PRODUCT") },
                    modifier = Modifier.testTag("add_product_button")
                ) {
                    Icon(Icons.Default.Add, "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Product")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Inventory List
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                if (dealerProducts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No products in your catalog yet. Click Add Product.", color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        dealerProducts.forEach { prod ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("SKU: ${prod.sku} | Cat: ${prod.category}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Price: Rs ${prod.price.toInt()} | Stock: ${prod.stock}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }

                                Row {
                                    IconButton(
                                        onClick = { viewModel.navigateTo("ADD_EDIT_PRODUCT", prod.id) },
                                        modifier = Modifier.testTag("edit_product_${prod.id}")
                                    ) {
                                        Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteDealerProduct(prod) },
                                        modifier = Modifier.testTag("delete_product_${prod.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Orders list to fulfill
            Text("Pending Merchant Orders (${dealerOrders.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                dealerOrders.take(5).forEach { order ->
                    DealerOrderRow(order,
                        onAccept = { viewModel.updateOrderStatus(order.id, "ACCEPTED") },
                        onPack = { viewModel.updateOrderStatus(order.id, "PACKED") },
                        onShip = { viewModel.updateOrderStatus(order.id, "SHIPPED") }
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DealerOrderRow(order: Order, onAccept: () -> Unit, onPack: () -> Unit, onShip: () -> Unit) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
    val statusColor = when (order.status) {
        "PLACED" -> Color(0xFF1565C0)
        "ACCEPTED" -> Color(0xFF5E35B1)
        "PACKED" -> Color(0xFFEF6C00)
        "SHIPPED" -> Color(0xFFF9A825)
        "DELIVERED" -> Color(0xFF2E7D32)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Order #${order.id}", fontWeight = FontWeight.Bold)
                    Text("Date: $dateStr", fontSize = 10.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(order.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Ship To: ${order.deliveryAddress}", fontSize = 11.sp)
            Text("Total Amount: Rs ${order.totalAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when (order.status) {
                    "PLACED" -> {
                        Button(onClick = onAccept, modifier = Modifier.testTag("accept_order_${order.id}")) {
                            Text("Accept Order")
                        }
                    }
                    "ACCEPTED" -> {
                        Button(onClick = onPack, modifier = Modifier.testTag("pack_order_${order.id}")) {
                            Text("Pack Crop Inputs")
                        }
                    }
                    "PACKED" -> {
                        Button(onClick = onShip, modifier = Modifier.testTag("ship_order_${order.id}")) {
                            Text("Dispatch / Ship")
                        }
                    }
                    else -> {
                        Text("Fulfillment completed", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Add / Edit Product Screen
@Composable
fun AddEditProductScreen(viewModel: KisanViewModel) {
    val editProd by viewModel.editingProduct.collectAsState()

    var sku by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Seeds") }
    var price by remember { mutableStateOf("") }
    var discountPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var specs by remember { mutableStateOf("") }

    val categoriesList = listOf("Seeds", "Fertilizers", "Pesticides", "Farm Tools", "Tractors", "Used Machinery", "Irrigation", "Dairy", "Organic", "Safety")

    LaunchedEffect(editProd) {
        editProd?.let {
            sku = it.sku
            name = it.name
            category = it.category
            price = it.price.toString()
            discountPrice = if (it.discountPrice > 0) it.discountPrice.toString() else ""
            stock = it.stock.toString()
            desc = it.description
            specs = it.specifications
        }
    }

    PortalScaffold(viewModel = viewModel, title = if (editProd != null) "Edit Product" else "Add New Produce / Input") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = sku,
                onValueChange = { sku = it },
                label = { Text("Unique SKU Code") },
                modifier = Modifier.fillMaxWidth().testTag("add_product_sku"),
                singleLine = true
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product / Input Name") },
                modifier = Modifier.fillMaxWidth().testTag("add_product_name"),
                singleLine = true
            )

            // Category select row
            Text("Select Category", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                categoriesList.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat) },
                        modifier = Modifier.padding(horizontal = 4.dp).testTag("select_cat_$cat")
                    )
                }
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Standard Price (Rs)") },
                modifier = Modifier.fillMaxWidth().testTag("add_product_price"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = discountPrice,
                onValueChange = { discountPrice = it },
                label = { Text("Agri-Discount Price (Rs) (Optional)") },
                modifier = Modifier.fillMaxWidth().testTag("add_product_discount"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Available Stock quantity") },
                modifier = Modifier.fillMaxWidth().testTag("add_product_stock"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Detailed Product Description") },
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("add_product_desc"),
                maxLines = 4
            )

            OutlinedTextField(
                value = specs,
                onValueChange = { specs = it },
                label = { Text("Specifications (key:value ; key:value)") },
                placeholder = { Text("e.g. Power: 50 HP ; Hours: 1200") },
                modifier = Modifier.fillMaxWidth().testTag("add_product_specs"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val pDouble = price.toDoubleOrNull() ?: 0.0
                    val dDouble = discountPrice.toDoubleOrNull() ?: 0.0
                    val sInt = stock.toIntOrNull() ?: 0
                    viewModel.saveProduct(name, category, pDouble, dDouble, sInt, desc, sku, specs)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("add_product_save_btn")
            ) {
                Text("Save and Publish Product", fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ==========================================
// DELIVERY PARTNER PORTAL
// ==========================================
@Composable
fun DeliveryDashboardScreen(viewModel: KisanViewModel) {
    val orders by viewModel.allOrders.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Active assigned orders
    val assignedOrders = orders.filter { it.deliveryPartnerId == (currentUser?.id ?: 0) }

    PortalScaffold(viewModel = viewModel, title = "Delivery Logistics Panel") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Header stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, "Truck", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Active Logistics Driver", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(currentUser?.username ?: "Rajesh Delivery", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Status: Online & Ready", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("My Assigned Shipments (${assignedOrders.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (assignedOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    Text("No shipments currently assigned to you.", color = Color.Gray)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    assignedOrders.forEach { order ->
                        DeliveryShipmentCard(order,
                            onStatusUpdate = { next -> viewModel.updateOrderStatus(order.id, next) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryShipmentCard(order: Order, onStatusUpdate: (String) -> Unit) {
    var showProofDialog by remember { mutableStateOf(false) }
    var proofNotes by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Order ID: #${order.id}", fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(order.status, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Buyer: ${order.buyerName} (${order.buyerPhone})", fontSize = 12.sp)
            Text("Destination: ${order.deliveryAddress}", fontSize = 12.sp, color = Color.Gray)
            Text("Collection Amount (COD): Rs ${order.totalAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

            Spacer(modifier = Modifier.height(12.dp))

            // Simulated GPS Map tracking
            Text("Simulated Routing: Driver -> Hub -> 12km to Village Gate", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                when (order.status) {
                    "SHIPPED" -> {
                        Button(onClick = { onStatusUpdate("OUT_FOR_DELIVERY") }, modifier = Modifier.weight(1f).testTag("out_for_delivery_${order.id}")) {
                            Text("Set Out for Delivery")
                        }
                    }
                    "OUT_FOR_DELIVERY" -> {
                        Button(onClick = { showProofDialog = true }, modifier = Modifier.weight(1f).testTag("delivered_${order.id}")) {
                            Text("Record Delivery Proof")
                        }
                    }
                    "DELIVERED" -> {
                        Text("Delivered & Collected", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    else -> {
                        Text("Fulfillment is in processing at Merchant store.", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }
    }

    if (showProofDialog) {
        AlertDialog(
            onDismissRequest = { showProofDialog = false },
            title = { Text("Delivery Proof Verification") },
            text = {
                Column {
                    Text("Enter proof details (e.g. Received by Ramesh Kumar, OTP verified):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = proofNotes,
                        onValueChange = { proofNotes = it },
                        placeholder = { Text("Proof notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onStatusUpdate("DELIVERED")
                    showProofDialog = false
                }) {
                    Text("Save Proof & Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProofDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


// ==========================================
// CENTRAL ADMIN & SUPER ADMIN ARCHITECTURE
// ==========================================
@Composable
fun AdminDashboardScreen(viewModel: KisanViewModel) {
    val products by viewModel.allProducts.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val tickets by viewModel.allTickets.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val auditLogs by viewModel.allAuditLogs.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()

    var percentFeeInput by remember { mutableStateOf(appSettings.platformFeePercent.toString()) }
    var fixedFeeInput by remember { mutableStateOf(appSettings.platformFeeFixed.toString()) }

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMsg by remember { mutableStateOf("") }

    var selectedTicketReplyText by remember { mutableStateOf("") }
    var replyingTicketId by remember { mutableStateOf<Long?>(null) }

    val pendingDealers = users.filter { it.role == "DEALER" && it.verificationStatus == "PENDING" }

    PortalScaffold(viewModel = viewModel, title = "Agri-Market Management Console") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System health banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Green))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Server Status: HEALTHY & ONLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Row {
                        Text("Mandi API: ", fontSize = 10.sp)
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (appSettings.isMandiServiceActive) Color.Green else Color.Red))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Weather API: ", fontSize = 10.sp)
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (appSettings.isWeatherServiceActive) Color.Green else Color.Red))
                    }
                }
            }

            // Stats grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatsCard("Total Platform Users", users.size.toString(), Icons.Default.People, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                StatsCard("Agri Dealers Approved", users.filter { it.role == "DEALER" && it.verificationStatus == "APPROVED" }.size.toString(), Icons.Default.Store, MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatsCard("Active Products", products.filter { it.approvalState == "APPROVED" }.size.toString(), Icons.Default.Grass, Color(0xFF2E7D32), Modifier.weight(1f))
                StatsCard("Total Orders Today", orders.size.toString(), Icons.Default.ReceiptLong, Color(0xFF1565C0), Modifier.weight(1f))
            }

            Divider()

            // Platform Commission control
            Text("Platform Commission Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Configure the platform service charges calculated during checkout dynamically.", fontSize = 11.sp, color = Color.Gray)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = percentFeeInput,
                            onValueChange = { percentFeeInput = it },
                            label = { Text("Percentage Fee (%)") },
                            modifier = Modifier.weight(1f).testTag("platform_percent_fee"),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fixedFeeInput,
                            onValueChange = { fixedFeeInput = it },
                            label = { Text("Fixed Flat Fee (Rs)") },
                            modifier = Modifier.weight(1f).testTag("platform_fixed_fee"),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            val pct = percentFeeInput.toDoubleOrNull() ?: 5.0
                            val fix = fixedFeeInput.toDoubleOrNull() ?: 10.0
                            viewModel.updatePlatformCommission(pct, fix)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_platform_fee_button")
                    ) {
                        Text("Apply Commission Parameters")
                    }
                }
            }

            // API Service Toggles
            Text("Service Integrations & Failover Switches", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Mandi API Rates Integration Active")
                        Switch(
                            checked = appSettings.isMandiServiceActive,
                            onCheckedChange = { viewModel.updateServiceStatus(it, appSettings.isWeatherServiceActive) },
                            modifier = Modifier.testTag("mandi_service_toggle")
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Open-Meteo Weather Integration Active")
                        Switch(
                            checked = appSettings.isWeatherServiceActive,
                            onCheckedChange = { viewModel.updateServiceStatus(appSettings.isMandiServiceActive, it) },
                            modifier = Modifier.testTag("weather_service_toggle")
                        )
                    }
                }
            }

            // Pending Dealer verification
            Text("Pending Dealer Document Approvals (${pendingDealers.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(modifier = Modifier.fillMaxWidth()) {
                if (pendingDealers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        Text("No pending merchant requests.", color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pendingDealers.forEach { dealer ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Yellow.copy(alpha = 0.05f))
                                    .padding(8.dp)
                            ) {
                                Text(dealer.username, fontWeight = FontWeight.Bold)
                                Text("Store: ${dealer.businessName} | doc: ${dealer.businessDocUrl}", fontSize = 11.sp, color = Color.Gray)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    OutlinedButton(
                                        onClick = { viewModel.verifyDealer(dealer.id, false) },
                                        modifier = Modifier.padding(end = 8.dp).testTag("reject_dealer_${dealer.id}"),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                    ) {
                                        Text("Reject")
                                    }
                                    Button(
                                        onClick = { viewModel.verifyDealer(dealer.id, true) },
                                        modifier = Modifier.testTag("approve_dealer_${dealer.id}")
                                    ) {
                                        Text("Verify & Approve")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Customer Support Ticket Resolver
            Text("Customer Support Center - Live Tickets", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(modifier = Modifier.fillMaxWidth()) {
                val openTickets = tickets.filter { it.status == "OPEN" }
                if (openTickets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        Text("All tickets resolved. Clean queue!", color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        openTickets.forEach { ticket ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(ticket.subject, fontWeight = FontWeight.Bold)
                                    Text(ticket.priority, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Text("User: ${ticket.username} (${ticket.userRole})", fontSize = 11.sp, color = Color.Gray)
                                Text(ticket.message, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                                
                                if (replyingTicketId == ticket.id) {
                                    OutlinedTextField(
                                        value = selectedTicketReplyText,
                                        onValueChange = { selectedTicketReplyText = it },
                                        label = { Text("Response Text") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { replyingTicketId = null }) { Text("Cancel") }
                                        Button(onClick = {
                                            viewModel.answerSupportTicket(ticket.id, selectedTicketReplyText)
                                            replyingTicketId = null
                                            selectedTicketReplyText = ""
                                        }) { Text("Send Response") }
                                    }
                                } else {
                                    Button(
                                        onClick = { replyingTicketId = ticket.id },
                                        modifier = Modifier.align(Alignment.End).testTag("reply_ticket_${ticket.id}")
                                    ) {
                                        Text("Answer Ticket")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // System Alerts broadcaster
            Text("Broadcast In-App Notification Center", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Announcement Heading") },
                        modifier = Modifier.fillMaxWidth().testTag("broadcast_title"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        label = { Text("Details & Description Body") },
                        modifier = Modifier.fillMaxWidth().height(80.dp).testTag("broadcast_msg")
                    )
                    Button(
                        onClick = {
                            viewModel.broadcastAnnouncement(broadcastTitle, broadcastMsg)
                            broadcastTitle = ""
                            broadcastMsg = ""
                        },
                        modifier = Modifier.fillMaxWidth().testTag("send_broadcast_button")
                    ) {
                        Text("Broadcast Live to All Devices")
                    }
                }
            }

            // Super Admin Controls (Database & Security Audit logs)
            if (selectedRole == "SUPERADMIN") {
                Text("SUPER ADMIN CONSOLE (Database / Safety)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Red)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.05f)),
                    border = BorderStroke(1.5.dp, Color.Red)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick = { viewModel.showFeedback("Database local scheduled backup executed successfully to KisanBuy folder!") },
                                modifier = Modifier.weight(1f).testTag("db_backup_btn")
                            ) {
                                Text("Trigger Backup", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.showFeedback("Database restore point initialized!") },
                                modifier = Modifier.weight(1f).testTag("db_restore_btn")
                            ) {
                                Text("Restore Point", fontSize = 11.sp)
                            }
                        }

                        Divider()

                        // Audit Trail list
                        Text("Immutable Platform Audit Logs (Live)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            auditLogs.take(8).forEach { log ->
                                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("[$timeStr] ${log.username} -> ${log.action}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(log.details, fontSize = 9.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==========================================
// FARMER CUSTOMER SUPPORT CENTER (Submissions)
// ==========================================
@Composable
fun CustomerSupportScreen(viewModel: KisanViewModel) {
    var subjectInput by remember { mutableStateOf("") }
    var msgInput by remember { mutableStateOf("") }
    var priorityInput by remember { mutableStateOf("MEDIUM") }

    val userTickets by viewModel.allTickets.collectAsState()

    PortalScaffold(viewModel = viewModel, title = "Farmer Support & Live Chat") { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Open Helpdesk Support Ticket", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Having trouble with crop inputs, orders, or mandi rates? Report to our agricultural specialists.", fontSize = 12.sp, color = Color.Gray)

            OutlinedTextField(
                value = subjectInput,
                onValueChange = { subjectInput = it },
                label = { Text("What is the issue about?") },
                placeholder = { Text("e.g. Seed order delay") },
                modifier = Modifier.fillMaxWidth().testTag("ticket_subject"),
                singleLine = true
            )

            OutlinedTextField(
                value = msgInput,
                onValueChange = { msgInput = it },
                label = { Text("Explain your issue in detail") },
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("ticket_message"),
                maxLines = 4
            )

            // Priority Chip Selection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Priority: ", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                    FilterChip(
                        selected = priorityInput == p,
                        onClick = { priorityInput = p },
                        label = { Text(p) },
                        modifier = Modifier.padding(horizontal = 4.dp).testTag("priority_chip_$p")
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.submitSupportTicket(subjectInput, msgInput, priorityInput)
                    subjectInput = ""
                    msgInput = ""
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_ticket_btn")
            ) {
                Text("Submit Ticket to Helpdesk", fontWeight = FontWeight.Bold)
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // My Tickets queue list
            Text("My Historic Tickets", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                userTickets.forEach { t ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(t.subject, fontWeight = FontWeight.Bold)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (t.status) {
                                                "OPEN" -> Color.Red.copy(alpha = 0.1f)
                                                "IN_PROGRESS" -> Color.Yellow.copy(alpha = 0.1f)
                                                else -> Color.Green.copy(alpha = 0.1f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(t.status, fontWeight = FontWeight.Bold, fontSize = 9.sp, color = if (t.status == "RESOLVED") Color(0xFF1B5E20) else Color.Red)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(t.message, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
