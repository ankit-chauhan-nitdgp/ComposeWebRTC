package com.ankit.whatsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ankit.whatsapp.navigation.BottomNavigationBar
import com.ankit.whatsapp.screens.authpages.ForgotPasswordScreen
import com.ankit.whatsapp.screens.authpages.LoginScreen
import com.ankit.whatsapp.screens.authpages.RegisterScreen
import com.ankit.whatsapp.screens.landingpages.CallScreen
import com.ankit.whatsapp.screens.landingpages.ChatScreen
import com.ankit.whatsapp.screens.landingpages.ProfileScreen
import com.ankit.whatsapp.theme.WhatsAppComposeTheme
import com.ankit.whatsapp.utils.PagesRouteNames
import com.ankit.whatsapp.utils.SharedPreferenceHelper
import com.ankit.whatsapp.viewmodels.FirebaseViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel : FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this.applicationContext)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        setContent {
            WhatsAppComposeTheme {
                MainScaffold()
            }
        }

    }


    override fun onStart() {
        super.onStart()
        viewModel.setUserStatus(true)
    }

    override fun onStop() {
        super.onStop()
        viewModel.setUserStatus(false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        PagesRouteNames.CHAT_PAGE,
        PagesRouteNames.CALL_PAGE,
        PagesRouteNames.PROFILE_PAGE
    )

    val topBarTitle = when (currentRoute) {
        PagesRouteNames.CHAT_PAGE -> "Chats"
        PagesRouteNames.CALL_PAGE -> "Calls"
        PagesRouteNames.PROFILE_PAGE -> "Profile"
        PagesRouteNames.LOGIN_PAGE -> ""
        PagesRouteNames.REGISTER_PAGE -> "Register"
        PagesRouteNames.FORGOT_PASSWORD_PAGE -> "Forgot Password"
        else -> ""
    }

    val showTopBar = topBarTitle.isNotEmpty()

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(topBarTitle) },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null &&
                            currentRoute !in bottomBarRoutes
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AppContent(navController)
        }
    }
}

@Composable
fun AppContent(navController: NavHostController) {
    val context = LocalContext.current.applicationContext
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Run once to decide start screen
    LaunchedEffect(Unit) {
        delay(1500) // short splash delay
        val isLoggedIn = SharedPreferenceHelper.readBooleanSharedPreference(
            context,
            SharedPreferenceHelper.IS_LOGGED_IN_KEY
        ) == true

        startDestination = if (isLoggedIn) {
            PagesRouteNames.CHAT_PAGE
        } else {
            PagesRouteNames.LOGIN_PAGE
        }
    }

    when (val destination = startDestination) {
        null -> SplashScreen() // still deciding
        else -> MainNavHost(navController, destination)
    }
}


@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "App Logo",
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "WhatsApp",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}


@Composable
fun MainNavHost(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = PagesRouteNames.LOGIN_PAGE) {

            LoginScreen(
                onLoginSuccess = {

                    navController.navigate(PagesRouteNames.CHAT_PAGE) {
                        popUpTo(PagesRouteNames.LOGIN_PAGE) { inclusive = true }
                    }

                },

                onRegisterClick = {
                    navController.navigate(PagesRouteNames.REGISTER_PAGE)
                },

                onForgotPasswordClick = {
                    navController.navigate(PagesRouteNames.FORGOT_PASSWORD_PAGE)
                }

            )
        }



        composable(route = PagesRouteNames.REGISTER_PAGE) {
            RegisterScreen() {
                navController.navigate(PagesRouteNames.LOGIN_PAGE) {
                    popUpTo(PagesRouteNames.REGISTER_PAGE) { inclusive = true }
                }
            }
        }

        composable(route = PagesRouteNames.CHAT_PAGE) {
            ChatScreen()
        }

        composable(route = PagesRouteNames.CALL_PAGE) {
            CallScreen()
        }

        composable(route = PagesRouteNames.PROFILE_PAGE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(PagesRouteNames.LOGIN_PAGE) {
                        popUpTo(PagesRouteNames.CHAT_PAGE) { inclusive = true }
                    }
                }
            )

        }

        composable(route = PagesRouteNames.FORGOT_PASSWORD_PAGE) {
            ForgotPasswordScreen()
        }

    }
}




