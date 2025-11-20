package com.example.cocktails.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cocktails.model.Cocktail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailListScreen(
    cocktails: List<Cocktail>,
    onCocktailClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suggestions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (cocktails.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No cocktails found for these ingredients.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(cocktails) { cocktail ->
                    CocktailItem(cocktail = cocktail, onClick = { onCocktailClick(cocktail.id) })
                }
            }
        }
    }
}

@Composable
fun CocktailItem(cocktail: Cocktail, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cocktail.imageUrl,
                contentDescription = cocktail.name,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = cocktail.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "Rating: ${cocktail.rating}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = cocktail.ingredients.take(3).joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}
