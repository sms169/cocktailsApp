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

class CocktailViewModel : ViewModel() {

    private val repository = CocktailRepository()

    var cocktails by mutableStateOf<List<Cocktail>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentDataSource by mutableStateOf(DataSourceType.THE_COCKTAIL_DB)
        private set

    // Cache the last search ingredients to avoid re-fetching
    private var lastIngredients: List<String> = emptyList()

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

    suspend fun getCocktailById(id: String): Cocktail? {
        return repository.getCocktailById(id)
    }
}
