package com.example.rent.ui.views

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rent.data.models.Room
import com.example.rent.data.models.Screen

@Composable
fun AvailableRoomsScreen(rooms:List<Room>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    CardInfoView(rooms.size,"Available", Screen.AvailableRooms.route,navController, icon = {
        Icon(
        imageVector = Icons.Filled.Warning,
        contentDescription = "Hotel icon",
        tint = iconColor,
        modifier = Modifier.size(48.dp)
    )
    })
}