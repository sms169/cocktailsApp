package com.example.cocktails.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocktails.data.CocktailRepository
import com.example.cocktails.data.DataSourceType
import com.example.cocktails.ui.screens.CocktailDetailScreen
import com.example.cocktails.ui.screens.CocktailListScreen
import com.example.cocktails.ui.screens.IngredientInputScreen
import com.example.cocktails.ui.theme.CocktailAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CocktailAppTheme {
                val navController = rememberNavController()
                val repository = remember { CocktailRepository() }

                NavHost(navController = navController, startDestination = "input") {
                    composable("input") {
                        var currentDataSource by remember { mutableStateOf(DataSourceType.THE_COCKTAIL_DB) }
                        
                        IngredientInputScreen(
                            currentDataSource = currentDataSource,
                            onDataSourceSelected = { 
                                currentDataSource = it
                                repository.setDataSource(it)
                            },
                            onFindCocktails = { ingredients ->
                                val ingredientsString = ingredients.joinToString(",")
                                navController.navigate("list/$ingredientsString")
                            }
                        )
                    }
                    composable("list/{ingredients}") { backStackEntry ->
                        val ingredientsString = backStackEntry.arguments?.getString("ingredients") ?: ""
                        val ingredients = ingredientsString.split(",").filter { it.isNotEmpty() }
                        
                        // State to hold the list of cocktails
                        var cocktails by remember { mutableStateOf<List<com.example.cocktails.model.Cocktail>>(emptyList()) }
                        var isLoading by remember { mutableStateOf(true) }

                        // Fetch cocktails when ingredients change
                        LaunchedEffect(ingredients) {
                            isLoading = true
                            cocktails = repository.searchCocktails(ingredients)
                            isLoading = false
                        }

                        if (isLoading) {
                            androidx.compose.foundation.layout.Box(
                                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator()
                            }
                        } else {
                            CocktailListScreen(
                                cocktails = cocktails,
                                onCocktailClick = { cocktailId ->
                                    navController.navigate("detail/$cocktailId")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                    composable("detail/{cocktailId}") { backStackEntry ->
                        val cocktailId = backStackEntry.arguments?.getString("cocktailId")
                        var cocktail by remember { mutableStateOf<com.example.cocktails.model.Cocktail?>(null) }
                        
                        LaunchedEffect(cocktailId) {
                            if (cocktailId != null) {
                                cocktail = repository.getCocktailById(cocktailId)
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
