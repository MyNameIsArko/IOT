package com.fgieracki.iotapplication.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fgieracki.iotapplication.di.viewModels.NavbarViewModel
import com.fgieracki.iotapplication.ui.screens.ScreenAddDevice
import com.fgieracki.iotapplication.ui.screens.ScreenHome
import com.fgieracki.iotapplication.ui.screens.ScreenLogin
import com.fgieracki.iotapplication.ui.screens.ScreenPairWithDevice
import kotlinx.coroutines.flow.collectLatest

@Composable
fun Navigation(viewModel : NavbarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_LONG).show()
        }
    }

    fun logout() {
        viewModel.logout()
        navController.navigate("login") {
            launchSingleTop = true
            popUpTo("home") {
                inclusive = true
            }
        }
    }

    fun navigateToHome() {
        navController.navigate("home") {
            launchSingleTop = true
            popUpTo("login") {
                inclusive = true
            }
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToAddDevice() {
        navController.navigate("addDevice") {
            launchSingleTop = true
            popUpTo("addDeviceTutorial") {
                inclusive = false
            }
        }
    }

    fun navigateToPairWithDevice() {
        navController.navigate("pairWithDevice") {
            launchSingleTop = true
            popUpTo("addDevice") {
                inclusive = false
            }
        }
    }

    NavHost(navController = navController, startDestination = "login" ) {
        composable("login") {
            ScreenLogin( onLogin = { navigateToHome() })
        }

        composable("home") {
            ScreenHome( onLogout = { logout() },
                        onAddDevice = {navigateToPairWithDevice()}
            )
        }


        composable("pairWithDevice") {
            ScreenPairWithDevice( onBackClick = { navigateBack() })
        }

        composable("addDevice") {
            ScreenAddDevice( onBackClick = { navigateBack() })
        }
    }
}