package com.example.cocktails.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.cocktails.analytics.AnalyticsLogger
import com.example.cocktails.analytics.LogcatAnalyticsLogger
import com.example.cocktails.data.CocktailRepository
import com.example.cocktails.data.DataSourceType
import com.example.cocktails.data.db.AppDatabase
import com.example.cocktails.model.Cocktail
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CocktailViewModel(application: Application) : AndroidViewModel(application) {

    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "cocktails-db"
    ).build()

    private val repository = CocktailRepository(database.cocktailDao())
    private val analyticsLogger: AnalyticsLogger = LogcatAnalyticsLogger()

    var cocktails by mutableStateOf<List<Cocktail>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentDataSource by mutableStateOf(DataSourceType.THE_COCKTAIL_DB)
        private set

    // Cache the last search ingredients to avoid re-fetching
    private var lastIngredients: List<String> = emptyList()

    val favorites: StateFlow<List<Cocktail>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDataSource(type: DataSourceType) {
        currentDataSource = type
        repository.setDataSource(type)
        analyticsLogger.logEvent("change_data_source", mapOf("source" to type.name))
    }

    fun searchCocktails(ingredients: List<String>) {
        // If ingredients haven't changed and we have results, don't re-fetch
        if (ingredients == lastIngredients && cocktails.isNotEmpty()) {
            return
        }

        analyticsLogger.logEvent("search_cocktails", mapOf("ingredients" to ingredients.joinToString(",")))

        lastIngredients = ingredients
        isLoading = true
        viewModelScope.launch {
            cocktails = repository.searchCocktails(ingredients)
            isLoading = false
        }
    }

    fun searchCocktailsByName(name: String) {
        analyticsLogger.logEvent("search_cocktails_by_name", mapOf("name" to name))
        isLoading = true
        viewModelScope.launch {
            cocktails = repository.searchCocktailsByName(name)
            isLoading = false
        }
    }

    suspend fun getCocktailById(id: String): Cocktail? {
        return repository.getCocktailById(id)
    }

    fun toggleFavorite(cocktail: Cocktail) {
        viewModelScope.launch {
            if (isFavorite(cocktail.id)) {
                repository.removeFromFavorites(cocktail)
                analyticsLogger.logEvent("remove_from_favorites", mapOf("cocktail_id" to cocktail.id, "cocktail_name" to cocktail.name))
            } else {
                repository.addToFavorites(cocktail)
                analyticsLogger.logEvent("add_to_favorites", mapOf("cocktail_id" to cocktail.id, "cocktail_name" to cocktail.name))
            }
        }
    }

    private fun isFavorite(id: String): Boolean {
        return favorites.value.any { it.id == id }
    }
    
    fun isFavoriteFlow(id: String): StateFlow<Boolean> {
         return repository.isFavorite(id)
             .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }
    
    fun logEvent(eventName: String, params: Map<String, Any>? = null) {
        analyticsLogger.logEvent(eventName, params)
    }
}
