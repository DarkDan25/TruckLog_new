package com.zyablik.trucklognew.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var searchHistoryList by remember { mutableStateOf<List<String>>(emptyList()) }
    var orders by remember { mutableStateOf<List<OrderResponse>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var isLoading by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val token = sessionManager.getAuthToken() ?: ""
    val userRole = sessionManager.getUserRole() ?: ""

    // Функция загрузки заказов
    val refreshOrders = {
        if (token.isNotEmpty()) {
            isLoading = true
            coroutineScope.launch {
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
    }

    // Загрузка истории поиска и заказов при открытии экрана
    LaunchedEffect(Unit) {
        refreshOrders()
        if (token.isNotEmpty()) {
            try {
                val response = RetrofitClient.instance.getSearchHistory("Bearer $token")
                if (response.isSuccessful) {
                    searchHistoryList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {}
        }
    }

    // Фильтрация активных заказов
    val activeOrders = orders.filter { order ->
        val s = order.status.uppercase()
        s != "DELIVERED" && s != "CANCELLED_BY_CUSTOMER" && s != "CANCELLED_BY_DRIVER"
    }

    val filteredOrders = if (searchQuery.isBlank()) activeOrders else activeOrders.filter {
        it.id.toString().contains(searchQuery, ignoreCase = true)
    }

    // Список статусов для прокрутки (для водителя)
    val statusCycle = listOf("ACCEPTED", "LOADING", "IN_TRANSIT", "DELIVERED")

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
                // Поле поиска заказов (аналогично HistoryPage)
                Box(
                    Modifier
                        .padding(5.dp, 2.dp)
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextField(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(45.dp))
                                    .background(MidLightGrey)
                                    .onFocusChanged { focusState ->
                                        isSearchFocused = focusState.isFocused
                                    },
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Поиск по ID", color = Color.Black) },
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
                            // Кнопка поиска
                            Button(
                                onClick = {
                                    val cleanedQuery = searchQuery.trim()
                                    if (cleanedQuery.isNotBlank()) {
                                        coroutineScope.launch {
                                            try {
                                                RetrofitClient.instance.saveSearchQuery("Bearer $token", cleanedQuery)
                                                val response = RetrofitClient.instance.getSearchHistory("Bearer $token")
                                                if (response.isSuccessful) {
                                                    searchHistoryList = response.body() ?: emptyList()
                                                }
                                            } catch (e: Exception) {}
                                        }
                                    }
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightCyan),
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(45.dp)),
                                shape = RoundedCornerShape(45.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            // Кнопка очищения
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LightCyan),
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(45.dp)),
                                shape = RoundedCornerShape(45.dp)
                            ) {
                                Text("Очистить", fontSize = 12.sp, color = Color.Black)
                            }
                        }

                        // История поиска
                        if (isSearchFocused && searchHistoryList.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MidLightGrey)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .heightIn(max = 200.dp)
                                ) {
                                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                        items(searchHistoryList) { query ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        searchQuery = query
                                                        focusManager.clearFocus()
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(query, color = Color.Black)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Кнопки управления заказом
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Кнопка "Принять" (только для водителя и если статус Pending)
                                        if (userRole == "driver" && order.status.equals("Pending", ignoreCase = true)) {
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        try {
                                                            val resp = RetrofitClient.instance.acceptOrder("Bearer $token", order.id)
                                                            if (resp.isSuccessful) {
                                                                // Мгновенное обновление локального списка для лучшего UX
                                                                 orders = orders.map { if (it.id == order.id) it.copy(status = "Accepted") else it }
                                                                 Toast.makeText(context, "Заказ принят", Toast.LENGTH_SHORT).show()
                                                                 refreshOrders()
                                                             } else {
                                                                 Toast.makeText(context, "Ошибка принятия: ${resp.code()}", Toast.LENGTH_SHORT).show()
                                                             }
                                                         } catch (e: Exception) {
                                                             Toast.makeText(context, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                                                         }
                                                     }
                                                 },
                                                modifier = Modifier.height(36.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = LightCyan)
                                            ) {
                                                Text("Принять", color = Color.Black, fontSize = 12.sp)
                                            }
                                        }

                                        // Кнопка "Изменить статус" (только для водителя и если он принял заказ)
                                        if (userRole == "driver" && statusCycle.any { it.equals(order.status, ignoreCase = true) }) {
                                            Button(
                                                onClick = {
                                                    val currentIndex = statusCycle.indexOfFirst { it.equals(order.status, ignoreCase = true) }
                                                    val nextIndex = (currentIndex + 1) % statusCycle.size
                                                    val nextStatus = statusCycle[nextIndex]
                                                    
                                                    coroutineScope.launch {
                                                        try {
                                                            val resp = RetrofitClient.instance.updateOrderStatus("Bearer $token", order.id, nextStatus)
                                                            if (resp.isSuccessful) {
                                                                // Мгновенное обновление локального списка
                                                                 orders = orders.map { if (it.id == order.id) it.copy(status = nextStatus) else it }
                                                                 Toast.makeText(context, "Статус обновлен", Toast.LENGTH_SHORT).show()
                                                                 refreshOrders()
                                                             } else {
                                                                 Toast.makeText(context, "Ошибка статуса: ${resp.code()}", Toast.LENGTH_SHORT).show()
                                                             }
                                                         } catch (e: Exception) {
                                                             Toast.makeText(context, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                                                         }
                                                     }
                                                 },
                                                modifier = Modifier.height(36.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = LightCyan)
                                            ) {
                                                Text("Изменить статус", color = Color.Black, fontSize = 12.sp)
                                            }
                                        }

                                        // Кнопка "Отменить заказ" (для всех, если не доставлен и не отменен)
                                        if (!order.status.equals("Delivered", ignoreCase = true) && !order.status.equals("Cancelled", ignoreCase = true)) {
                                            Button(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        try {
                                                            val resp = RetrofitClient.instance.cancelOrder("Bearer $token", order.id)
                                                            if (resp.isSuccessful) {
                                                                // Мгновенное обновление локального списка
                                                                 orders = orders.map { if (it.id == order.id) it.copy(status = "Cancelled") else it }
                                                                 Toast.makeText(context, "Заказ отменен", Toast.LENGTH_SHORT).show()
                                                                 refreshOrders()
                                                             } else {
                                                                 Toast.makeText(context, "Ошибка отмены: ${resp.code()}", Toast.LENGTH_SHORT).show()
                                                             }
                                                         } catch (e: Exception) {
                                                             Toast.makeText(context, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                                                         }
                                                     }
                                                 },
                                                modifier = Modifier.height(36.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f))
                                            ) {
                                                Text("Отменить", color = Color.White, fontSize = 12.sp)
                                            }
                                        }
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
                    if (userRole == "customer") {
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