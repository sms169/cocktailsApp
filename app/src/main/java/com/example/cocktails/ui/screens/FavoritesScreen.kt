package com.example.cocktails.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.cocktails.model.Cocktail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favorites: List<Cocktail>,
    onCocktailClick: (String) -> Unit,
    onToggleFavorite: (Cocktail) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") }
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorites yet.")
            }
        } else {
            // Reuse the list logic, but we need to adapt CocktailListScreen or just duplicate the list part.
            // Since CocktailListScreen has a specific TopBar with back button, let's just use the list part.
            // Actually, CocktailListScreen is coupled with "Suggestions" title and back button.
            // Let's refactor CocktailListScreen to be more reusable or just copy the list part here.
            // For simplicity/speed, I'll just use the LazyColumn part here.
            
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(favorites.size) { index ->
                    val cocktail = favorites[index]
                    CocktailItem(
                        cocktail = cocktail,
                        isFavorite = true, // Always favorite in this screen
                        onClick = { onCocktailClick(cocktail.id) },
                        onToggleFavorite = { onToggleFavorite(cocktail) }
                    )
                }
            }
        }
    }
}
