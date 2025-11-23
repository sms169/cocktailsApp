package com.example.cocktails.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.cocktails.model.Cocktail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "favorites")
@TypeConverters(Converters::class)
data class CocktailEntity(
    @PrimaryKey val id: String,
    val name: String,
    val ingredients: List<String>,
    val calories: Int,
    val description: String,
    val history: String,
    val imageUrl: String,
    val youtubeUrl: String,
    val rating: Double,
    val popularity: Int
) {
    fun toDomainModel(): Cocktail {
        return Cocktail(
            id = id,
            name = name,
            ingredients = ingredients,
            calories = calories,
            description = description,
            history = history,
            imageUrl = imageUrl,
            youtubeUrl = youtubeUrl,
            rating = rating,
            popularity = popularity
        )
    }

    companion object {
        fun fromDomainModel(cocktail: Cocktail): CocktailEntity {
            return CocktailEntity(
                id = cocktail.id,
                name = cocktail.name,
                ingredients = cocktail.ingredients,
                calories = cocktail.calories,
                description = cocktail.description,
                history = cocktail.history,
                imageUrl = cocktail.imageUrl,
                youtubeUrl = cocktail.youtubeUrl,
                rating = cocktail.rating,
                popularity = cocktail.popularity
            )
        }
    }
}

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }
}
