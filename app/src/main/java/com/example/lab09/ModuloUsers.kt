package com.example.lab09

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ScreenUsers(navController: NavHostController, servicio: UserApiService) {

    val listaUsers: SnapshotStateList<UserModel> = remember {
        mutableStateListOf()
    }

    LaunchedEffect(Unit) {
        try {
            val listado = servicio.getUsers()

            listaUsers.clear()

            listado.forEach {
                listaUsers.add(it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1976D2),
                        Color(0xFFE3F2FD)
                    )
                )
            )
            .padding(12.dp)
    ) {

        Text(
            text = "Lista de Usuarios",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn {

            items(listaUsers) { user ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            navController.navigate("usersVer/${user.id}")
                        },

                    shape = RoundedCornerShape(18.dp),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Usuario",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = user.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = user.email,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = user.address.city,
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        IconButton(
                            onClick = {
                                navController.navigate("usersVer/${user.id}")
                            }
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Ver detalle"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenUserDetalle(
    navController: NavHostController,
    servicio: UserApiService,
    id: Int
) {

    var user by remember {
        mutableStateOf<UserModel?>(null)
    }

    LaunchedEffect(Unit) {

        try {

            user = servicio.getUserById(id)

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .padding(16.dp)
    ) {

        if (user != null) {

            Card(
                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(22.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Usuario",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(70.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user!!.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "@${user!!.username}",
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    InfoUser(
                        icono = Icons.Outlined.Email,
                        titulo = "Correo",
                        texto = user!!.email
                    )

                    InfoUser(
                        icono = Icons.Outlined.Phone,
                        titulo = "Teléfono",
                        texto = user!!.phone
                    )

                    InfoUser(
                        icono = Icons.Outlined.LocationOn,
                        titulo = "Dirección",
                        texto = "${user!!.address.street}, ${user!!.address.city}"
                    )

                    InfoUser(
                        icono = Icons.Outlined.Work,
                        titulo = "Empresa",
                        texto = user!!.company.name
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            navController.navigate("users")
                        },

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text("Volver a usuarios")
                    }
                }
            }

        } else {

            Text("Cargando usuario...")
        }
    }
}

@Composable
fun InfoUser(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    texto: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icono,
            contentDescription = titulo,
            tint = Color(0xFF1976D2)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {

            Text(
                text = titulo,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = texto,
                color = Color.DarkGray
            )
        }
    }
}