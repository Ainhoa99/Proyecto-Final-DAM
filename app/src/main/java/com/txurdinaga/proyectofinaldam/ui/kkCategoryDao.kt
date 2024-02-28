package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: kkCategoryEntity)

    @Update
    fun update(category: kkCategoryEntity)

    @Delete
    fun delete(category: kkCategoryEntity)

    @Query("SELECT * FROM category WHERE name = :categoryName")
    fun getCategoryByName(categoryName: String): kkCategoryEntity

    @Query("SELECT * FROM category")
    fun getAllCategorias(): List<kkCategoryEntity>

    @Query("SELECT COUNT(*) FROM category WHERE name = :categoryName")
    fun countCategoryByName(categoryName: String): Int

}