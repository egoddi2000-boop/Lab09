package com.example.lab09

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

private val AzulFuerte = Color(0xFF0D47A1)
private val Azul = Color(0xFF1976D2)
private val Celeste = Color(0xFFE3F2FD)
private val TextoOscuro = Color(0xFF1F2937)
private val TextoSuave = Color(0xFF6B7280)

@Composable
fun ScreenUsers(navController: NavHostController, servicio: UserApiService) {
    val listaUsers: SnapshotStateList<UserModel> = remember {
        mutableStateListOf()
    }

    var busqueda by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var recargar by remember { mutableStateOf(0) }

    LaunchedEffect(recargar) {
        try {
            cargando = true
            mensajeError = null

            val listado = servicio.getUsers()
            listaUsers.clear()
            listado.forEach { listaUsers.add(it) }
        } catch (e: Exception) {
            Log.e("USERS", "Error cargando usuarios", e)
            mensajeError = "No se pudieron cargar los usuarios."
        } finally {
            cargando = false
        }
    }

    val usuariosFiltrados = listaUsers.filter { user ->
        val inicial = user.name.firstOrNull()?.uppercaseChar()

        val coincideBusqueda =
            user.name.contains(busqueda, ignoreCase = true) ||
                    user.email.contains(busqueda, ignoreCase = true) ||
                    user.address.city.contains(busqueda, ignoreCase = true) ||
                    user.company.name.contains(busqueda, ignoreCase = true)

        val coincideFiltro = when (filtro) {
            "A-M" -> inicial != null && inicial in 'A'..'M'
            "N-Z" -> inicial != null && inicial in 'N'..'Z'
            else -> true
        }

        coincideBusqueda && coincideFiltro
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AzulFuerte, Azul, Celeste)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            HeaderUsers(
                total = listaUsers.size,
                ciudades = listaUsers.map { it.address.city }.distinct().size
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Buscar usuario, correo, ciudad o empresa")
                },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = "Buscar")
                },
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroChip("Todos", filtro) { filtro = "Todos" }
                FiltroChip("A-M", filtro) { filtro = "A-M" }
                FiltroChip("N-Z", filtro) { filtro = "N-Z" }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                mensajeError != null -> {
                    UsersErrorCard(
                        mensaje = mensajeError!!,
                        onRetry = { recargar++ }
                    )
                }

                usuariosFiltrados.isEmpty() -> {
                    UsersEmptyCard()
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = usuariosFiltrados,
                            key = { user -> user.id }
                        ) { user ->
                            UserCardPremium(
                                user = user,
                                onClick = {
                                    navController.navigate("usersVer/${user.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderUsers(total: Int, ciudades: Int) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.18f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Usuarios",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Explorador de Usuarios",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Ejercicio 1 - Recurso /users",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                icono = Icons.Outlined.Person,
                titulo = "Usuarios",
                valor = total.toString(),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                icono = Icons.Outlined.LocationOn,
                titulo = "Ciudades",
                valor = ciudades.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    icono: ImageVector,
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = Azul,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = valor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoOscuro
                )

                Text(
                    text = titulo,
                    color = TextoSuave,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun FiltroChip(
    texto: String,
    filtroActual: String,
    onClick: () -> Unit
) {
    val seleccionado = texto == filtroActual

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (seleccionado) Color.White else Color.White.copy(alpha = 0.28f)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (seleccionado) Azul else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun UserCardPremium(
    user: UserModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAF7FF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(user = user, size = 66.dp)

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoOscuro,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "@${user.username}",
                    color = Azul,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = user.email,
                    color = TextoSuave,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Ciudad",
                        tint = Azul,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = user.address.city,
                        color = TextoOscuro,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Ver detalle",
                    tint = TextoOscuro
                )
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
    var user by remember { mutableStateOf<UserModel?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var recargar by remember { mutableStateOf(0) }

    LaunchedEffect(id, recargar) {
        try {
            cargando = true
            mensajeError = null
            user = servicio.getUserById(id)
        } catch (e: Exception) {
            Log.e("USERS", "Error cargando usuario $id", e)
            mensajeError = "No se pudo cargar el usuario."
        } finally {
            cargando = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AzulFuerte, Azul, Celeste)
                )
            )
            .padding(16.dp)
    ) {
        when {
            cargando -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            mensajeError != null -> {
                UsersErrorCard(
                    mensaje = mensajeError!!,
                    onRetry = { recargar++ }
                )
            }

            user != null -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UserAvatar(user = user!!, size = 128.dp)

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = user!!.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextoOscuro
                                )

                                Text(
                                    text = "@${user!!.username}",
                                    color = Azul,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                InfoUserPremium(
                                    icono = Icons.Outlined.Email,
                                    titulo = "Correo",
                                    texto = user!!.email
                                )

                                InfoUserPremium(
                                    icono = Icons.Outlined.Phone,
                                    titulo = "Telefono",
                                    texto = user!!.phone
                                )

                                InfoUserPremium(
                                    icono = Icons.Outlined.Public,
                                    titulo = "Website",
                                    texto = user!!.website
                                )

                                InfoUserPremium(
                                    icono = Icons.Outlined.LocationOn,
                                    titulo = "Direccion",
                                    texto = "${user!!.address.street}, ${user!!.address.suite}, ${user!!.address.city}"
                                )

                                InfoUserPremium(
                                    icono = Icons.Outlined.Work,
                                    titulo = "Empresa",
                                    texto = user!!.company.name
                                )

                                InfoUserPremium(
                                    icono = Icons.Outlined.Work,
                                    titulo = "Frase de empresa",
                                    texto = user!!.company.catchPhrase
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        navController.navigate("users") {
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AzulFuerte
                                    )
                                ) {
                                    Text("Volver a usuarios")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserAvatar(user: UserModel, size: Dp) {
    val colores = listOf(
        Color(0xFFBBDEFB),
        Color(0xFFC8E6C9),
        Color(0xFFFFF9C4),
        Color(0xFFFFCCBC),
        Color(0xFFD1C4E9)
    )

    val colorFondo = colores[user.id % colores.size]
    val inicial = user.name.firstOrNull()?.uppercase() ?: "U"

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inicial,
            color = AzulFuerte,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )

        AsyncImage(
            model = "https://i.pravatar.cc/300?u=${user.email}",
            contentDescription = "Foto de ${user.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

@Composable
fun InfoUserPremium(
    icono: ImageVector,
    titulo: String,
    texto: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE3F2FD)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = Azul,
                modifier = Modifier
                    .padding(8.dp)
                    .size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Text(
                text = texto,
                color = TextoSuave,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun UsersErrorCard(
    mensaje: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = mensaje,
                color = TextoOscuro,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulFuerte
                )
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
fun UsersEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = "No se encontraron usuarios con esa busqueda.",
            modifier = Modifier.padding(16.dp),
            color = TextoOscuro,
            fontWeight = FontWeight.Bold
        )
    }
}
