package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fgieracki.iotapplication.ui.theme.IoTGamboge

@Composable
fun LoginButtonGroup(registerActive: MutableState<Boolean>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        OutlinedButton(
            onClick = { registerActive.value = false },
            colors = ButtonDefaults.buttonColors(containerColor = if (!registerActive.value) IoTGamboge else Color.Transparent),
        ) {
            Text(text = "Login", color = if (!registerActive.value) Color.White else Color.Gray)
        }
        OutlinedButton(
            onClick = { registerActive.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = if (registerActive.value) IoTGamboge else Color.Transparent),
        ) {
            Text(text = "Register", color = if (registerActive.value) Color.White else Color.Gray)
        }
    }
}