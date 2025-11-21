package com.example.cocktails.data.source

import com.example.cocktails.data.network.CocktailApiService
import com.example.cocktails.data.network.model.DrinkDto
import com.example.cocktails.model.Cocktail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class TheCocktailDBDataSource(private val api: CocktailApiService) : CocktailDataSource {

    override suspend fun searchCocktails(userIngredients: List<String>): List<Cocktail> = withContext(Dispatchers.IO) {
        if (userIngredients.isEmpty()) return@withContext emptyList()

        val deferredResults = userIngredients.map { ingredient ->
            async {
                try {
                    api.searchByIngredient(ingredient).drinks ?: emptyList()
                } catch (e: Exception) {
                    emptyList<DrinkDto>()
                }
            }
        }
        val results = deferredResults.awaitAll()

        val cocktailCounts = mutableMapOf<String, Int>()
        val cocktailPreviews = mutableMapOf<String, DrinkDto>()

        results.flatten().forEach { drink ->
            cocktailCounts[drink.id] = cocktailCounts.getOrDefault(drink.id, 0) + 1
            cocktailPreviews[drink.id] = drink
        }

        val topCocktailIds = cocktailCounts.entries
            .sortedByDescending { it.value }
            .take(20)
            .map { it.key }

        val fullDetailsDeferred = topCocktailIds.map { id ->
            async {
                try {
                    api.lookupCocktailById(id).drinks?.firstOrNull()
                } catch (e: Exception) {
                    null
                }
            }
        }
        
        fullDetailsDeferred.awaitAll().filterNotNull().map { it.toDomainModel() }
    }

    override suspend fun getCocktailById(id: String): Cocktail? = withContext(Dispatchers.IO) {
        try {
            api.lookupCocktailById(id).drinks?.firstOrNull()?.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchCocktailsByName(name: String): List<Cocktail> {
        return try {
            val response = api.searchByName(name)
            response.drinks?.map { it.toDomainModel() } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun DrinkDto.toDomainModel(): Cocktail {
        val ingredients = listOfNotNull(
            strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
            strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
            strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15
        ).filter { it.isNotBlank() }

        return Cocktail(
            id = id,
            name = name,
            ingredients = ingredients,
            calories = (100..300).random(),
            description = instructions ?: "No description available.",
            history = "Category: $category, Glass: $glass",
            imageUrl = thumbnail ?: "",
            youtubeUrl = "https://www.youtube.com/results?search_query=${name.replace(" ", "+")}+cocktail+recipe",
            rating = 4.0 + (Math.random() * 1.0),
            popularity = (50..100).random()
        )
    }
}
