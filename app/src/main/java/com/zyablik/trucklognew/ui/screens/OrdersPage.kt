package com.zyablik.trucklognew.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.zyablik.trucklognew.api.OrderResponse
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey
import com.zyablik.trucklognew.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Окно с заказами.
 */
@Composable
fun OrdersPage(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var orders by remember { mutableStateOf<List<OrderResponse>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var isLoading by remember { mutableStateOf(false) }

    val token = sessionManager.getAuthToken() ?: ""

    // Загрузка заказов при открытии экрана
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getOrders("Bearer $token")
                if (response.isSuccessful) {
                    orders = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Ошибка загрузки заказов", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    val filteredOrders = if (searchQuery.isBlank()) orders else orders.filter {
        it.type.contains(searchQuery, ignoreCase = true) || it.destination.contains(searchQuery, ignoreCase = true)
    }

    // Окно с заказами
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        Box(
            Modifier
                .padding(innerpadding)
                .fillMaxSize()) {
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Поле поиска заказов
                Box(
                    Modifier
                        .padding(5.dp, 2.dp)
                        .clip(RoundedCornerShape(45.dp))
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MidLightGrey),
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Поиск по типу или месту", color = Color.Black) },
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

                // Отображение списка заказов
                Box(Modifier.weight(1f)) {
                    LazyColumn(
                        Modifier.padding(10.dp,10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredOrders){ order ->
                            Box(Modifier.background(MidLightGrey)
                                .fillMaxWidth()
                                .padding(10.dp,10.dp)
                                .clip(RoundedCornerShape(8.dp))){
                                Column { 
                                    Text("ID: ${order.id}", color = Color.Black)
                                    Text("Тип: ${order.type}", color = Color.Black)
                                    Text("Вес: ${order.weight} кг", color = Color.Black)
                                    Text("Куда: ${order.destination}", color = Color.Black)
                                    Text("Дата: ${order.deliveryDate}", color = Color.Black)
                                    Text("Статус: ${order.status}", color = Color.Black)
                                    if (order.comment != null) {
                                        Text("Коммент: ${order.comment}", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .height(120.dp)
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Кнопка создания нового заказа (только для клиентов)
                    if (sessionManager.getUserRole() == "customer") {
                        Button(
                            onClick = { navController.navigate("create_order") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightCyan
                            )
                        ) {
                            Text(
                                "Новый заказ",
                                color = Color.Black
                            )
                        }
                    }
                    // Возврат в главное меню
                    Button(
                        onClick = { navController.navigate("home") },
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
@Preview

@Composable
fun Preview7() {
    OrdersPage(navController = rememberNavController())
}