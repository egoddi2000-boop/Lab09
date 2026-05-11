package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val Ink = Color(0xFF172033)
private val Indigo = Color(0xFF304FFE)
private val Teal = Color(0xFF00897B)
private val Amber = Color(0xFFFFB300)
private val Paper = Color(0xFFF7F8FC)
private val Muted = Color(0xFF6E7787)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProgPrincipal9()
            }
        }
    }
}

@Composable
fun ProgPrincipal9() {
    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val servicioPosts = remember { retrofit.create(PostApiService::class.java) }
    val servicioUsers = remember { retrofit.create(UserApiService::class.java) }
    val navController = rememberNavController()

    Scaffold(
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        containerColor = Paper
    ) { paddingValues ->
        Contenido(paddingValues, navController, servicioPosts, servicioUsers)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Lab09 API Studio",
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Ink,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "inicio"

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == "inicio",
            onClick = {
                navController.navigate("inicio") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = currentRoute.startsWith("posts"),
            onClick = {
                navController.navigate("posts") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Posts") },
            label = { Text("Posts") }
        )

        NavigationBarItem(
            selected = currentRoute.startsWith("users"),
            onClick = {
                navController.navigate("users") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Users") },
            label = { Text("Users") }
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    servicioPosts: PostApiService,
    servicioUsers: UserApiService
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        NavHost(
            navController = navController,
            startDestination = "inicio"
        ) {
            composable("inicio") {
                ScreenInicio(navController)
            }

            composable("posts") {
                ScreenPosts(navController, servicioPosts)
            }

            composable(
                route = "postsVer/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    }
                )
            ) {
                val id = it.arguments!!.getInt("id")
                ScreenPost(navController, servicioPosts, id)
            }

            composable("users") {
                ScreenUsers(navController, servicioUsers)
            }

            composable(
                route = "usersVer/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    }
                )
            ) {
                val id = it.arguments!!.getInt("id")
                ScreenUserDetalle(navController, servicioUsers, id)
            }
        }
    }
}

@Composable
fun ScreenInicio(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Ink,
                        Color(0xFF23304A),
                        Paper
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Amber
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = "Destacado",
                    tint = Ink,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Laboratorio 09",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Retrofit + Jetpack Compose + navegacion",
                color = Color.White.copy(alpha = 0.88f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniMetric(
                    icono = Icons.Outlined.Verified,
                    titulo = "API",
                    valor = "REST",
                    color = Teal,
                    modifier = Modifier.weight(1f)
                )

                MiniMetric(
                    icono = Icons.Outlined.Search,
                    titulo = "UI",
                    valor = "Search",
                    color = Indigo,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HomeOptionCard(
                icono = Icons.Outlined.Favorite,
                titulo = "Parte guiada: Posts",
                descripcion = "Buscador, resumen, tarjetas y detalle de publicaciones.",
                color = Color(0xFFFFF8E1),
                accent = Amber,
                onClick = {
                    navController.navigate("posts")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeOptionCard(
                icono = Icons.Outlined.Person,
                titulo = "Ejercicio 1: Users",
                descripcion = "Directorio con avatars, filtros, metricas y detalle completo.",
                color = Color.White,
                accent = Teal,
                onClick = {
                    navController.navigate("users")
                }
            )
        }
    }
}

@Composable
fun MiniMetric(
    icono: ImageVector,
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.94f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = color,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = valor,
                    color = Ink,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = titulo,
                    color = Muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun HomeOptionCard(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    color: Color,
    accent: Color,
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
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accent.copy(alpha = 0.16f)
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = accent,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = titulo,
                    color = Ink,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = descripcion,
                    color = Muted,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
