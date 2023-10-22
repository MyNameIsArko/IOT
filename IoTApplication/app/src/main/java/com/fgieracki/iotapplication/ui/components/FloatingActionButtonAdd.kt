package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fgieracki.iotapplication.R
import com.fgieracki.iotapplication.ui.theme.IoTBlue

@Composable
fun FloatingActionButtonAdd(
    modifier: Modifier = Modifier,
    contentDesc: String,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = modifier
            .padding(16.dp)
            .background(Gray, shape = CircleShape),
//            .border(1.dp, IoTGamboge, CircleShape),
        shape = CircleShape,
        containerColor = IoTBlue,
        elevation = FloatingActionButtonDefaults.elevation(16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_add_24),
            tint = Color.White,
            contentDescription = contentDesc
        )
    }
}