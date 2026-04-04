package com.zyablik.trucklognew.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.api.AuthRequest
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.LightlightGrey
import com.zyablik.trucklognew.ui.theme.MidLightGrey
import com.zyablik.trucklognew.utils.SessionManager
import kotlinx.coroutines.launch

@Composable
fun LoginPage(navController: NavController) {
    var phoneValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var isLoading by remember { mutableStateOf(false) }

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
                // Поле ввода номера телефона (используется как логин)
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
                        value = phoneValue,
                        onValueChange = { phoneValue = it },
                        label = { Text("Номер телефона", color = Color.Black) },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
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
                        value = passwordValue,
                        onValueChange = { passwordValue = it },
                        label = { Text("Пароль", color = Color.Black) },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
                        )
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                // Кнопка переместит пользователя в главное меню если данные правильные
                Button(
                    onClick = { 
                        if (phoneValue.isNotBlank() && passwordValue.isNotBlank()) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.instance.login(AuthRequest(phoneValue, passwordValue))
                                    if (response.isSuccessful) {
                                        val authResponse = response.body()
                                        if (authResponse != null) {
                                            sessionManager.saveAuthToken(authResponse.token)
                                            sessionManager.saveUserRole(authResponse.role)
                                            sessionManager.saveUserName(authResponse.name)
                                            navController.navigate("homepage")
                                        }
                                    } else {
                                        Toast.makeText(context, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        }
                    },
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
                        color = Color.Black
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