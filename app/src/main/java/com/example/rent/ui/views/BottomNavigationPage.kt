package com.example.rent.ui.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rent.data.models.Screen


@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    screens: List<Screen>,
    roomCounts: Map<Screen, Int>
) {
    // Keep track of the selected item index
    var selectedItemIndex by remember { mutableStateOf(0) }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        content = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    screens.forEachIndexed { index, screen ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(screen.route)
                            },
                            label = {

                                Text(
                                    text = screen.title,
                                    fontWeight = FontWeight.SemiBold
                                )

                            },
                            icon = {
                                Row(modifier = Modifier.padding()) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = "${screen.title} Icon"
                                    )
                                    if(screen.count!=null) {
                                        roomCounts[screen]?.let { count ->
                                            Text(
                                                text = count.toString(),
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }

                            }
                        )
                    }
                }
            )
        }
    )
}