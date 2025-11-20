package com.example.cocktails.model

data class Cocktail(
    val id: String,
    val name: String,
    val ingredients: List<String>,
    val calories: Int,
    val description: String,
    val history: String,
    val imageUrl: String,
    val youtubeUrl: String,
    val rating: Double, // 0.0 to 5.0
    val popularity: Int // Higher is more popular
)
