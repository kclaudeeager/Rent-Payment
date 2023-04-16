package com.example.rent.ui.views

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.telephony.UiccCardInfo
import android.widget.Button
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.window.DialogProperties
import com.example.rent.data.models.*
import com.example.rent.data.repositories.UserRepository
import com.example.rent.data.repositories.impl.RentalRepositoryImpl
import com.example.rent.network.ApiService
import com.example.rent.util.ApiServiceSingleton
import com.example.rent.util.CountServicesSingleton.selectedDate
import com.example.rent.util.ThemeManager
import com.maxkeppeker.sheets.core.icons.LibIcons
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.maxkeppeler.sheets.calendar.models.CalendarTimeline
import dagger.hilt.EntryPoint
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


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
//    var isDarkTheme by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val rentalRepository = RentalRepositoryImpl(ApiServiceSingleton.createApiService())
    val roomViewModel = RoomViewModel(repository = rentalRepository, coroutineScope = coroutineScope)
    val rooms by roomViewModel.rooms.observeAsState(emptyList())
    val availablerooms by roomViewModel.availableRooms.observeAsState(emptyList())
    val occupiedRooms by roomViewModel.occupiedRooms.observeAsState(emptyList())
    val invoices by roomViewModel.dueInvoices.observeAsState(emptyList())
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
        roomViewModel.getDueInvoices(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
    // Set up the theme for the whole app
    RentTheme(darkTheme = ThemeManager.isDarkTheme) {
        val navController = rememberNavController()
        Scaffold(
            topBar = {
                  topNavigationDrawer()

            },
            bottomBar = { BottomNavigationBar(navController = navController, screens = screens, roomCounts = roomCounts) }
        ) {

            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(roomsData=roomsData, invoices = invoices, payments =payments,navController)
                    }
                    composable(Screen.AvailableRooms.route) {
                        AvailableRoomsScreen(availablerooms,navController)
                    }
                    composable(Screen.OccupiedRooms.route) {
                        OccupiedRoomsScreen(occupiedRooms,navController)
                    }
                    composable(Screen.Invoice.route) {
                        showInvoicesWithDate(roomViewModel,navController)
                    }
                    composable(Screen.Payments.route) {
                        PaymentScreen(payments,navController)
                    }
                }
            }
        }
    }
}
@Composable
fun showInvoicesWithDate(roomViewModel: RoomViewModel, navController: NavHostController) {
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
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        selectedDate = date
                        roomViewModel.getDueInvoices(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    }
                )
            }
            item {
                invoices?.let {
                    UiccCardInfo(it.size, "Invoices",Screen.Invoice.route,navController, icon = {
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerButton(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
//    var showDatePicker by remember { mutableStateOf(false) }
    val calendarState = rememberSheetState()
    val today = LocalDate.now()
    val maxYear = today.year

    val calendarConfig = CalendarConfig(
        monthSelection = true,
        yearSelection = true,
        minYear = 1900,
        maxYear = maxYear,
        disabledDates = null,
        disabledTimeline = CalendarTimeline.FUTURE
    )

    CalendarDialog(
        state = calendarState,
        selection = CalendarSelection.Date { date ->
            onDateSelected(date)
        },
        config = calendarConfig,
        header = null,
        properties = DialogProperties()
    )


    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.onTertiary)

    ) {
        Button(
            onClick = {  calendarState.show() },
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            content = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected date: ${selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    IconButton(
                        onClick = {
                            calendarState.show()
                                  },
                        modifier = Modifier.size(48.dp),
                        content = {
                            Icon(
                                Icons.Filled.CalendarToday,
                                contentDescription = "Select date",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                }
            }
        )
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topNavigationDrawer() {
    val items = listOf(Icons.Default.Person)
    val selectedItem = remember { mutableStateOf(items[0]) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val loginResult = LoginResultSingleton.getLoginResult()
    var user: User? = null

    when (loginResult.value) {
        is LoginResult.Success -> {
            user = (loginResult.value as LoginResult.Success).user
        }
        else -> {

        }
    }

    suspend fun DrawerState.openOrClose() {
        if (isClosed) open() else close()
    }

    Column() {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
        ){
            IconButton(
                onClick = { scope.launch { drawerState.openOrClose() } },
                modifier = Modifier.padding(16.dp)
            ) {
                val icon = if (drawerState.isClosed) Icons.Default.Settings else Icons.Default.Close
                Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            }
            Text(text = "CHICK",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(24.dp),
                fontWeight= FontWeight.Bold,
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            content = {
                Box(modifier = Modifier.fillMaxSize())
            },
            drawerContent = {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (selectedItem.value == Icons.Default.Person && user != null) {
                        ProfileAndSettings(user = user)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSettings(user: User) {
    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(MaterialTheme.colorScheme.inversePrimary)
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
                        user,

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
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val switchTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        val switchThumbColor = if (ThemeManager.isDarkTheme) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                        val switchIcon = if (ThemeManager.isDarkTheme) {
                            painterResource(id = R.drawable.dark)
                        } else {
                            painterResource(id = R.drawable.light)
                        }

                        Switch(
                            checked = ThemeManager.isDarkTheme,
                            onCheckedChange = { ThemeManager.isDarkTheme = it },
                            modifier = Modifier
                                .size(24.dp)
                                .background(color = switchTrackColor, shape = CircleShape)
                                .padding(5.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = switchThumbColor,
                                uncheckedThumbColor = switchThumbColor
                            ),
                            thumbContent = {
                                Image(
                                    painter = switchIcon,
                                    contentDescription = "Dark Mode"
                                )
                            }
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
                        roomsData.get("all")?.let { AllRoomsScreen(it,navController) }
                    }
                }

            item {
                Box(
                    modifier = Modifier.clickable {
                       navController.navigate(Screen.AvailableRooms.route)
                    }
                ) {
                    roomsData.get("available")?.let { AvailableRoomsScreen(it,navController) }
                }
            }
            item {
                Box(
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.OccupiedRooms.route)
                    }
                ) {
                    roomsData.get("occupied")?.let { OccupiedRoomsScreen(it,navController) }
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

@Composable
fun AvailableRoomsScreen(rooms:List<Room>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(rooms.size,"Available",Screen.AvailableRooms.route,navController, icon = {Icon(
        imageVector = Icons.Filled.Warning,
        contentDescription = "Hotel icon",
        tint = iconColor,
        modifier = Modifier.size(48.dp)
    )})
}
@Composable
fun AllRoomsScreen(rooms:List<Room>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(rooms.size,"All",Screen.Home.route,navController, icon = {Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = "Hotel icon",
        tint = iconColor,
        modifier = Modifier.size(48.dp)
    )
    }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiccCardInfo(itemCount:Int, itemName:String, root:String, navController: NavHostController,icon: @Composable () -> Unit){

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
                .clickable {
                    navController.navigate(root)
                },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color.Black),
            elevation = cardElevation(),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = cardBackgroundColor,
                contentColor = textColor
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon()
                Spacer(Modifier.width(12.dp))
                Text(
                    text = " ${itemName}",
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = itemCount.toString(),
                    color = textColor,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

        }
    }
}


@Composable
fun OccupiedRoomsScreen(rooms:List<Room>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(rooms.size,"Occupied",Screen.OccupiedRooms.route,navController, icon ={
        Icon(
            imageVector = Icons.Filled.Done,
            contentDescription = "Hotel icon",
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
    } )
}

@Composable
fun InvoiceScreen(invoices: List<Invoice>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            UiccCardInfo(invoices.size, "Invoices",Screen.Invoice.route,navController, icon = {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Invoice icon",
                    tint = iconColor,
                    modifier = Modifier.size(48.dp)
                )
            })

    }
}




@Composable
fun PaymentScreen(payements:List<Payment>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    UiccCardInfo(payements.size,"Payments",Screen.Payments.route,navController, icon = {
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
@Preview(showBackground = true)
@Composable
fun ProfileSettingPreview(){
    RentTheme {
        ProfileAndSettings(user = User("1","Kwizera","Kwizera","claudekwiera003@gmail.com","2","1","3","1","0788722091","2"))
    }
}
