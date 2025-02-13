package com.example.critflix.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.critflix.viewmodel.APIViewModel

@Composable
fun ProfileView(navController: NavHostController, apiViewModel: APIViewModel){
    Column {
        Text(text = "hola")
    }
}