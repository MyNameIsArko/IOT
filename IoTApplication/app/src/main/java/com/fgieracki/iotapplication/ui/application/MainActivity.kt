package com.fgieracki.iotapplication.ui.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fgieracki.iotapplication.data.local.ActivityCatcher
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.ui.components.Navigation
import com.fgieracki.iotapplication.ui.theme.IoTApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextCatcher.setContext(applicationContext)
        ActivityCatcher.setActivity(this)

        setContent {
            IoTApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    IoTApplicationTheme {
//        Greeting("Android")
//    }
//}