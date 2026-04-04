package com.zyablik.trucklognew.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.api.OrderRequest
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey
import com.zyablik.trucklognew.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun CreateOrderPage(navController: NavController) {
    var orderType by remember { mutableStateOf("") }
    var orderWeight by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var comments by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var isLoading by remember { mutableStateOf(false) }

    val token = sessionManager.getAuthToken() ?: ""

    Scaffold(Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // Заголовок экрана
                Text(
                    text = "Создание заказа",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightCyan,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Поля ввода
                InputFieldWithLabel("Тип заказа (например: Мебель)", orderType) { orderType = it }
                InputFieldWithLabel("Вес заказа (кг)", orderWeight) { orderWeight = it }
                InputFieldWithLabel("Место назначения", destination) { destination = it }
                InputFieldWithLabel("Дата доставки (ГГГГ-ММ-ДД)", deliveryDate) { deliveryDate = it }
                InputFieldWithLabel("Комментарии", comments) { comments = it }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Кнопки внизу
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Кнопка создания заказа
                Button(
                    onClick = { 
                        if (orderType.isNotBlank() && orderWeight.isNotBlank() && destination.isNotBlank()) {
                            isLoading = true
                            coroutineScope.launch {
                                try {
                                    val weightDouble = orderWeight.toDoubleOrNull() ?: 0.0
                                    // Добавляем время T12:00:00 для корректного парсинга на сервере
                                    val fullIsoDate = "${deliveryDate}T12:00:00"
                                    
                                    val response = RetrofitClient.instance.createOrder(
                                        "Bearer $token",
                                        OrderRequest(orderType, weightDouble, destination, fullIsoDate, comments)
                                    )
                                    
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Заказ создан", Toast.LENGTH_SHORT).show()
                                        navController.navigate("orders") {
                                            popUpTo("home") // Remove create_order from backstack
                                        }
                                    } else {
                                        Toast.makeText(context, "Ошибка создания заказа", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        } else {
                            Toast.makeText(context, "Заполните основные поля", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightCyan
                    ),
                    shape = RoundedCornerShape(45.dp)
                ) {
                    Text("Создать заказ", color = Color.Black, fontSize = 18.sp)
                }

                // Кнопка отмены
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightCyan
                    ),
                    shape = RoundedCornerShape(45.dp)
                ) {
                    Text("Отмена", color = Color.Black, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun InputFieldWithLabel(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 10.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(45.dp))
                .background(MidLightGrey),
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
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateOrder() {
    com.zyablik.trucklognew.ui.theme.TruckLognewTheme {
        CreateOrderPage(navController = rememberNavController())
    }
}