package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.KisanDatabase
import com.example.data.KisanRepository
import com.example.ui.KisanViewModel
import com.example.ui.KisanViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge
        enableEdgeToEdge()

        // Initialize Database & Repository
        val database = KisanDatabase.getDatabase(applicationContext)
        val repository = KisanRepository(database.kisanDao())

        // Initialize ViewModel
        val viewModelFactory = KisanViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[KisanViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val screen by viewModel.currentScreen.collectAsState()
                val feedbackMsg by viewModel.feedbackMessage.collectAsState()

                // Feedback observer to show Native Toasts
                LaunchedEffect(feedbackMsg) {
                    feedbackMsg?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
                        viewModel.clearFeedback()
                    }
                }

                // Main Routing Controller
                Box(modifier = Modifier.fillMaxSize()) {
                    when (screen) {
                        "ONBOARDING" -> OnboardingScreen(viewModel)
                        "HOME" -> FarmerHomeScreen(viewModel)
                        "PRODUCT_DETAILS" -> ProductDetailsScreen(viewModel)
                        "CART" -> CartScreen(viewModel)
                        "CHECKOUT" -> CheckoutScreen(viewModel)
                        "ORDERS" -> OrdersScreen(viewModel)
                        "TRACK_ORDER" -> OrderTrackingScreen(viewModel)
                        "WEATHER_MANDI" -> WeatherMandiScreen(viewModel)
                        "SCHEMES" -> SchemesScreen(viewModel)
                        "PROFILE" -> ProfileScreen(viewModel)
                        "DEALER_DASHBOARD" -> DealerDashboardScreen(viewModel)
                        "ADD_EDIT_PRODUCT" -> AddEditProductScreen(viewModel)
                        "DELIVERY_DASHBOARD" -> DeliveryDashboardScreen(viewModel)
                        "ADMIN_DASHBOARD", "SUPERADMIN_DASHBOARD" -> AdminDashboardScreen(viewModel)
                        "SUPPORT" -> CustomerSupportScreen(viewModel)
                        else -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
