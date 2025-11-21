package com.example.cocktails.data.source

import com.example.cocktails.model.Cocktail

interface CocktailDataSource {
    suspend fun searchCocktails(ingredients: List<String>): List<Cocktail>
    suspend fun searchCocktailsByName(name: String): List<Cocktail>
    suspend fun getCocktailById(id: String): Cocktail?
}
