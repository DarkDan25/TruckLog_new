package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.R
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.WarRed

@Composable
fun ProfilePage(navController: NavController, navController1: NavController){
    Scaffold { innerpadding ->
        Box(Modifier.padding(innerpadding)){
            Column {
                Box(Modifier.fillMaxWidth()
                .fillMaxHeight(0.3f)){
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "s",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Box(Modifier.fillMaxSize()){
                    Column(Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Фамилия ИО",
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center)
                        Text("ID: <номер>",
                            fontSize = 30.sp)
                    }
                    Column(
                        Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .padding(0.dp,5.dp)){
                            Button(onClick = { navController.navigate("login"){
                                popUpTo("login"){
                                    inclusive = true
                                }
                            }},
                                Modifier.align(Alignment.BottomCenter),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WarRed
                                )
                            ) {
                                Text("Выйти",
                                    color = Color.White)
                            }
                            // To make list of items
                            //Column { l.forEach {t-> Text(t.toString())} }
                        }
                        Box(Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .padding(0.dp,5.dp)){
                            Button(onClick = { navController1.navigate("home"){
                                popUpTo("home"){
                                    inclusive = true
                                }
                            } },
                                Modifier.align(Alignment.BottomCenter),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightCyan
                                )
                            ) {
                                Text("Назад",
                                    color = Color.Black)
                            }
                            // To make list of items
                            //Column { l.forEach {t-> Text(t.toString())} }
                        }
                    }
                }
            }
        }
    }
}

@Preview

@Composable
fun Preview6() {
    ProfilePage(navController = rememberNavController(), navController1 = rememberNavController())
}