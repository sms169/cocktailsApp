package com.example.cocktails.data.network

import com.example.cocktails.data.network.model.DrinkResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApiService {
    @GET("filter.php")
    suspend fun searchByIngredient(@Query("i") ingredient: String): DrinkResponse

    @GET("lookup.php")
    suspend fun lookupCocktailById(@Query("i") id: String): DrinkResponse

    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): DrinkResponse
}
