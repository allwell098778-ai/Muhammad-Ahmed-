package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AmbientFrostedBackground
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignupScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.exchanges.ExchangeScreen
import com.example.ui.screens.ibadah.IbadahScreen
import com.example.ui.screens.intelligence.IntelligenceScreen
import com.example.ui.screens.leaderboard.LeaderboardScreen
import com.example.ui.screens.messages.ChatDetailScreen
import com.example.ui.screens.messages.MessagesScreen
import com.example.ui.screens.profile.EditProfileScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.videos.AiVideosScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val uiMessage by viewModel.uiMessage.collectAsState()

            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(uiMessage) {
                uiMessage?.let { msg ->
                    snackbarHostState.showSnackbar(msg)
                    viewModel.clearToast()
                }
            }

            WorldEagleTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Absolute Login Wall Enforcement:
                    // If currentUser is null, ONLY Login or Signup screen is rendered.
                    // Zero app navigation, sidebars, or internal widgets exist.
                    if (currentUser == null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when (currentScreen) {
                                AppDestination.SIGNUP -> SignupScreen(viewModel = viewModel)
                                else -> LoginScreen(viewModel = viewModel)
                            }

                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                            )
                        }
                    } else {
                        // Authenticated App Shell with Drawer, TopBar, BottomBar, and Screen Router
                        AuthenticatedAppShell(
                            viewModel = viewModel,
                            currentScreen = currentScreen,
                            snackbarHostState = snackbarHostState,
                            onBackPressed = {
                                if (!viewModel.navigateBack()) {
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatedAppShell(
    viewModel: MainViewModel,
    currentScreen: AppDestination,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler {
        onBackPressed()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = FrostedBackgroundDark,
                drawerContentColor = Color.White,
                modifier = Modifier
                    .width(300.dp)
                    .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Drawer Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(AmberLight, AmberDark)
                                )
                            )
                            .border(1.dp, Color(0x4DFFFFFF), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = currentUser?.dpUrl?.ifBlank { "🦅" } ?: "🦅", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "WORLD EAGLE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "${currentUser?.fullName ?: ""} (@${currentUser?.username ?: ""})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0x1FFFFFFF))

                // Navigation Items
                DrawerNavigationItem(
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    selected = currentScreen == AppDestination.DASHBOARD
                ) {
                    viewModel.navigateTo(AppDestination.DASHBOARD)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.Bolt,
                    label = "Early News & Signals",
                    selected = currentScreen == AppDestination.INTELLIGENCE_EARLY_NEWS
                ) {
                    viewModel.navigateTo(AppDestination.INTELLIGENCE_EARLY_NEWS)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.WarningAmber,
                    label = "Important News Impact",
                    selected = currentScreen == AppDestination.INTELLIGENCE_IMPORTANT_IMPACT
                ) {
                    viewModel.navigateTo(AppDestination.INTELLIGENCE_IMPORTANT_IMPACT)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.Business,
                    label = "Company News",
                    selected = currentScreen == AppDestination.INTELLIGENCE_COMPANY_NEWS
                ) {
                    viewModel.navigateTo(AppDestination.INTELLIGENCE_COMPANY_NEWS)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.RocketLaunch,
                    label = "New Companies & IPOs",
                    selected = currentScreen == AppDestination.INTELLIGENCE_NEW_COMPANIES
                ) {
                    viewModel.navigateTo(AppDestination.INTELLIGENCE_NEW_COMPANIES)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.AccountBalance,
                    label = "Top 20 Investors",
                    selected = currentScreen == AppDestination.INTELLIGENCE_TOP_INVESTORS
                ) {
                    viewModel.navigateTo(AppDestination.INTELLIGENCE_TOP_INVESTORS)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.ShowChart,
                    label = "Binance Intelligence",
                    selected = currentScreen == AppDestination.EXCHANGE_BINANCE
                ) {
                    viewModel.navigateTo(AppDestination.EXCHANGE_BINANCE)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.CurrencyExchange,
                    label = "MEXC Signals",
                    selected = currentScreen == AppDestination.EXCHANGE_MEXC
                ) {
                    viewModel.navigateTo(AppDestination.EXCHANGE_MEXC)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.CandlestickChart,
                    label = "WEEX Derivatives",
                    selected = currentScreen == AppDestination.EXCHANGE_WEEX
                ) {
                    viewModel.navigateTo(AppDestination.EXCHANGE_WEEX)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.PlayCircle,
                    label = "AI Videos (Daily 2)",
                    selected = currentScreen == AppDestination.AI_VIDEOS
                ) {
                    viewModel.navigateTo(AppDestination.AI_VIDEOS)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.Leaderboard,
                    label = "Leaderboard",
                    selected = currentScreen == AppDestination.LEADERBOARD
                ) {
                    viewModel.navigateTo(AppDestination.LEADERBOARD)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.Mosque,
                    label = "Ibadah Suite",
                    selected = currentScreen == AppDestination.IBADAH_NAMAZ
                ) {
                    viewModel.navigateTo(AppDestination.IBADAH_NAMAZ)
                    scope.launch { drawerState.close() }
                }

                if (currentUser?.role == "ADMIN") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0x1FFFFFFF))
                    DrawerNavigationItem(
                        icon = Icons.Default.AdminPanelSettings,
                        label = "Admin Control Center",
                        selected = currentScreen == AppDestination.ADMIN_PANEL,
                        badge = "OWNER"
                    ) {
                        viewModel.navigateTo(AppDestination.ADMIN_PANEL)
                        scope.launch { drawerState.close() }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(color = Color(0x1FFFFFFF))

                DrawerNavigationItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    selected = currentScreen == AppDestination.SETTINGS
                ) {
                    viewModel.navigateTo(AppDestination.SETTINGS)
                    scope.launch { drawerState.close() }
                }

                DrawerNavigationItem(
                    icon = Icons.Default.Logout,
                    label = "Sign Out",
                    selected = false,
                    isDestructive = true
                ) {
                    viewModel.logout()
                    scope.launch { drawerState.close() }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            containerColor = FrostedBackgroundDark,
            topBar = {
                if (currentScreen != AppDestination.CHAT_DETAIL) {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = getScreenTitle(currentScreen),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.toggleDarkMode() }) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "Toggle Theme",
                                    tint = AmberPrimary
                                )
                            }
                            IconButton(onClick = { viewModel.navigateTo(AppDestination.PROFILE) }) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AmberPrimary.copy(alpha = 0.2f))
                                        .border(1.dp, AmberPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = currentUser?.dpUrl?.ifBlank { "🦅" } ?: "🦅", fontSize = 18.sp)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0x180B132B)
                        )
                    )
                }
            },
            bottomBar = {
                if (currentScreen != AppDestination.CHAT_DETAIL) {
                    NavigationBar(
                        containerColor = Color(0x180B132B),
                        contentColor = Color.White
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppDestination.DASHBOARD,
                            onClick = { viewModel.navigateTo(AppDestination.DASHBOARD) },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                            label = { Text("Home") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmberPrimary,
                                selectedTextColor = AmberPrimary,
                                indicatorColor = AmberPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("nav_dashboard")
                        )
                        NavigationBarItem(
                            selected = isIntelligenceScreen(currentScreen),
                            onClick = { viewModel.navigateTo(AppDestination.INTELLIGENCE_EARLY_NEWS) },
                            icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
                            label = { Text("Signals") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmberPrimary,
                                selectedTextColor = AmberPrimary,
                                indicatorColor = AmberPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("nav_signals")
                        )
                        NavigationBarItem(
                            selected = isExchangeScreen(currentScreen),
                            onClick = { viewModel.navigateTo(AppDestination.EXCHANGE_BINANCE) },
                            icon = { Icon(Icons.Default.ShowChart, contentDescription = null) },
                            label = { Text("Exchanges") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmberPrimary,
                                selectedTextColor = AmberPrimary,
                                indicatorColor = AmberPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("nav_exchanges")
                        )
                        NavigationBarItem(
                            selected = isIbadahScreen(currentScreen),
                            onClick = { viewModel.navigateTo(AppDestination.IBADAH_NAMAZ) },
                            icon = { Icon(Icons.Default.Mosque, contentDescription = null) },
                            label = { Text("Ibadah") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmberPrimary,
                                selectedTextColor = AmberPrimary,
                                indicatorColor = AmberPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("nav_ibadah")
                        )
                        NavigationBarItem(
                            selected = currentScreen == AppDestination.MESSAGES,
                            onClick = { viewModel.navigateTo(AppDestination.MESSAGES) },
                            icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                            label = { Text("Desk") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AmberPrimary,
                                selectedTextColor = AmberPrimary,
                                indicatorColor = AmberPrimary.copy(alpha = 0.2f),
                                unselectedIconColor = Color(0xFF94A3B8),
                                unselectedTextColor = Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.testTag("nav_messages")
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AmbientFrostedBackground {
                    when (currentScreen) {
                        AppDestination.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        AppDestination.INTELLIGENCE_EARLY_NEWS -> IntelligenceScreen(viewModel = viewModel, initialTab = 0)
                        AppDestination.INTELLIGENCE_IMPORTANT_IMPACT -> IntelligenceScreen(viewModel = viewModel, initialTab = 1)
                        AppDestination.INTELLIGENCE_COMPANY_NEWS -> IntelligenceScreen(viewModel = viewModel, initialTab = 2)
                        AppDestination.INTELLIGENCE_NEW_COMPANIES -> IntelligenceScreen(viewModel = viewModel, initialTab = 3)
                        AppDestination.INTELLIGENCE_TOP_INVESTORS -> IntelligenceScreen(viewModel = viewModel, initialTab = 4)
                        AppDestination.EXCHANGE_BINANCE -> ExchangeScreen(viewModel = viewModel, initialExchange = "BINANCE")
                        AppDestination.EXCHANGE_MEXC -> ExchangeScreen(viewModel = viewModel, initialExchange = "MEXC")
                        AppDestination.EXCHANGE_WEEX -> ExchangeScreen(viewModel = viewModel, initialExchange = "WEEX")
                        AppDestination.AI_VIDEOS -> AiVideosScreen(viewModel = viewModel)
                        AppDestination.LEADERBOARD -> LeaderboardScreen(viewModel = viewModel)
                        AppDestination.MESSAGES -> MessagesScreen(viewModel = viewModel)
                        AppDestination.CHAT_DETAIL -> ChatDetailScreen(viewModel = viewModel)
                        AppDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
                        AppDestination.EDIT_PROFILE -> EditProfileScreen(viewModel = viewModel)
                        AppDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        AppDestination.IBADAH_NAMAZ -> IbadahScreen(viewModel = viewModel, initialTab = 0)
                        AppDestination.IBADAH_QURAN -> IbadahScreen(viewModel = viewModel, initialTab = 1)
                        AppDestination.IBADAH_ZIKR -> IbadahScreen(viewModel = viewModel, initialTab = 2)
                        AppDestination.IBADAH_AZAN -> IbadahScreen(viewModel = viewModel, initialTab = 3)
                        AppDestination.IBADAH_GOALS -> IbadahScreen(viewModel = viewModel, initialTab = 4)
                        AppDestination.IBADAH_ACHIEVEMENTS -> IbadahScreen(viewModel = viewModel, initialTab = 0)
                        AppDestination.ADMIN_PANEL -> AdminScreen(viewModel = viewModel)
                        else -> DashboardScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerNavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    badge: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isDestructive) SignalBearish else if (selected) EagleGold else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isDestructive) SignalBearish else if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                if (badge != null) {
                    Surface(
                        color = EagleGold,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black),
                            color = EagleNavy900,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = EagleNavy700.copy(alpha = 0.5f)
        )
    )
}

fun getScreenTitle(dest: AppDestination): String = when (dest) {
    AppDestination.DASHBOARD -> "World Eagle"
    AppDestination.INTELLIGENCE_EARLY_NEWS -> "Early News Signals"
    AppDestination.INTELLIGENCE_IMPORTANT_IMPACT -> "Important News Impact"
    AppDestination.INTELLIGENCE_COMPANY_NEWS -> "Company Intelligence"
    AppDestination.INTELLIGENCE_NEW_COMPANIES -> "New Startups & IPOs"
    AppDestination.INTELLIGENCE_TOP_INVESTORS -> "Top 20 Investors"
    AppDestination.EXCHANGE_BINANCE -> "Binance Desk"
    AppDestination.EXCHANGE_MEXC -> "MEXC Spot Desk"
    AppDestination.EXCHANGE_WEEX -> "WEEX Derivatives"
    AppDestination.AI_VIDEOS -> "AI Video Briefings"
    AppDestination.LEADERBOARD -> "Global Leaderboard"
    AppDestination.MESSAGES -> "Secure Messages"
    AppDestination.CHAT_DETAIL -> "Encrypted Desk"
    AppDestination.PROFILE -> "My Profile"
    AppDestination.EDIT_PROFILE -> "Edit Profile"
    AppDestination.SETTINGS -> "Settings"
    AppDestination.IBADAH_NAMAZ -> "Ibadah • Namaz"
    AppDestination.IBADAH_QURAN -> "Ibadah • Quran"
    AppDestination.IBADAH_ZIKR -> "Ibadah • Zikr"
    AppDestination.IBADAH_AZAN -> "Ibadah • Azan AI"
    AppDestination.IBADAH_GOALS -> "Ibadah • Azan Roadmap"
    AppDestination.IBADAH_ACHIEVEMENTS -> "Ibadah • Achievements"
    AppDestination.ADMIN_PANEL -> "Admin Console"
    else -> "World Eagle"
}

fun isIntelligenceScreen(dest: AppDestination): Boolean = when (dest) {
    AppDestination.INTELLIGENCE_EARLY_NEWS,
    AppDestination.INTELLIGENCE_IMPORTANT_IMPACT,
    AppDestination.INTELLIGENCE_COMPANY_NEWS,
    AppDestination.INTELLIGENCE_NEW_COMPANIES,
    AppDestination.INTELLIGENCE_TOP_INVESTORS -> true
    else -> false
}

fun isExchangeScreen(dest: AppDestination): Boolean = when (dest) {
    AppDestination.EXCHANGE_BINANCE,
    AppDestination.EXCHANGE_MEXC,
    AppDestination.EXCHANGE_WEEX -> true
    else -> false
}

fun isIbadahScreen(dest: AppDestination): Boolean = when (dest) {
    AppDestination.IBADAH_NAMAZ,
    AppDestination.IBADAH_QURAN,
    AppDestination.IBADAH_ZIKR,
    AppDestination.IBADAH_AZAN,
    AppDestination.IBADAH_GOALS,
    AppDestination.IBADAH_ACHIEVEMENTS -> true
    else -> false
}
