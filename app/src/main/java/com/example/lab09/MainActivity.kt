package com.example.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    val servicioPosts = remember {
        retrofit.create(PostApiService::class.java)
    }

    val servicioUsers = remember {
        retrofit.create(UserApiService::class.java)
    }

    val navController = rememberNavController()

    Scaffold(
        topBar = {
            BarraSuperior()
        },
        bottomBar = {
            BarraInferior(navController)
        }
    ) { paddingValues ->
        Contenido(
            pv = paddingValues,
            navController = navController,
            servicioPosts = servicioPosts,
            servicioUsers = servicioUsers
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Lab09 API Explorer",
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF0D47A1),
            titleContentColor = Color.White
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "inicio"

    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentRoute == "inicio",
            onClick = {
                navController.navigate("inicio") {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(Icons.Outlined.Home, contentDescription = "Inicio")
            },
            label = {
                Text("Inicio")
            }
        )

        NavigationBarItem(
            selected = currentRoute.startsWith("posts"),
            onClick = {
                navController.navigate("posts") {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(Icons.Outlined.Favorite, contentDescription = "Posts")
            },
            label = {
                Text("Posts")
            }
        )

        NavigationBarItem(
            selected = currentRoute.startsWith("users"),
            onClick = {
                navController.navigate("users") {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(Icons.Outlined.Person, contentDescription = "Users")
            },
            label = {
                Text("Users")
            }
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
                        Color(0xFF0D47A1),
                        Color(0xFF1976D2),
                        Color(0xFFE3F2FD)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Laboratorio 09",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Consumo de API REST con Retrofit y Jetpack Compose",
                color = Color.White.copy(alpha = 0.92f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            HomeOptionCard(
                icono = Icons.Outlined.Favorite,
                titulo = "Parte guiada: Posts",
                descripcion = "Lista y detalle usando el endpoint /posts.",
                color = Color(0xFFFFF3E0),
                onClick = {
                    navController.navigate("posts")
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            HomeOptionCard(
                icono = Icons.Outlined.Person,
                titulo = "Ejercicio 1: Users",
                descripcion = "Buscador, filtros, avatars y detalle premium.",
                color = Color.White,
                onClick = {
                    navController.navigate("users")
                }
            )
        }
    }
}

@Composable
fun HomeOptionCard(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = Color(0xFF0D47A1),
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = titulo,
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = descripcion,
                    color = Color(0xFF6B7280),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
