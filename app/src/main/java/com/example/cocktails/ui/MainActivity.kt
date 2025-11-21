package com.example.cocktails.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cocktails.ui.screens.CocktailDetailScreen
import com.example.cocktails.ui.screens.CocktailListScreen
import com.example.cocktails.ui.screens.IngredientInputScreen
import com.example.cocktails.ui.screens.SplashScreen
import com.example.cocktails.ui.theme.CocktailAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CocktailAppTheme {
                val navController = rememberNavController()
                val viewModel: CocktailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                NavHost(navController = navController, startDestination = "splash") {
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
                    composable("list/{ingredients}") { backStackEntry ->
                        // We trigger search in the input screen, but just in case of deep link or direct nav, we could check here.
                        // For now, we rely on the ViewModel state.
                        
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
