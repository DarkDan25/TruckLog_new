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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.LightlightGrey
import com.zyablik.trucklognew.ui.theme.MidLightGrey

/**
 * Экран авторизации пользователя.
 * login_value - логин пользователя
 * email_value - почтка пользователя
 * password_value - пароль пользователя
 * reply_password_value - проверка пароля пользователя (чтобы удостовериться что пароль введен был тот, что планировался пользователем)
 * Данные параметры нужны чтобы зарегестрировать пользователя в системе.
 *
 * Также имеется две кнопки:
 * Уже есть аккаунт - переносит пользователя на экран входа, чтобы пользователь мог войти в существующий аккаунт
 * Регистрация - переносит пользователя на экран главного меню после успешного создания аккаунта.
 */
@Composable
fun RegistrationPage(navController: NavController){
    // Значения для полей ввода
    var login_value by remember { mutableStateOf("") }
    var email_value by remember { mutableStateOf("") }
    var password_value by remember { mutableStateOf("") }
    var reply_password_value by remember { mutableStateOf("") }
    Scaffold(Modifier.fillMaxWidth())
    { innerPadding ->
        Box(
            Modifier.padding(innerPadding)
            .fillMaxSize())
        {
            Column(
                Modifier.align(Alignment.Center)
                    .padding(0.dp,10.dp)
            ) {
                Box(
                    Modifier
                        .width(200.dp)
                        .height(60.dp)
                        .padding(0.dp,5.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(MidLightGrey)
                ){
                    // Поле ввода логина
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = login_value,
                        onValueChange = {login_value = it},
                        label = { Text("Логин", color = Color.Black) },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = MidLightGrey,
                            unfocusedIndicatorColor = MidLightGrey,
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
                    // Поле ввода почты
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = email_value,
                        onValueChange = {email_value = it},
                        label = { Text("Почта", color = Color.Black) },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = MidLightGrey,
                            unfocusedIndicatorColor = MidLightGrey,
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
                    // Поле пароля (символы должы заменяться точками)
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = password_value,
                        onValueChange = {password_value = it},
                        label = { Text("Пароль", color = Color.Black) },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = MidLightGrey,
                            unfocusedIndicatorColor = MidLightGrey,
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
                    // Поле повтора пароля (символы должы заменяться точками)
                    TextField(
                        modifier = Modifier.fillMaxSize()
                            .background(MidLightGrey),
                        value = reply_password_value,
                        onValueChange = {reply_password_value = it},
                        label = { Text("Повторите пароль", color = Color.Black) },
                        shape = RoundedCornerShape(45.dp),
                        textStyle = TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MidLightGrey,
                            unfocusedContainerColor = MidLightGrey,
                            focusedIndicatorColor = MidLightGrey,
                            unfocusedIndicatorColor = MidLightGrey,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black
                        )
                    )
                }
                // Переход в главное меню
                Button(onClick = { navController.navigate("homepage") },
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
                        color = LightlightGrey,
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