package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey

/**
 * Окно с заказами
 *
 * value - переменная для поисковой стркои (используется для сортировки заказов)
 * orders - список заказов (в будущем будет хранить либо все заказы, либо те, которые соответствуют запросу поиска)
 *
 * В Scaffold описана визуальная часть экрана.
 * Также имеется Button() с именем Назад, чтобы можно было вернуться в главное меню
 */
@Composable
fun OrdersPage(navController: NavController) {
    var value by remember { mutableStateOf("") }
    // Список заказов
    val orders = listOf(
        Order(1,"Done","Heavy","Someone"),
        Order(1,"Done","Heavy","Someone")
    )
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
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Поиск", color = Color.Black) },
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
                // Отобраэение списка заказов
                Box {
                    LazyColumn(
                        Modifier.padding(10.dp,10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(orders){ order ->
                            Box(Modifier.background(MidLightGrey)
                                .fillMaxWidth()
                                .padding(10.dp,10.dp)){
                                Column { 
                                    Text(order.id.toString())
                                    Text(order.status)
                                    Text(order.type)
                                    Text(order.driver)
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
                // Возврат в главное меню
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
data class Order(val id:Int, val status:String, val type:String, val driver:String)

@Preview

@Composable
fun Preview7() {
    OrdersPage(navController = rememberNavController())
}