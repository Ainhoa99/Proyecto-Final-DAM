package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface kkPartidosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarPartido(partido: kkPartidosEntity)

    @Query("SELECT * FROM partido")
    fun getAllPartidos(): List<kkPartidosEntity>

}
