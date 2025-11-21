package com.example.cocktails.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cocktails.data.DataSourceType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientInputScreen(
    currentDataSource: DataSourceType,
    onDataSourceSelected: (DataSourceType) -> Unit,
    onFindCocktails: (List<String>) -> Unit,
    onFindCocktailsByName: (String) -> Unit
) {
    var currentIngredient by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf(listOf<String>()) }
    val maxIngredients = 5

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cocktail Finder") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "Add up to 5 ingredients",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var expanded by remember { mutableStateOf(false) }
                val filteredIngredients = com.example.cocktails.data.CommonIngredients.list.filter {
                    it.contains(currentIngredient, ignoreCase = true) && it != currentIngredient
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = currentIngredient,
                        onValueChange = { 
                            currentIngredient = it
                            expanded = true
                        },
                        label = { Text("Ingredient") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    if (filteredIngredients.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.heightIn(max = 200.dp)
                        ) {
                            filteredIngredients.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        currentIngredient = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (currentIngredient.isNotBlank() && ingredients.size < maxIngredients) {
                            ingredients = ingredients + currentIngredient.trim()
                            currentIngredient = ""
                        }
                    },
                    enabled = currentIngredient.isNotBlank() && ingredients.size < maxIngredients
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(ingredients) { ingredient ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = ingredient, style = MaterialTheme.typography.bodyLarge)
                        IconButton(onClick = { ingredients = ingredients - ingredient }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                    Divider()
                }
            }
            


            Button(
                onClick = { onFindCocktails(ingredients) },
                enabled = ingredients.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Find Cocktails")
            }

            Spacer(modifier = Modifier.height(24.dp))
        
        Divider()
        
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Or Search by Name",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var nameExpanded by remember { mutableStateOf(false) }
            var cocktailName by remember { mutableStateOf("") }
            val filteredNames = com.example.cocktails.data.TopCocktails.list.filter {
                it.contains(cocktailName, ignoreCase = true) && it != cocktailName
            }

            ExposedDropdownMenuBox(
                expanded = nameExpanded,
                onExpandedChange = { nameExpanded = !nameExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = cocktailName,
                    onValueChange = { 
                        cocktailName = it
                        nameExpanded = true
                    },
                    label = { Text("Cocktail Name") },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nameExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                if (filteredNames.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = nameExpanded,
                        onDismissRequest = { nameExpanded = false },
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        filteredNames.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    cocktailName = selectionOption
                                    nameExpanded = false
                                    onFindCocktailsByName(selectionOption)
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            

            Button(
                onClick = { onFindCocktailsByName(cocktailName) },
                enabled = cocktailName.isNotBlank()
            ) {
                Text("Search")
            }
        }

            Spacer(modifier = Modifier.weight(1f)) // Push to bottom if needed, or just spacer
            Spacer(modifier = Modifier.height(24.dp))

            Text("Data Source:", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = currentDataSource == DataSourceType.THE_COCKTAIL_DB,
                    onClick = { onDataSourceSelected(DataSourceType.THE_COCKTAIL_DB) }
                )
                Text("TheCocktailDB")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = currentDataSource == DataSourceType.GEMINI,
                    onClick = { onDataSourceSelected(DataSourceType.GEMINI) }
                )
                Text("Gemini")
            }
        }
    }
}
