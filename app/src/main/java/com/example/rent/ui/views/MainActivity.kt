package com.example.rent.ui.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rent.ui.theme.RentTheme
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat.startActivity
import com.example.rent.viewModels.RoomViewModel
import com.example.rent.data.models.*
import com.example.rent.data.repositories.impl.RentalRepositoryImpl
import com.example.rent.data.repositories.impl.UserRepositoryImpl
import com.example.rent.util.*
import com.example.rent.util.CountServicesSingleton.invoiceSelectedDate
import com.example.rent.util.CountServicesSingleton.paymentSelectedDate
import com.example.rent.viewModels.LoginViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : ComponentActivity() {
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val screens = listOf(
            Screen.Home
        )
        val userRepositoryImpl=UserRepositoryImpl(ApiServiceSingleton.createApiService())
        val loginViewModel = LoginViewModel(userRepositoryImpl)
        setContent {
            RentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(screens = screens,loginViewModel)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(screens: List<Screen>,loginViewModel: LoginViewModel) {
//    var isDarkTheme by remember { mutableStateOf(false) }
  val user:User? = UserSingleton.user
    var loggedOut by remember {
        mutableStateOf(false)
    }
    if(user==null){
        return
    }
    val coroutineScope = rememberCoroutineScope()
    val rentalRepository = RentalRepositoryImpl(ApiServiceSingleton.createApiService())
    val roomViewModel = RoomViewModel(repository = rentalRepository, coroutineScope = coroutineScope)
    val rooms by roomViewModel.rooms.observeAsState(emptyList())
    val availablerooms by roomViewModel.availableRooms.observeAsState(emptyList())
    val occupiedRooms by roomViewModel.occupiedRooms.observeAsState(emptyList())
    val invoices by roomViewModel.dueInvoices.observeAsState(emptyList())
    val payments by roomViewModel.duePayments.observeAsState(emptyList())
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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    suspend fun DrawerState.openOrClose() {
        if (isClosed) open() else close()
    }
    if (loggedOut) {
        val intent = Intent(LocalContext.current, LoginActivity::class.java)
        LocalContext.current.startActivity(intent)
        loginViewModel.logout()
        (LocalContext.current as Activity).finishAffinity()
    }

    LaunchedEffect(roomViewModel) {
        roomViewModel.getRooms()
        roomViewModel.getAvailableRooms()
        roomViewModel.getOccupiedRooms()
        roomViewModel.getInvoices()
        roomViewModel.getDuePayments(paymentSelectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        roomViewModel.getDueInvoices(invoiceSelectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
    // Set up the theme for the whole app
    val navController = rememberNavController()
val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        RentTheme(darkTheme = ThemeManager.isDarkTheme) {

            TopNavigationDrawer(drawerState, content = {
                Scaffold(
                    modifier= Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection ),
                    topBar = {
                        TopAppBar(
                          navigationIcon ={
                              IconButton(
                                  onClick = { scope.launch { drawerState.openOrClose() } },
                                  modifier = Modifier.padding(16.dp)
                              ) {
                                  val icon = if (drawerState.isClosed) Icons.Default.Menu else Icons.Default.Close
                                  Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
                              }
                          },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically
                                    ) {
                                    Spacer(modifier =Modifier.padding(12.dp) )
                                    Icon(Icons.Filled.House, contentDescription = "App Icon")
                                    Text(text = "Rent Management App")
                                }
                            },

                            colors = TopAppBarDefaults.mediumTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ),
                            scrollBehavior = scrollBehavior
                        )
                    },
                    bottomBar = { BottomNavigationBar(navController = navController, screens = screens, roomCounts = roomCounts) }
                ) {

                    Box(modifier = Modifier.fillMaxSize()) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) {
                                Box( modifier = Modifier
                                    .fillMaxSize()
                                    .zIndex(1f)){
                                    HomeScreen(roomsData=roomsData, invoices = invoices, payments =payments,navController)
                                }
                            }
                            composable(Screen.AvailableRooms.route) {
                                AvailableRoomsScreen(availablerooms,navController)
                            }
                            composable(Screen.OccupiedRooms.route) {
                                OccupiedRoomsScreen(occupiedRooms,navController)
                            }
                            composable(Screen.Invoice.route) {
                                ShowInvoicesWithDate(roomViewModel,navController)
                            }
                            composable(Screen.Payments.route) {
                                ShowPaymentsWithDate(roomViewModel,navController)
                            }
                            composable("login") {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    LoginScreen(loginViewModel, navController = navController)
                                }
                            }
                        }
                    }


                }

            }, onLogout ={
                navController.navigate("login")
                loggedOut=true

            })

        }
    }

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun AllRoomsScreen(rooms:List<Room>, navController: NavHostController) {
    val iconColor = MaterialTheme.colorScheme.primary
    CardInfoView(rooms.size,"All",Screen.Home.route,navController, icon = {Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = "Hotel icon",
        tint = iconColor,
        modifier = Modifier.size(48.dp)
    )
    }
    )
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
    val userRepositoryImpl=UserRepositoryImpl(ApiServiceSingleton.createApiService())
    val loginViewModel = LoginViewModel(userRepositoryImpl)
    RentTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(screens = screens,loginViewModel)
        }
    }
}

