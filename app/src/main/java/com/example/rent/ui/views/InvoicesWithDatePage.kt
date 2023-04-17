package com.example.rent.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rent.data.models.Screen
import com.example.rent.util.CountServicesSingleton
import com.example.rent.viewModels.RoomViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowInvoicesWithDate(roomViewModel: RoomViewModel, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    val invoices by roomViewModel.dueInvoices.observeAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            userScrollEnabled = true
        ) {
            item {
                DatePickerButton(
                    selectedDate = CountServicesSingleton.invoiceSelectedDate,
                    onDateSelected = { date ->
                        CountServicesSingleton.invoiceSelectedDate = date
                        roomViewModel.getDueInvoices(
                            CountServicesSingleton.invoiceSelectedDate.format(
                                DateTimeFormatter.ISO_LOCAL_DATE))
                    }
                )
            }
            item {
                invoices?.let {
                    CardInfoView(it.size, "Invoices", Screen.Invoice.route,navController, icon = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Invoice icon",
                            tint = iconColor,
                            modifier = Modifier.size(48.dp)
                        )
                    })
                }
            }
        }
    }
}