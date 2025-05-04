package com.example.critflix.view.medium

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.critflix.viewmodel.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenombrarListaMedium(navController: NavController, listViewModel: ListViewModel, listId: String) {
   /* val context = LocalContext.current
    val userSessionManager = remember { UserSessionManager(context) }
    val token = userSessionManager.getToken() ?: ""
    val listas by listViewModel.listas.observeAsState(emptyList())
    val lista = listas.find { it.id == listId }
    var nuevoNombre by remember { mutableStateOf(lista?.name ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Renombrar Lista") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = {
                        if (it.length <= 100) {
                            nuevoNombre = it
                        }
                    },
                    label = { Text("Nombre de la lista") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nuevoNombre.isBlank(),
                    supportingText = {
                        Text(
                            text = "${nuevoNombre.length}/100",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (nuevoNombre.isNotBlank() && listId.isNotEmpty()) {
                            isLoading = true
                            listViewModel.renameList(listId, nuevoNombre, token)
                            // Esperar brevemente para dar tiempo a la operación
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(1000)
                                isLoading = false
                                Toast.makeText(context, "Lista renombrada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = nuevoNombre.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Renombrar Lista")
                    }
                }
            }
        }
    }*/
}