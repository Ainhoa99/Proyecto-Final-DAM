package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkFotosDao {
    @Insert
    fun insert(foto: kkFotosEntity)

    @Update
    fun update(foto: kkFotosEntity)

    @Query("SELECT * FROM fotos")
    fun getAllFotos(): List<kkFotosEntity>

    @Query("SELECT DISTINCT (temporada) FROM fotos")
    fun getTemporadas(): List<String>
}
