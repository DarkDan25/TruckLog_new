package com.zyablik.trucklognew.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.api.AnimeQuote
import com.zyablik.trucklognew.api.RetrofitClient
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey
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

    // Лист с цитатами, сообщение об ошибке и корутина (из практики по мобильным приложениям)
    val quotesList = remember { mutableStateListOf<AnimeQuote>() }
    var errorMessage by rememberSaveable() { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Контроллер клавиатуры (чтобы закрть её) и фокус поля ввода (из практики по мобильным приложениям)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Список имеющихся автомобилей
    val cars = listOf(
        Cars("Honda", "Civic", "Free"),
        Cars("Nitsubishi", "Lancer X", "In service")
    )

    // Функция поиска цитаты с использованием API
    fun findQuote() {
        coroutineScope.launch {
            if (value.isNotBlank()) {
                try {
                    errorMessage = null;
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
                }
            }
        }
    }

    // Экран с поиском
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
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    Modifier
                        .padding(5.dp, 2.dp)
                        .clip(RoundedCornerShape(45.dp))
                ) {
                    // Поле поиска
                    Row(Modifier.fillMaxWidth()) {
                        TextField(
                            modifier = Modifier
                                .background(MidLightGrey),
                            value = value,
                            onValueChange = {
                                value = it
                                findQuote()
                            },
                            label = { Text("Поиск") },
                            shape = RoundedCornerShape(45.dp),
                            textStyle = TextStyle(color = Color.Black),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MidLightGrey,
                                unfocusedContainerColor = MidLightGrey,
                                focusedIndicatorColor = MidLightGrey,
                                unfocusedIndicatorColor = MidLightGrey,
                                focusedLabelColor = Color.Black
                            )
                        )
                        // Кнопка очещения поля поиска
                        Button(
                            onClick = {
                                value = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightCyan
                            )
                        ) {
                            Text("Очистить", fontSize = 12.sp, color = Color.Black)
                        }
                    }

                }

                // Поле отображения результатов поиска в зависимости от результата поиска
                Box {
                    // Ошибка при поиске
                    if (errorMessage != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(errorMessage!!)
                            Button(
                                onClick = { findQuote() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightCyan
                                )
                            ) {
                                Text(
                                    "Попробовать снова",
                                    color = Color.Black
                                )
                            }
                        }
                        // Поле поиска не пустое, но резльтата еще нет
                    } else if (quotesList.isEmpty() && value.isNotBlank()) {
                        Text(
                            "Загрузка...", modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Center)
                        )
                        // Резльтат имеется (для каждого результата делается свой блок)
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
                                        Text(quotes.show)
                                        Text(quotes.character)
                                        Text(quotes.quote)
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


@Preview

@Composable
fun Preview8() {
    CarsPage(navController = rememberNavController())
}