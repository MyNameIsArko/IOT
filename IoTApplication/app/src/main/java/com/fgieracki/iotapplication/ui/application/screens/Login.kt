package com.fgieracki.iotapplication.ui.application.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fgieracki.iotapplication.ui.application.viewModels.SignInViewModel
import com.fgieracki.iotapplication.ui.components.LoginButtonGroup
import com.fgieracki.iotapplication.ui.components.LoginForm
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScreenLogin(viewModel: SignInViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
                onLogin: () -> Unit = {}) {
    val inputFields = viewModel.inputFields.collectAsState()
    val registerActive = remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navChannel.collectLatest {
            onLogin()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "IoT Application", fontSize = 35.sp)
        Spacer(modifier = Modifier.height(20.dp))

        LoginButtonGroup(registerActive = registerActive)

        Spacer(modifier = Modifier.height(20.dp))

        LoginForm(
            inputFields = inputFields.value,
            onUsernameChange = { viewModel.changeUsername(it) },
            onPasswordChange = { viewModel.changePassword(it) },
            onPasswordRepeatChange = { viewModel.changePasswordRepeat(it) },
            onLogin = { viewModel.login() },
            onRegister = { viewModel.register() },
            registerActive = registerActive.value
        )
    }
}