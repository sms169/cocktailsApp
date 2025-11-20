package com.example.cocktails.data

import com.example.cocktails.data.network.CocktailApiService
import com.example.cocktails.data.source.CocktailDataSource
import com.example.cocktails.data.source.GeminiDataSource
import com.example.cocktails.data.source.TheCocktailDBDataSource
import com.example.cocktails.model.Cocktail
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

enum class DataSourceType {
    THE_COCKTAIL_DB,
    GEMINI
}

class CocktailRepository {

    private val theCocktailDBDataSource: TheCocktailDBDataSource
    private val geminiDataSource: GeminiDataSource
    
    // Default to TheCocktailDB
    var currentDataSourceType: DataSourceType = DataSourceType.THE_COCKTAIL_DB
        private set

    // Cache Gemini results because we can't re-fetch by ID easily
    private val geminiCache = mutableMapOf<String, Cocktail>()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(CocktailApiService::class.java)
        
        theCocktailDBDataSource = TheCocktailDBDataSource(api)
        geminiDataSource = GeminiDataSource()
    }

    fun setDataSource(type: DataSourceType) {
        currentDataSourceType = type
    }

    suspend fun searchCocktails(userIngredients: List<String>): List<Cocktail> {
        return when (currentDataSourceType) {
            DataSourceType.THE_COCKTAIL_DB -> theCocktailDBDataSource.searchCocktails(userIngredients)
            DataSourceType.GEMINI -> {
                val results = geminiDataSource.searchCocktails(userIngredients)
                results.forEach { geminiCache[it.id] = it }
                results
            }
        }
    }

    suspend fun getCocktailById(id: String): Cocktail? {
        return when (currentDataSourceType) {
            DataSourceType.THE_COCKTAIL_DB -> theCocktailDBDataSource.getCocktailById(id)
            DataSourceType.GEMINI -> geminiCache[id]
        }
    }
}
