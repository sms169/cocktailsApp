package com.example.cocktails.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cocktails.ui.screens.CocktailDetailScreen
import com.example.cocktails.ui.screens.CocktailListScreen
import com.example.cocktails.ui.screens.IngredientInputScreen
import com.example.cocktails.ui.screens.SplashScreen
import com.example.cocktails.ui.theme.CocktailAppTheme

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.navigation.NavGraph.Companion.findStartDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CocktailAppTheme {
                val navController = rememberNavController()
                val viewModel: CocktailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                
                val favorites by viewModel.favorites.collectAsState()
                val favoriteIds = favorites.map { it.id }.toSet()

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val currentRoute = currentDestination?.route

                        // Only show bottom bar on main screens
                        if (currentRoute == "input" || currentRoute == "favorites") {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                    label = { Text("Search") },
                                    selected = currentRoute == "input",
                                    onClick = {
                                        navController.navigate("input") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                                    label = { Text("Favorites") },
                                    selected = currentRoute == "favorites",
                                    onClick = {
                                        navController.navigate("favorites") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController, 
                        startDestination = "splash",
                        modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onAnimationFinished = {
                                    navController.navigate("input") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("input") {
                            IngredientInputScreen(
                                currentDataSource = viewModel.currentDataSource,
                                onDataSourceSelected = { viewModel.setDataSource(it) },
                                onFindCocktails = { ingredients ->
                                    viewModel.searchCocktails(ingredients)
                                    val ingredientsString = ingredients.joinToString(",")
                                    navController.navigate("list/$ingredientsString")
                                },
                                onFindCocktailsByName = { name ->
                                    viewModel.searchCocktailsByName(name)
                                    navController.navigate("list/name:$name")
                                }
                            )
                        }
                        composable("favorites") {
                            com.example.cocktails.ui.screens.FavoritesScreen(
                                favorites = favorites,
                                onCocktailClick = { cocktailId ->
                                    navController.navigate("detail/$cocktailId")
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }
                        composable("list/{ingredients}") { backStackEntry ->
                            if (viewModel.isLoading) {
                                androidx.compose.foundation.layout.Box(
                                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    androidx.compose.material3.CircularProgressIndicator()
                                }
                            } else {
                                CocktailListScreen(
                                    cocktails = viewModel.cocktails,
                                    favoriteIds = favoriteIds,
                                    onCocktailClick = { cocktailId ->
                                        navController.navigate("detail/$cocktailId")
                                    },
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                        composable("detail/{cocktailId}") { backStackEntry ->
                            val cocktailId = backStackEntry.arguments?.getString("cocktailId")
                            var cocktail by remember { mutableStateOf<com.example.cocktails.model.Cocktail?>(null) }
                            
                            LaunchedEffect(cocktailId) {
                                if (cocktailId != null) {
                                    cocktail = viewModel.getCocktailById(cocktailId)
                                }
                            }
    
                            if (cocktail != null) {
                                CocktailDetailScreen(
                                    cocktail = cocktail!!,
                                    onBack = { navController.popBackStack() }
                                )
                            } else {
                                 androidx.compose.foundation.layout.Box(
                                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                                    contentAlignment = androidx.compose.ui.Alignment.Center
                                ) {
                                    androidx.compose.material3.CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
