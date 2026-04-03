package com.zyablik.trucklognew.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey
import com.zyablik.trucklognew.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Экран с историей (бывший экран транспорта).
 */
@Composable
fun HistoryPage(navController: NavController) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var appliedSearchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var searchHistoryList by remember { mutableStateOf<List<String>>(emptyList()) }
    var orders by remember { mutableStateOf<List<com.zyablik.trucklognew.api.OrderResponse>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    var isLoading by remember { mutableStateOf(false) }

    val token = sessionManager.getAuthToken() ?: ""

    // Контроллер клавиатуры и фокус поля ввода
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Загрузка истории и заказов
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            isLoading = true
            try {
                // История поиска
                val searchResp = RetrofitClient.instance.getSearchHistory("Bearer $token")
                if (searchResp.isSuccessful) {
                    searchHistoryList = searchResp.body() ?: emptyList()
                }
                
                // История заказов
                val ordersResp = RetrofitClient.instance.getOrders("Bearer $token")
                if (ordersResp.isSuccessful) {
                    orders = ordersResp.body() ?: emptyList()
                }
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    // Фильтрация завершенных заказов
    val finishedOrders = orders.filter { order ->
        val s = order.status.uppercase()
        s == "DELIVERED" || s == "CANCELLED_BY_CUSTOMER" || s == "CANCELLED_BY_DRIVER"
    }

    val filteredOrders = if (appliedSearchQuery.isBlank()) finishedOrders else finishedOrders.filter {
        it.id.toString().contains(appliedSearchQuery, ignoreCase = true)
    }

    // Экран с историей
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        Box(
            Modifier
                .padding(innerpadding)
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .padding(0.dp, 10.dp)
                    .fillMaxHeight()
                    .padding(bottom = 70.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    Modifier
                        .padding(5.dp, 2.dp)
                ) {
                    // Поле поиска (по ID заказа)
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
                                label = { Text("Поиск заказа по ID", color = Color.Black) },
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
                                    val cleanedQuery = searchQuery.trim().replace("\"", "").replace("'", "")
                                    appliedSearchQuery = cleanedQuery
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
                                    appliedSearchQuery = ""
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
                                                        appliedSearchQuery = query
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

                // Отображение истории заказов
                LazyColumn(
                    Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filteredOrders) { order ->
                        Box(
                            Modifier
                                .background(MidLightGrey)
                                .fillMaxWidth()
                                .padding(10.dp, 10.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Column {
                                Text("ID: ${order.id}", color = Color.Black)
                                Text("Тип: ${order.type}", color = Color.Black)
                                Text("Куда: ${order.destination}", color = Color.Black)
                                Text("Статус: ${order.status}", color = Color.Black)
                                Text("Дата: ${order.deliveryDate}", color = Color.Black)
                            }
                        }
                    }
                }
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .height(60.dp)
                    .fillMaxWidth()
                    .padding(0.dp, 5.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Кнопка возврата в главное меню
                Button(
                    onClick = { navController.navigate("home") },
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

@Preview
@Composable
fun Preview8() {
    HistoryPage(navController = rememberNavController())
}