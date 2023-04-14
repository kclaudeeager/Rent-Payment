package com.example.rent.ui.views

import android.graphics.drawable.Icon
import android.os.Bundle
import android.telephony.UiccCardInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rent.ui.theme.RentTheme
import com.example.rent.util.LoginResult
import com.example.rent.util.LoginResultSingleton
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rent.R
import com.example.rent.viewModels.RoomViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import com.example.rent.data.models.*
import com.example.rent.data.repositories.UserRepository
import com.example.rent.data.repositories.impl.RentalRepositoryImpl
import com.example.rent.network.ApiService
import com.example.rent.util.ApiServiceSingleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screens = listOf(
            Screen.Home
        )
        setContent {
            RentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(screens = screens)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(screens: List<Screen>) {
    var isDarkTheme by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val rentalRepository = RentalRepositoryImpl(ApiServiceSingleton.createApiService())

    val roomViewModel = RoomViewModel(repository = rentalRepository, coroutineScope = coroutineScope)
    val rooms by roomViewModel.rooms.observeAsState(emptyList())
    val availablerooms by roomViewModel.availableRooms.observeAsState(emptyList())
    val occupiedRooms by roomViewModel.occupiedRooms.observeAsState(emptyList())
    val invoices by roomViewModel.invoices.observeAsState(emptyList())
    val payments by roomViewModel.payments.observeAsState(emptyList())

    val roomCounts = mapOf(
        Screen.AvailableRooms to availablerooms.size,
        Screen.OccupiedRooms to occupiedRooms.size,
        Screen.Invoice to invoices.size,
        Screen.Payments to payments.size,
        Screen.Home to 0
    )
    val roomsData= mapOf(
        "all" to rooms,
        "available" to availablerooms,
       "occupied" to occupiedRooms
    )

    LaunchedEffect(roomViewModel) {
        roomViewModel.getRooms()
        roomViewModel.getAvailableRooms()
        roomViewModel.getOccupiedRooms()
        roomViewModel.getInvoices()
        roomViewModel.getPayments()
    }
    // Set up the theme for the whole app
    RentTheme(darkTheme = isDarkTheme) {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController, screens = screens, roomCounts = roomCounts) }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Set up the toggle button for switching between light and dark modes
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .zIndex(1f)
                        .offset(x = 0.dp, y = -10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val switchTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    val switchThumbColor = if (isDarkTheme) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                    val switchIcon = if (isDarkTheme) {
                        painterResource(id = R.drawable.dark)
                    } else {
                        painterResource(id = R.drawable.light)
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        modifier = Modifier
                            .size(24.dp)
                            .background(color = switchTrackColor, shape = CircleShape)
                            .padding(5.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = switchThumbColor,
                            uncheckedThumbColor = switchThumbColor
                        ),
                        thumbContent = { Image(painter = switchIcon, contentDescription = "Dark Mode") }
                    )
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(roomsData=roomsData, invoices = invoices, payments =payments)
                    }
                    composable(Screen.AvailableRooms.route) {
                        AvailableRoomsScreen(availablerooms)
                    }
                    composable(Screen.OccupiedRooms.route) {
                        OccupiedRoomsScreen(occupiedRooms)
                    }
                    composable(Screen.Invoice.route) {
                        InvoiceScreen(invoices)
                    }
                    composable(Screen.Payments.route) {
                        PaymentScreen(payments)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSettings(user: User) {
    var isDarkTheme by remember { mutableStateOf(false) }
    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Profile and Settings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // User Profile section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { },
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color.Black),
            elevation = cardElevation(),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = cardBackgroundColor,
                contentColor = textColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "User Profile",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Profile details
                ProfileDetails(
                    user
                )
            }
        }

        // Settings section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { },
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color.Black),
            elevation = cardElevation(),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = cardBackgroundColor,
                contentColor = textColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Dark mode switch
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .zIndex(1f)
                        .offset(x = 0.dp, y = -10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val switchTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    val switchThumbColor = if (isDarkTheme) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                    val switchIcon = if (isDarkTheme) {
                        painterResource(id = R.drawable.dark)
                    } else {
                        painterResource(id = R.drawable.light)
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { isDarkTheme = it },
                        modifier = Modifier
                            .size(24.dp)
                            .background(color = switchTrackColor, shape = CircleShape)
                            .padding(5.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = switchThumbColor,
                            uncheckedThumbColor = switchThumbColor
                        ),
                        thumbContent = { Image(painter = switchIcon, contentDescription = "Dark Mode") }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileDetails(user: User) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.person),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                // Set image size to 40 dp
                .size(40.dp)
                // Clip image to be shaped as a circle
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${user.f_name} ${user.l_name}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.mobile,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}


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

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun HomeScreen(
    roomsData: Map<String, List<Room>>,
    invoices: List<Invoice>,
    payments: List<Payment>
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                roomsData.get("all")?.let { AllRoomsScreen(it) }
            }
            item {
                roomsData.get("available")?.let { AvailableRoomsScreen(it) }
            }
            item {
                roomsData.get("occupied")?.let { OccupiedRoomsScreen(it) }
            }
            item {
                InvoiceScreen(invoices)
            }
            item {
                PaymentScreen(payments)
            }
        }
    }
}


@Composable
fun ProfileScreen() {
    Greeting(name = "Profile")
}

@Composable
fun AvailableRoomsScreen(rooms:List<Room>) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(rooms.size,"Available rooms", icon = {Icon(
        imageVector = Icons.Filled.Build,
        contentDescription = "Hotel icon",
        tint = iconColor,
        modifier = Modifier.size(48.dp)
    )})
}
@Composable
fun AllRoomsScreen(rooms:List<Room>) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(rooms.size,"Available rooms", icon = {Icon(
        imageVector = Icons.Filled.AccountCircle,
        contentDescription = "Hotel icon",
        tint = iconColor,
        modifier = Modifier.size(48.dp)
    )
    }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiccCardInfo(itemCount:Int, itemName:String, icon: @Composable () -> Unit){

    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable { },
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color.Black),
            elevation = cardElevation(),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = cardBackgroundColor,
                contentColor = textColor
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                icon()
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = " ${itemName}",
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = itemCount.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
fun OccupiedRoomsScreen(rooms:List<Room>) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(rooms.size,"Occupied rooms", icon ={
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = "Hotel icon",
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
    } )
}

@Composable
fun InvoiceScreen(invoices:List<Invoice>) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(invoices.size,"Invoices", icon = {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Invoice icon",
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
    } )
}

@Composable
fun PaymentScreen(payements:List<Payment>) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(payements.size,"Payments", icon = {
        Icon(
            imageVector = Icons.Filled.ThumbUp,
            contentDescription = "Payment icon",
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
    })
}



@Composable
fun Greeting(name: String) {
    val loginResult = LoginResultSingleton.getLoginResult()
    var user: User? = null



        when (loginResult.value) {
            is LoginResult.Success -> {
                user = (loginResult.value as LoginResult.Success).user
            }
            else -> {

            }
        }


    if (user != null) {
        Text(text = "Hello ${user.f_name}! you are at $name")
    } else {
        Text(text = "$name")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val screens = listOf(
        Screen.Home
    )
    RentTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(screens = screens)
        }
    }
}
