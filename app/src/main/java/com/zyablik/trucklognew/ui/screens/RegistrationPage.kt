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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.api.RegisterRequest
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.LightlightGrey
import com.zyablik.trucklognew.ui.theme.MidLightGrey
import com.zyablik.trucklognew.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Экран регистрации пользователя.
 */
@Composable
fun RegistrationPage(navController: NavController){
    // Значения для полей ввода
    var nameValue by remember { mutableStateOf("") }
    var phoneValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var replyPasswordValue by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("customer") } // "customer" or "driver"
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(Modifier.fillMaxWidth())
    { innerPadding ->
        Box(
            Modifier.padding(innerPadding)
            .fillMaxSize())
        {
            Column(
                Modifier.align(Alignment.Center)
                    .padding(0.dp,10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp,5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ){
                    // Поле ввода ФИО
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = nameValue,
                        onValueChange = {nameValue = it},
                        label = { Text("ФИО", color = Color.Black) },
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

                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp,5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ){
                    // Поле ввода номера телефона
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = phoneValue,
                        onValueChange = {phoneValue = it},
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

                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp,5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ){
                    // Поле пароля
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = passwordValue,
                        onValueChange = {passwordValue = it},
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

                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp,5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ){
                    // Поле повтора пароля
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = replyPasswordValue,
                        onValueChange = {replyPasswordValue = it},
                        label = { Text("Повторите пароль", color = Color.Black) },
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

                // Выбор роли
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRole == "customer",
                        onClick = { selectedRole = "customer" }
                    )
                    Text("Клиент", color = Color.Black, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    RadioButton(
                        selected = selectedRole == "driver",
                        onClick = { selectedRole = "driver" }
                    )
                    Text("Водитель", color = Color.Black, fontSize = 12.sp)
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }

                // Регистрация
                Button(onClick = { 
                    if (nameValue.isNotBlank() && phoneValue.isNotBlank() && passwordValue.isNotBlank() && passwordValue == replyPasswordValue) {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val response = RetrofitClient.instance.register(
                                    RegisterRequest(nameValue, phoneValue, passwordValue, selectedRole)
                                )
                                if (response.isSuccessful) {
                                    val authResponse = response.body()
                                    if (authResponse != null) {
                                        sessionManager.saveAuthToken(authResponse.token)
                                        sessionManager.saveUserRole(authResponse.role)
                                        sessionManager.saveUserName(authResponse.name)
                                        navController.navigate("homepage")
                                    }
                                } else {
                                    Toast.makeText(context, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else if (passwordValue != replyPasswordValue) {
                        Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
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
                    Text("Регистрация",
                        color = Color.Black)
                }
                // Переход на экран авторизации
                Button(onClick = {navController.navigate("login")},
                    Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MidLightGrey
                    )) {
                    Text("Уже есть аккаунт",
                        color = Color.Black,
                        fontSize = 10.sp)
                }
            }
        }
    }
}

@Preview

@Composable
fun Preview2() {
    RegistrationPage(navController = rememberNavController())
}