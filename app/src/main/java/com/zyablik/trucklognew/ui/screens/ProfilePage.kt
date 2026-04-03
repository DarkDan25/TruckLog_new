package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.R
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.WarRed

import androidx.compose.ui.platform.LocalContext
import com.zyablik.trucklognew.utils.SessionManager

/**
 * Экран профиля пользователя.
 */
@Composable
fun ProfilePage(navController: NavController, navController1: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val userName = sessionManager.getUserName() ?: "Пользователь"
    val userRole = sessionManager.getUserRole() ?: "Неизвестно"

    // Экран профиля пользователя
    Scaffold { innerpadding ->
        Box(Modifier.padding(innerpadding)) {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "avatar",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Box(Modifier.fillMaxSize()) {
                    // Информация о пользователе
                    Column(
                        Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            userName,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                        Text(
                            "Роль: $userRole",
                            fontSize = 20.sp,
                            color = Color.Gray
                        )
                    }
                    Column(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp)
                        ) {
                            // Выход из аккаунта
                            Button(
                                onClick = {
                                    sessionManager.clearSession()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                Modifier.align(Alignment.BottomCenter),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WarRed
                                )
                            ) {
                                Text(
                                    "Выйти",
                                    color = Color.White
                                )
                            }
                        }
                        Box(
                            Modifier
                                .height(60.dp)
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp)
                        ) {
                            // Возврат в главное меню
                            Button(
                                onClick = {
                                    navController1.navigate("home")
                                },
                                Modifier.align(Alignment.BottomCenter),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightCyan
                                )
                            ) {
                                Text(
                                    "Назад",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview

@Composable
fun Preview6() {
    ProfilePage(navController = rememberNavController(), navController1 = rememberNavController())
}