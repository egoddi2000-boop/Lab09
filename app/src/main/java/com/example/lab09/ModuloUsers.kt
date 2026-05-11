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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SortByAlpha
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

private val UserInk = Color(0xFF172033)
private val UserTeal = Color(0xFF00897B)
private val UserIndigo = Color(0xFF304FFE)
private val UserAmber = Color(0xFFFFB300)
private val UserCoral = Color(0xFFFF7043)
private val UserPaper = Color(0xFFF7F8FC)
private val UserMuted = Color(0xFF6E7787)

@Composable
fun ScreenUsers(navController: NavHostController, servicio: UserApiService) {
    val listaUsers: SnapshotStateList<UserModel> = remember {
        mutableStateListOf()
    }

    var busqueda by remember {
        mutableStateOf("")
    }

    var ciudadSeleccionada by remember {
        mutableStateOf("Todas")
    }

    var ordenAZ by remember {
        mutableStateOf(true)
    }

    var cargando by remember {
        mutableStateOf(true)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    var recargar by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(recargar) {
        try {
            cargando = true
            mensajeError = null

            val listado = servicio.getUsers()
            listaUsers.clear()
            listaUsers.addAll(listado)

        } catch (e: Exception) {
            Log.e("USERS", "Error cargando usuarios", e)
            mensajeError = "No se pudieron cargar los usuarios."
        } finally {
            cargando = false
        }
    }

    val ciudades = listOf("Todas") + listaUsers
        .map { it.address.city }
        .distinct()
        .take(5)

    val usuariosFiltrados = listaUsers
        .filter { user ->
            val coincideTexto =
                user.name.contains(busqueda, ignoreCase = true) ||
                        user.username.contains(busqueda, ignoreCase = true) ||
                        user.email.contains(busqueda, ignoreCase = true) ||
                        user.address.city.contains(busqueda, ignoreCase = true) ||
                        user.company.name.contains(busqueda, ignoreCase = true)

            val coincideCiudad =
                ciudadSeleccionada == "Todas" ||
                        user.address.city == ciudadSeleccionada

            coincideTexto && coincideCiudad
        }
        .let { lista ->
            if (ordenAZ) {
                lista.sortedBy { it.name }
            } else {
                lista.sortedByDescending { it.name }
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        UserInk,
                        Color(0xFF24324C),
                        UserPaper
                    )
                )
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            UsersHero(
                total = listaUsers.size,
                ciudades = listaUsers.map { it.address.city }.distinct().size,
                empresas = listaUsers.map { it.company.name }.distinct().size
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Buscar usuario, correo, ciudad o empresa")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (busqueda.isNotBlank()) {
                        IconButton(
                            onClick = {
                                busqueda = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ciudades) { ciudad ->
                    UserFilterChip(
                        texto = ciudad,
                        seleccionado = ciudad == ciudadSeleccionada,
                        onClick = {
                            ciudadSeleccionada = ciudad
                        }
                    )
                }

                item {
                    UserFilterChip(
                        texto = if (ordenAZ) "A-Z" else "Z-A",
                        seleccionado = true,
                        onClick = {
                            ordenAZ = !ordenAZ
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }

                mensajeError != null -> {
                    UsersMessageCard(
                        mensaje = mensajeError!!,
                        accion = "Reintentar",
                        onClick = {
                            recargar++
                        }
                    )
                }

                usuariosFiltrados.isEmpty() -> {
                    UsersMessageCard(
                        mensaje = "No se encontraron usuarios.",
                        accion = "Limpiar filtros",
                        onClick = {
                            busqueda = ""
                            ciudadSeleccionada = "Todas"
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = usuariosFiltrados,
                            key = { user -> user.id }
                        ) { user ->
                            UserCardDeluxe(
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
fun UsersHero(
    total: Int,
    ciudades: Int,
    empresas: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = UserTeal.copy(alpha = 0.16f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Usuarios",
                        tint = UserTeal,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Directorio inteligente",
                        color = UserInk,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Ejercicio 1 con /users",
                        color = UserMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserMetric(
                    icono = Icons.Outlined.Person,
                    titulo = "Usuarios",
                    valor = total.toString(),
                    color = UserTeal,
                    modifier = Modifier.weight(1f)
                )

                UserMetric(
                    icono = Icons.Outlined.LocationOn,
                    titulo = "Ciudades",
                    valor = ciudades.toString(),
                    color = UserIndigo,
                    modifier = Modifier.weight(1f)
                )

                UserMetric(
                    icono = Icons.Outlined.Apartment,
                    titulo = "Empresas",
                    valor = empresas.toString(),
                    color = UserCoral,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun UserMetric(
    icono: ImageVector,
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.11f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(23.dp)
            )

            Text(
                text = valor,
                color = UserInk,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = titulo,
                color = UserMuted,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun UserFilterChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(8.dp),
        color = if (seleccionado) {
            UserAmber
        } else {
            Color.White.copy(alpha = 0.84f)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (texto == "A-Z" || texto == "Z-A") {
                Icon(
                    imageVector = Icons.Outlined.SortByAlpha,
                    contentDescription = "Orden",
                    tint = UserInk,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = texto,
                color = if (seleccionado) UserInk else UserMuted,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun UserCardDeluxe(
    user: UserModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 7.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatarDeluxe(
                user = user,
                size = 66.dp
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    color = UserInk,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "@${user.username}",
                    color = UserTeal,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = user.email,
                    color = UserMuted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SmallBadge(
                        icono = Icons.Outlined.LocationOn,
                        texto = user.address.city,
                        color = UserIndigo
                    )
                }
            }
        }
    }
}

@Composable
fun SmallBadge(
    icono: ImageVector,
    texto: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = color,
                modifier = Modifier.size(14.dp)
            )

            Spacer(modifier = Modifier.width(3.dp))

            Text(
                text = texto,
                color = color,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall
            )
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

    var cargando by remember {
        mutableStateOf(true)
    }

    var mensajeError by remember {
        mutableStateOf<String?>(null)
    }

    var recargar by remember {
        mutableStateOf(0)
    }

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
                    listOf(
                        UserInk,
                        Color(0xFF24324C),
                        UserPaper
                    )
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
                UsersMessageCard(
                    mensaje = mensajeError!!,
                    accion = "Reintentar",
                    onClick = {
                        recargar++
                    }
                )
            }

            user != null -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 10.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                UserAvatarDeluxe(
                                    user = user!!,
                                    size = 126.dp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = user!!.name,
                                    color = UserInk,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall
                                )

                                Text(
                                    text = "@${user!!.username}",
                                    color = UserTeal,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                DetailSection("Contacto") {
                                    InfoUserDeluxe(
                                        icono = Icons.Outlined.Email,
                                        titulo = "Correo",
                                        texto = user!!.email
                                    )

                                    InfoUserDeluxe(
                                        icono = Icons.Outlined.Phone,
                                        titulo = "Telefono",
                                        texto = user!!.phone
                                    )

                                    InfoUserDeluxe(
                                        icono = Icons.Outlined.Public,
                                        titulo = "Website",
                                        texto = user!!.website
                                    )
                                }

                                DetailSection("Ubicacion") {
                                    InfoUserDeluxe(
                                        icono = Icons.Outlined.LocationOn,
                                        titulo = "Direccion",
                                        texto = "${user!!.address.street}, ${user!!.address.suite}, ${user!!.address.city}"
                                    )
                                }

                                DetailSection("Empresa") {
                                    InfoUserDeluxe(
                                        icono = Icons.Outlined.Work,
                                        titulo = "Nombre",
                                        texto = user!!.company.name
                                    )

                                    InfoUserDeluxe(
                                        icono = Icons.Outlined.Apartment,
                                        titulo = "Frase",
                                        texto = user!!.company.catchPhrase
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        navController.navigate("users") {
                                            launchSingleTop = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = UserInk
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
fun DetailSection(
    titulo: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            color = UserInk,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        content()
    }
}

@Composable
fun UserAvatarDeluxe(
    user: UserModel,
    size: Dp
) {
    val colores = listOf(
        Color(0xFFE0F2F1),
        Color(0xFFE8EAF6),
        Color(0xFFFFF8E1),
        Color(0xFFFBE9E7),
        Color(0xFFEDE7F6)
    )

    val colorFondo = colores[user.id % colores.size]
    val inicial = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inicial,
            color = UserInk,
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
fun InfoUserDeluxe(
    icono: ImageVector,
    titulo: String,
    texto: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = UserTeal.copy(alpha = 0.12f)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = UserTeal,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(11.dp))

        Column {
            Text(
                text = titulo,
                color = UserInk,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = texto,
                color = UserMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun UsersMessageCard(
    mensaje: String,
    accion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = mensaje,
                color = UserInk,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UserInk
                )
            ) {
                Text(accion)
            }
        }
    }
}
