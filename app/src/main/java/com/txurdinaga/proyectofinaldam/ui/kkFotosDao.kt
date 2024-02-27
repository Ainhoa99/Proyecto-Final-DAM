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

    @Query("SELECT DISTINCT (temporada) FROM fotos ORDER BY temporada DESC")
    fun getTemporadas(): List<String>

    @Query("SELECT * FROM fotos WHERE equipoId = :equipoId")
    fun getFotosByEquipo(equipoId:Int): List<kkFotosEntity>

    @Query("SELECT * FROM fotos WHERE temporada = :temporada AND galeria=true")
    fun getFotosByTemporada(temporada:String): List<kkFotosEntity>
}
