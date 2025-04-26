package com.zyablik.trucklognew.ui.navigation

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
import com.zyablik.trucklognew.ui.screens.HomePage
import com.zyablik.trucklognew.ui.screens.LoginPage
import com.zyablik.trucklognew.ui.screens.RegistrationPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Navigation()
        }
    }
}

/**
 * Переключение между экранами приложения:
 * регистрация (registration),
 * авторизация (login),
 * главное меню ()homepage).
 */
@Composable
fun Navigation(){
    val navController = rememberNavController()
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        NavHost(
            navController,
            startDestination = "login",
            modifier = Modifier.padding(innerpadding)
        ) {
            composable("login") {
                LoginPage(navController) // Экран авторизации
            }
            composable("registration") {
                RegistrationPage(navController) // Экран регистрации
            }
            composable("homepage") {
                HomePage(navController) // Экран главного меню
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