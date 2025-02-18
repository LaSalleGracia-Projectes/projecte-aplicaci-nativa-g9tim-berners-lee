package com.example.critflix.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.critflix.viewmodel.APIViewModel

@Composable
fun ProfileView(navController: NavHostController, apiViewModel: APIViewModel){
    Scaffold (
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Hola")
        }
    }
}