package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CustomerViewModel
import com.example.data.Screen
import com.example.ui.AutoNotifScreen
import com.example.ui.CustomerDetailScreen
import com.example.ui.CustomerListScreen
import com.example.ui.DashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: CustomerViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Styled Premium Bottom Navigation Bar
                        NavigationBar(
                            containerColor = com.example.ui.theme.RicePaperCream,
                            modifier = Modifier
                                .testTag("bottom_nav_bar")
                                .border(width = (0.5).dp, color = com.example.ui.theme.PaperBorder)
                        ) {
                            NavigationBarItem(
                                selected = currentScreen is Screen.Dashboard,
                                onClick = { viewModel.navigateTo(Screen.Dashboard) },
                                icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "대시보드") },
                                label = { Text("대시보드", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = com.example.ui.theme.TraditionalSlateBlack,
                                    selectedTextColor = com.example.ui.theme.TraditionalSlateBlack,
                                    indicatorColor = com.example.ui.theme.TraditionalGoldAcc.copy(alpha = 0.3f),
                                    unselectedIconColor = com.example.ui.theme.MutedWarmSand,
                                    unselectedTextColor = com.example.ui.theme.MutedWarmSand
                                ),
                                modifier = Modifier.testTag("nav_dashboard")
                            )
                            NavigationBarItem(
                                selected = currentScreen is Screen.CustomerList || currentScreen is Screen.CustomerDetail,
                                onClick = { viewModel.navigateTo(Screen.CustomerList) },
                                icon = { Icon(imageVector = Icons.Default.Assignment, contentDescription = "고객대장") },
                                label = { Text("고객대장", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = com.example.ui.theme.TraditionalSlateBlack,
                                    selectedTextColor = com.example.ui.theme.TraditionalSlateBlack,
                                    indicatorColor = com.example.ui.theme.TraditionalGoldAcc.copy(alpha = 0.3f),
                                    unselectedIconColor = com.example.ui.theme.MutedWarmSand,
                                    unselectedTextColor = com.example.ui.theme.MutedWarmSand
                                ),
                                modifier = Modifier.testTag("nav_customer_list")
                            )
                            NavigationBarItem(
                                selected = currentScreen is Screen.AutoNotifications,
                                onClick = { viewModel.navigateTo(Screen.AutoNotifications) },
                                icon = { Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = "자동알림") },
                                label = { Text("자동알림", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = com.example.ui.theme.TraditionalSlateBlack,
                                    selectedTextColor = com.example.ui.theme.TraditionalSlateBlack,
                                    indicatorColor = com.example.ui.theme.TraditionalGoldAcc.copy(alpha = 0.3f),
                                    unselectedIconColor = com.example.ui.theme.MutedWarmSand,
                                    unselectedTextColor = com.example.ui.theme.MutedWarmSand
                                ),
                                modifier = Modifier.testTag("nav_alerts")
                            )
                        }
                    }
                ) { innerPadding ->
                    // Main Screen Router
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (val screen = currentScreen) {
                            is Screen.Dashboard -> {
                                DashboardScreen(viewModel = viewModel)
                            }
                            is Screen.CustomerList -> {
                                CustomerListScreen(viewModel = viewModel)
                            }
                            is Screen.CustomerDetail -> {
                                CustomerDetailScreen(customer = screen.customer, viewModel = viewModel)
                            }
                            is Screen.AutoNotifications -> {
                                AutoNotifScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
