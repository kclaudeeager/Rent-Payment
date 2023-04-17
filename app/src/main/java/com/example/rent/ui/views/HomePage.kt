package com.example.rent.ui.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rent.data.models.Invoice
import com.example.rent.data.models.Payment
import com.example.rent.data.models.Room
import com.example.rent.data.models.Screen

@Composable
fun HomeScreen(
    roomsData: Map<String, List<Room>>,
    invoices: List<Invoice>,
    payments: List<Payment>,
    navController: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            userScrollEnabled = true
        ) {
            item {
                Box(
                    modifier = Modifier.clickable {
                        // Handle click for "All Rooms" item
                    }
                ) {
                    roomsData["all"]?.let { AllRoomsScreen(it,navController) }
                }
            }

            item {
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.AvailableRooms.route)
                    }
                ) {
                    roomsData["available"]?.let { AvailableRoomsScreen(it,navController) }
                }
            }
            item {
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.OccupiedRooms.route)
                    }
                ) {
                    roomsData["occupied"]?.let { OccupiedRoomsScreen(it,navController) }
                }
            }
            item {
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Invoice.route)

                    }
                ) {
                    InvoiceScreen(invoices,navController)
                }
            }
            item { Box(
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Payments.route)
                }
            ) {
                PaymentScreen(payments,navController)
            }
            }

        }
    }
}