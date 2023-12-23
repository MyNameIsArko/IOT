package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fgieracki.iotapplication.R
import com.fgieracki.iotapplication.domain.model.LoginInputFields
import com.fgieracki.iotapplication.ui.theme.IoTBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(inputFields: LoginInputFields,
              onUsernameChange: (String) -> Unit = {},
              onPasswordChange: (String) -> Unit = {},
              onPasswordRepeatChange: (String) -> Unit = {},
              onLogin: () -> Unit = {},
              onRegister: () -> Unit = {},
              registerActive: Boolean = false,
) {
    val passwordVisible = remember { mutableStateOf(false) }
    val passwordRepeatVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = inputFields.username,
        onValueChange = { onUsernameChange(it) },
        label = { Text(text = "Username", color = IoTBlue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = IoTBlue,
        ),
        singleLine = true,
    )
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = inputFields.password,
        onValueChange = { onPasswordChange(it) },
        label = { Text(text = "Password", color = IoTBlue) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = IoTBlue,
        ),
        singleLine = true,
        placeholder = { Text(text = "Password") },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardActions = KeyboardActions(onDone = { if(!registerActive) onLogin() else onRegister() }),
        trailingIcon = {
            val image = if (passwordVisible.value)
                painterResource(id = R.drawable.baseline_visibility_24)
            else painterResource(id = R.drawable.baseline_visibility_off_24)
            val description = if (passwordVisible.value)
                "Hide password"
            else "Show password"
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(image, contentDescription = description)
            }
        },
        modifier = Modifier
            .padding(20.dp)
    )
    if(registerActive) {
        OutlinedTextField(
            value = inputFields.passwordRepeat,
            onValueChange = { onPasswordRepeatChange(it) },
            label = { Text(text = "Repeat Password", color = IoTBlue) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = IoTBlue,
            ),
            singleLine = true,
            placeholder = { Text(text = "Repeat Password") },
            visualTransformation = if (passwordRepeatVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(onDone = { onRegister() }),
            trailingIcon = {
                val image = if (passwordRepeatVisible.value)
                    painterResource(id = R.drawable.baseline_visibility_24)
                else painterResource(id = R.drawable.baseline_visibility_off_24)
                val description = if (passwordRepeatVisible.value)
                    "Hide password"
                else "Show password"
                IconButton(onClick = { passwordRepeatVisible.value = !passwordRepeatVisible.value }) {
                    Icon(image, contentDescription = description)
                }
            },
            modifier = Modifier
                .padding(20.dp)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))

    if(!registerActive) {
        Button(
            onClick = { onLogin() },
            modifier = Modifier
                .padding(horizontal = 60.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = IoTBlue)
        ) {
            Text(text = "Sign in",
                color = Color.White,
                fontSize = 20.sp,
            )
        }
    } else {
        Button(
            onClick = { onRegister() },
            modifier = Modifier
                .padding(horizontal = 60.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = IoTBlue)
        ) {
            Text(text = "Register",
                color = Color.White,
                fontSize = 20.sp,
            )
        }
    }
}