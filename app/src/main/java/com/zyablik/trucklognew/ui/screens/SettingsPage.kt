package com.zyablik.trucklognew.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.theme.LightCyan
import com.zyablik.trucklognew.ui.theme.MidLightGrey

@Composable
fun SettingsPage(navController: NavController){
    var sliderPosition by remember { mutableStateOf(0f) }
    Scaffold(Modifier.fillMaxSize()) { innerpadding ->
        Box(Modifier.padding(innerpadding).fillMaxSize()){
            Column(Modifier.align(Alignment.TopStart).fillMaxWidth()
                .padding(0.dp,10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(){
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()){
                        Text("Уведомления")
                        Box(Modifier.width(5.dp))
                        Switch(checked = false,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LightCyan,
                                uncheckedThumbColor = LightCyan,
                                checkedBorderColor = MidLightGrey,
                                uncheckedBorderColor = MidLightGrey,
                                uncheckedTrackColor = MidLightGrey,
                                checkedTrackColor = MidLightGrey
                            ))}
                }
                Box{
                    Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()){
                        Text("Звук")
                        Box(Modifier.width(5.dp))
                        Slider(value = sliderPosition,
                        onValueChange = {sliderPosition},
                        Modifier.fillMaxWidth(0.3f),
                            colors = SliderDefaults.colors(
                                thumbColor = LightCyan
                            ))
                    }
                }
            }
             Box(Modifier.align(Alignment.BottomCenter)
                 .height(60.dp)
                 .fillMaxWidth()
                 .padding(0.dp,5.dp)){
                 Button(onClick = { navController.navigate("home") },
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

@Preview

@Composable
fun Preview5() {
    SettingsPage(navController = rememberNavController())
}