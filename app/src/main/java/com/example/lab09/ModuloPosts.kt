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
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

private val PostInk = Color(0xFF172033)
private val PostIndigo = Color(0xFF304FFE)
private val PostAmber = Color(0xFFFFB300)
private val PostCoral = Color(0xFFFF7043)
private val PostPaper = Color(0xFFF7F8FC)
private val PostMuted = Color(0xFF6E7787)

@Composable
fun ScreenPosts(navController: NavHostController, servicio: PostApiService) {
    val listaPosts: SnapshotStateList<PostModel> = remember {
        mutableStateListOf()
    }

    var busqueda by remember {
        mutableStateOf("")
    }

    var filtroUsuario by remember {
        mutableStateOf(0)
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

            val listado = servicio.getUserPosts()
            listaPosts.clear()
            listaPosts.addAll(listado)

        } catch (e: Exception) {
            Log.e("POSTS", "Error cargando posts", e)
            mensajeError = "No se pudieron cargar los posts."
        } finally {
            cargando = false
        }
    }

    val postsFiltrados = listaPosts.filter { post ->
        val coincideTexto =
            post.title.contains(busqueda, ignoreCase = true) ||
                    post.body.contains(busqueda, ignoreCase = true) ||
                    post.id.toString() == busqueda.trim()

        val coincideUsuario =
            filtroUsuario == 0 || post.userId == filtroUsuario

        coincideTexto && coincideUsuario
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        PostInk,
                        Color(0xFF263858),
                        PostPaper
                    )
                )
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PostsHero(
                total = listaPosts.size,
                visibles = postsFiltrados.size
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Buscar publicacion")
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PostFilterChip(
                    texto = "Todos",
                    seleccionado = filtroUsuario == 0,
                    onClick = {
                        filtroUsuario = 0
                    }
                )

                PostFilterChip(
                    texto = "User 1",
                    seleccionado = filtroUsuario == 1,
                    onClick = {
                        filtroUsuario = 1
                    }
                )

                PostFilterChip(
                    texto = "User 2",
                    seleccionado = filtroUsuario == 2,
                    onClick = {
                        filtroUsuario = 2
                    }
                )

                PostFilterChip(
                    texto = "User 3",
                    seleccionado = filtroUsuario == 3,
                    onClick = {
                        filtroUsuario = 3
                    }
                )
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
                    PostMessageCard(
                        mensaje = mensajeError!!,
                        accion = "Reintentar",
                        onClick = {
                            recargar++
                        }
                    )
                }

                postsFiltrados.isEmpty() -> {
                    PostMessageCard(
                        mensaje = "No hay publicaciones con ese filtro.",
                        accion = "Limpiar",
                        onClick = {
                            busqueda = ""
                            filtroUsuario = 0
                        }
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = postsFiltrados,
                            key = { post -> post.id }
                        ) { post ->
                            PostCardDeluxe(
                                post = post,
                                onClick = {
                                    navController.navigate("postsVer/${post.id}")
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
fun PostsHero(
    total: Int,
    visibles: Int
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
                    color = PostAmber.copy(alpha = 0.22f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Favorite,
                        contentDescription = "Posts",
                        tint = PostAmber,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Publicaciones",
                        color = PostInk,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = "Parte guiada con /posts",
                        color = PostMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PostMetric(
                    icono = Icons.Outlined.Search,
                    titulo = "Total",
                    valor = total.toString(),
                    color = PostIndigo,
                    modifier = Modifier.weight(1f)
                )

                PostMetric(
                    icono = Icons.Outlined.FilterAlt,
                    titulo = "Vista",
                    valor = visibles.toString(),
                    color = PostCoral,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PostMetric(
    icono: ImageVector,
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = valor,
                    color = PostInk,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = titulo,
                    color = PostMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun PostFilterChip(
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
            PostAmber
        } else {
            Color.White.copy(alpha = 0.82f)
        }
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            color = if (seleccionado) PostInk else PostMuted,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun PostCardDeluxe(
    post: PostModel,
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
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = PostIndigo.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "#${post.id}",
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                        color = PostIndigo,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

                Text(
                    text = "Usuario ${post.userId}",
                    color = PostCoral,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = post.title.replaceFirstChar {
                    it.uppercaseChar()
                },
                color = PostInk,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.body,
                color = PostMuted,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ScreenPost(
    navController: NavHostController,
    servicio: PostApiService,
    id: Int
) {
    var post by remember {
        mutableStateOf<PostModel?>(null)
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

            post = servicio.getUserPostById(id)

        } catch (e: Exception) {
            Log.e("POSTS", "Error cargando post $id", e)
            mensajeError = "No se pudo cargar el post."
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
                        PostInk,
                        Color(0xFF263858),
                        PostPaper
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
                PostMessageCard(
                    mensaje = mensajeError!!,
                    accion = "Reintentar",
                    onClick = {
                        recargar++
                    }
                )
            }

            post != null -> {
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
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PostAmber.copy(alpha = 0.22f)
                            ) {
                                Text(
                                    text = "#${post!!.id}",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    color = PostInk,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Usuario ${post!!.userId}",
                                color = PostCoral,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = post!!.title.replaceFirstChar {
                                it.uppercaseChar()
                            },
                            color = PostInk,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = post!!.body,
                            color = PostMuted,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Button(
                            onClick = {
                                navController.navigate("posts") {
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PostInk
                            )
                        ) {
                            Text("Volver a posts")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostMessageCard(
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
                color = PostInk,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PostInk
                )
            ) {
                Text(accion)
            }
        }
    }
}
