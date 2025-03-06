package com.zyablik.trucklognew

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.zyablik.trucklognew.ui.theme.MidLightGrey

@Composable
fun MainMenuPage(navController: NavController){
    val listButt = listOf(
        mainButtons("Профиль","profile"),
        mainButtons("Заказы","orders"),
        mainButtons("Транспорт","cars"),
        mainButtons("Настройки","settings")
    )
    Scaffold { innerpadding ->
        Box(Modifier.padding(innerpadding)){
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(20.dp)
            ) {
                items(listButt){ button ->
                    Box(Modifier.clickable(
                        onClick = { navController.navigate(button.route){
                            popUpTo(button.route){
                                inclusive = true
                            }
                        } }
                    )
                        .background(MidLightGrey)
                        .size(60.dp,180.dp)
                    ){
                        Text(button.name,
                            Modifier.align(Alignment.BottomCenter))
                    }

                }
            }
        }
    }
}
data class mainButtons(val name:String, val route:String)
@Preview

@Composable
fun Preview9() {
    MainMenuPage(navController = rememberNavController())
}