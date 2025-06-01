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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.api.AnimeQuote
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Экран со списком машин агрегатора.
 * value - параметр, который используется поисковой строкой
 * quotesList - список найденых по запросу цитат (использовалось для практической работы по разработке мобильных приложений)
 * errorMessage - сообщение при возникновении ошибки
 * coroutineScope - запуск корутины для работы поиска
 * keyboardController - управление клавиаутрой, чтобы скрывать её, если требуется
 * focusManager - фокусировка на объекте (который использует пользователь. В данном случае это поисковая строка
 * cars - список автомобилей (это будет использовано чутка позже
 *
 * Функция findQuote ищет квоты по введеном запросу в поле поиска с использованием API
 * В Scaffold описан внешний вид страницы
 * Также в Box, где выводится результат поиска настроено поведение при успешном поиске, ошибке или при выполнении поиска (но результата еще нет)
 */
@Composable
fun CarsPage(navController: NavController) {
    var sliderPosition by remember { mutableStateOf(0f) }
    var value by rememberSaveable { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val searchHistory = remember { SearchHistory(context = context) }
    var isLoading by remember { mutableStateOf(false) }
    var lastSearchText by remember { mutableStateOf("") }
    
    // Лист с цитатами, сообщение об ошибке и корутина
    val quotesList = remember { mutableStateListOf<AnimeQuote>() }
    var errorMessage by rememberSaveable() { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Контроллер клавиатуры и фокус поля ввода
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Список имеющихся автомобилей
    val cars = listOf(
        Cars("Honda", "Civic", "Free"),
        Cars("Nitsubishi", "Lancer X", "In service")
    )

    // Функция поиска цитаты с использованием API
    fun findQuote() {
        if (value.isBlank()) return
        if (value == lastSearchText) return
        lastSearchText = value
        searchHistory.addQuery(value)
        isLoading = true
        
        coroutineScope.launch {
            try {
                errorMessage = null
                val newQuotes = RetrofitClient.instance.getQuotesByAnime(value)
                quotesList.clear()
                quotesList.addAll(newQuotes)
                if (newQuotes.isEmpty()) {
                    errorMessage = "Цитаты не найдены для \"$value\""
                    Log.d("rrr", errorMessage.toString())
                }
            } catch (e: Exception) {
                quotesList.clear()
                errorMessage = "Please, try again"
            } finally {
                isLoading = false
            }
        }
    }

    // Эффект для задержки поиска
    LaunchedEffect(value) {
        delay(2000)
        if (value.isNotBlank()) {
            findQuote()
        }
    }

    // Экран с поиском
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        Box(
            Modifier
                .padding(innerpadding)
                .fillMaxSize()
        ) {
            // Индикатор загрузки
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = LightCyan
                    )
                }
            }

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
                                    findQuote()
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

                // Поле отображения результатов поиска в зависимости от результата поиска
                Box {
                    // Ошибка при поиске
                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MidLightGrey)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    errorMessage!!,
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    color = Color.Black
                                )
                                Button(
                                    onClick = { findQuote() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LightCyan
                                    ),
                                    shape = RoundedCornerShape(45.dp)
                                ) {
                                    Text(
                                        "Попробовать снова",
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            Modifier
                                .padding(10.dp, 10.dp)
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(quotesList) { quotes ->
                                Box(
                                    Modifier
                                        .background(MidLightGrey)
                                        .fillMaxWidth()
                                        .padding(10.dp, 10.dp)
                                ) {
                                    Column {
                                        Text(quotes.show, color = Color.Black)
                                        Text(quotes.character, color = Color.Black)
                                        Text(quotes.quote, color = Color.Black)
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
    CarsPage(navController = rememberNavController())
}