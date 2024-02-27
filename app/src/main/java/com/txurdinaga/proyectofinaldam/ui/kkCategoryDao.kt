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
    fun getTeacherByName(categoryName: String): kkCategoryEntity

    @Query("SELECT * FROM category")
    fun getAllTeachers(): List<kkCategoryEntity>

    @Query("SELECT COUNT(*) FROM category WHERE name = :categoryName")
    fun countTeachersByName(categoryName: String): Int

}