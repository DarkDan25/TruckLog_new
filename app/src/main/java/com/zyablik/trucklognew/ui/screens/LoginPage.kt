package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.LightlightGrey
import com.zyablik.trucklognew.ui.theme.MidLightGrey

// Окно регистрации
@Composable
fun LoginPage(navController: NavController) {
    var login_value by remember { mutableStateOf("") }
    var password_value by remember { mutableStateOf("") }
    Scaffold(Modifier.fillMaxWidth())
    { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
        {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .padding(0.dp, 10.dp)
            ) {
                // Поле ввода логина пользователя
                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp, 5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MidLightGrey),
                        value = login_value,
                        onValueChange = { login_value = it },
                        label = { Text("Логин") },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = MidLightGrey,
                            unfocusedIndicatorColor = MidLightGrey,
                            focusedLabelColor = Color.Black
                        )
                    )

                }
                // Поле ввода пароля
                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp, 5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MidLightGrey),
                        value = password_value,
                        onValueChange = { password_value = it },
                        label = { Text("Пароль") },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = MidLightGrey,
                            unfocusedIndicatorColor = MidLightGrey,
                            focusedLabelColor = Color.Black
                        )
                    )
                }
                // Кнопка переместит пользователя в главное меню если данные правильные
                Button(
                    onClick = { navController.navigate("homepage") },
                    Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightCyan
                    )
                ) {
                    Text(
                        "Вход",
                        color = Color.Black
                    )
                }
                // Кнопка переместит пользователя на экран регистрации
                Button(
                    onClick = { navController.navigate("registration") },
                    Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidLightGrey
                    )
                ) {
                    Text(
                        "Регистрация",
                        color = LightlightGrey
                    )
                }
            }
        }
    }
}

@Preview

@Composable
fun Preview3() {
    LoginPage(navController = rememberNavController())
}