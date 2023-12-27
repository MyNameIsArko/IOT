package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fgieracki.iotapplication.R
import com.fgieracki.iotapplication.ui.theme.IoTAmaranth
import com.fgieracki.iotapplication.ui.theme.IoTBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceDialog(
    onDismiss: () -> Unit = {},
    onAddDevice: () -> Unit = {},
    ssid: String = "",
    password: String = "",
    onPasswordChange: (String) -> Unit = {},
    onSsidChange: (String) -> Unit = {}
) {
    val passwordVisible = remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = true,
        ),
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(24.dp),
        ) {
            OutlinedTextField(
                value = ssid,
                onValueChange = { onSsidChange(it) },
            )
            Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { onPasswordChange(it) },
                label = { Text(text = "Password") },
                supportingText = { Text(text = "Leave empty if network is not secured") },
                placeholder = { Text(text = "Network Password") },
                visualTransformation = if (passwordVisible.value) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

            )
            Spacer(modifier = Modifier.padding(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = IoTAmaranth,
                    )
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White,
                    )
                }

                Button(
                    onClick = { onAddDevice() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IoTBlue,
                    )
                ) {
                    Text(text = "Connect")
                }
            }

        }
    }
}