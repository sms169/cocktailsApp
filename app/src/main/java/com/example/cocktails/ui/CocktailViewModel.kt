package com.example.cocktails.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cocktails.data.CocktailRepository
import com.example.cocktails.data.DataSourceType
import com.example.cocktails.model.Cocktail
import kotlinx.coroutines.launch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
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
    }

    fun searchCocktails(ingredients: List<String>) {
        // If ingredients haven't changed and we have results, don't re-fetch
        if (ingredients == lastIngredients && cocktails.isNotEmpty()) {
            return
        }

        lastIngredients = ingredients
        isLoading = true
        viewModelScope.launch {
            cocktails = repository.searchCocktails(ingredients)
            isLoading = false
        }
    }

    fun searchCocktailsByName(name: String) {
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
            } else {
                repository.addToFavorites(cocktail)
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
}
