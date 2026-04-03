package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey

/**
 * Экран создания нового заказа.
 */
@Composable
fun CreateOrderPage(navController: NavController) {
    var orderType by remember { mutableStateOf("") }
    var orderWeight by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }

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
                    .verticalScroll(rememberScrollState()),
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
                InputFieldWithLabel("Тип заказа", orderType) { orderType = it }
                InputFieldWithLabel("Вес заказа", orderWeight) { orderWeight = it }
                InputFieldWithLabel("Место назначения", destination) { destination = it }
                InputFieldWithLabel("Дата доставки", deliveryDate) { deliveryDate = it }
                InputFieldWithLabel("Комментарии", comments) { comments = it }
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
                        // Логика создания будет тут
                        navController.popBackStack() 
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