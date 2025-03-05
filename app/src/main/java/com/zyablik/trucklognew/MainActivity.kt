package com.zyablik.trucklognew

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}
enum class t(){
    HomePage,
    Login,
    Register
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        NavHost(
            navController,
            startDestination = t.Login.name,
            modifier = Modifier.padding(innerpadding)
        ) {
            composable(t.Login.name) {
                LoginPage(navController)
            }
            composable(t.Register.name) {
                RegistrationPage(navController)
            }
            composable(t.HomePage.name) {
                HomePage(navController)
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)

@Composable
fun Preview() {
    Navigation()
}