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
    var value by rememberSaveable { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var searchHistoryList by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val token = sessionManager.getAuthToken() ?: ""

    // Контроллер клавиатуры и фокус поля ввода
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Загрузка истории поиска с сервера
    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            try {
                val response = RetrofitClient.instance.getSearchHistory("Bearer $token")
                if (response.isSuccessful) {
                    searchHistoryList = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Игнорируем ошибки для истории
            }
        }
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
                    // Поле поиска
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth(),
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
                                value = value,
                                onValueChange = { value = it },
                                label = { Text("Поиск", color = Color.Black) },
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
                                    if (value.isNotBlank()) {
                                        coroutineScope.launch {
                                            try {
                                                RetrofitClient.instance.saveSearchQuery("Bearer $token", value)
                                                // Обновляем локальный список истории
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightCyan
                                ),
                                modifier = Modifier
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(45.dp)),
                                shape = RoundedCornerShape(45.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            // Кнопка очищения поля поиска
                            Button(
                                onClick = {
                                    value = ""
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightCyan
                                ),
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
                                        .height(200.dp)
                                ) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        items(searchHistoryList) { query ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        value = query
                                                        focusManager.clearFocus()
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
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

                // Поле отображения истории поиска (вместо машин)
                LazyColumn(
                    Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val filteredHistory = if (value.isBlank()) {
                        searchHistoryList
                    } else {
                        searchHistoryList.filter { it.contains(value, ignoreCase = true) }
                    }
                    items(filteredHistory) { query ->
                        Box(
                            Modifier
                                .background(MidLightGrey)
                                .fillMaxWidth()
                                .padding(10.dp, 10.dp)
                        ) {
                            Text(query, color = Color.Black)
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