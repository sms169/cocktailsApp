package com.example.cocktails.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CocktailDao {
    @Query("SELECT * FROM favorites")
    fun getFavorites(): Flow<List<CocktailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cocktail: CocktailEntity)

    @Delete
    suspend fun delete(cocktail: CocktailEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>
}
