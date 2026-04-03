package com.zyablik.trucklognew.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey

/**
 * Экран с историей (бывший экран транспорта).
 * value - параметр, который используется поисковой строкой
 * searchHistory - история поисковых запросов
 * focusManager - управление фокусом для скрытия клавиатуры
 * keyboardController - управление клавиатурой
 */
@Composable
fun HistoryPage(navController: NavController) {
    var value by rememberSaveable { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val searchHistory = remember { SearchHistory(context = context) }

    // Контроллер клавиатуры и фокус поля ввода
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Список имеющихся автомобилей
    val cars = listOf(
        Cars("Honda", "Civic", "Free"),
        Cars("Nitsubishi", "Lancer X", "In service")
    )

    // Фильтрация списка машин по поисковому запросу
    val filteredCars = if (value.isBlank()) {
        cars
    } else {
        cars.filter {
            it.name.contains(value, ignoreCase = true) ||
            it.model.contains(value, ignoreCase = true) ||
            it.status.contains(value, ignoreCase = true)
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
                                        searchHistory.addQuery(value)
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
                        if (isSearchFocused && searchHistory.getQueries().isNotEmpty()) {
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
                                        items(searchHistory.getQueries()) { query ->
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
                                                IconButton(
                                                    onClick = {
                                                        searchHistory.removeQuery(query)
                                                    },
                                                    modifier = Modifier.padding(4.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Clear,
                                                        contentDescription = "Remove",
                                                        tint = Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Button(
                                        onClick = { searchHistory.clear() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LightCyan
                                        ),
                                        shape = RoundedCornerShape(45.dp)
                                    ) {
                                        Text("Очистить историю", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                // Поле отображения результатов (автомобилей)
                LazyColumn(
                    Modifier
                        .padding(10.dp, 10.dp)
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filteredCars) { car ->
                        Box(
                            Modifier
                                .background(MidLightGrey)
                                .fillMaxWidth()
                                .padding(10.dp, 10.dp)
                        ) {
                            Column {
                                Text("${car.name} ${car.model}", color = Color.Black)
                                Text("Status: ${car.status}", color = Color.Black)
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

/**
 * Класс автомобиля
 * name - марка машины
 * model - модель
 * status - статус машины (выполняет заказ, свободна, на ТО)
 */
data class Cars(val name: String, val model: String, val status: String)

data class SearchHistory(
    private val queries: MutableList<String> = mutableListOf(),
    private val maxSize: Int = 10,
    private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    init {
        // Загружаем сохраненную историю при создании
        val savedHistory = sharedPreferences.getString("queries", "") ?: ""
        if (savedHistory.isNotEmpty()) {
            queries.addAll(savedHistory.split("|||"))
        }
    }

    private fun saveToPreferences() {
        // Сохраняем историю в SharedPreferences
        sharedPreferences.edit()
            .putString("queries", queries.joinToString("|||"))
            .apply()
    }

    fun addQuery(query: String) {
        if (query.isBlank()) return
        queries.remove(query) // Remove if exists to avoid duplicates
        if (queries.size >= maxSize) {
            queries.removeAt(queries.size - 1) // Remove oldest query
        }
        queries.add(0, query) // Add new query at the beginning
        saveToPreferences()
    }

    fun clear() {
        queries.clear()
        saveToPreferences()
    }

    fun removeQuery(query: String) {
        queries.remove(query)
        saveToPreferences()
    }

    fun getQueries(): List<String> = queries
}

@Preview
@Composable
fun Preview8() {
    HistoryPage(navController = rememberNavController())
}