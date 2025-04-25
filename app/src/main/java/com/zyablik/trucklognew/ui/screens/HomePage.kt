package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.navigation.MainMenuPage
import com.zyablik.trucklognew.R
import com.zyablik.trucklognew.ui.theme.LightCyan

// Основной экран приложения
@Composable
fun HomePage(navController: NavController){
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        Box(Modifier.padding(innerpadding)) {
            Column(
                Modifier.align(Alignment.Center)
            ) {
                // Заголовок приложения
                Box(
                    Modifier
                        .fillMaxHeight(0.1f)
                        .fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.app_name),
                        Modifier.align(Alignment.Center),
                        color = LightCyan,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                // Поле меню приложения
                Box(
                    Modifier.background(Color.Blue)
                        .fillMaxHeight()
                        .fillMaxWidth()
                ) {
                    Navigation2(navController)
                }
            }
        }
    }
}

// Интерактивное меню приложения
@Composable
fun Navigation2(navController: NavController){
    val navController1 = rememberNavController()
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        NavHost(
            navController = navController1,
            startDestination = "home",
            modifier = Modifier.padding(innerpadding)
        ) {
            composable("settings") {
                SettingsPage(navController1)
            }
            composable("profile") {
                ProfilePage(navController,navController1)
            }
            composable("orders") {
                OrdersPage(navController1)
            }
            composable("cars") {
                CarsPage(navController1)
            }
            composable("home") {
                MainMenuPage(navController1)
            }
        }
    }
}

@Preview

@Composable
fun Preview4() {
    HomePage(navController = rememberNavController())
}
