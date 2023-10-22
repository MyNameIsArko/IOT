package com.fgieracki.iotapplication.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fgieracki.iotapplication.ui.application.screens.ScreenAddDevice
import com.fgieracki.iotapplication.ui.application.screens.ScreenAddDeviceTutorial
import com.fgieracki.iotapplication.ui.application.screens.ScreenHome
import com.fgieracki.iotapplication.ui.application.screens.ScreenLogin
import com.fgieracki.iotapplication.ui.application.viewModels.NavbarViewModel
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

    fun navigateToAddDeviceTutorial() {
        navController.navigate("addDeviceTutorial") {
            launchSingleTop = true
            popUpTo("home") {
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
                        onAddDevice = {navigateToAddDeviceTutorial()}
            )
        }

        composable("addDeviceTutorial") {
            ScreenAddDeviceTutorial( onNextClick = { navigateToAddDevice() },
                onBackClick = { navigateBack() })
        }

        composable("addDevice") {
            ScreenAddDevice( onBackClick = { navigateBack() })
        }
    }
}