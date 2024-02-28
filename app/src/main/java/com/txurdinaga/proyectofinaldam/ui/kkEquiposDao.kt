package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkEquiposDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(equipos: kkEquiposEntity)

    @Update
    fun update(equipos: kkEquiposEntity)

    @Delete
    fun delete(equipos: kkEquiposEntity)

    @Query("DELETE FROM equipos")
    fun deleteAll()

    @Query("SELECT * FROM equipos WHERE name = :equiposName")
    fun getEquiposByName(equiposName: String): List<kkEquiposEntity>

    @Query("SELECT * FROM equipos WHERE id = :id")
    fun getEquiposById(id: Int): kkEquiposEntity

    @Query("SELECT * FROM equipos")
    fun getAllEquipos(): List<kkEquiposEntity>

    @Query("SELECT COUNT(*) FROM equipos WHERE name = :equiposName")
    fun countEquiposByName(equiposName: String): Int

    @Query("SELECT * FROM equipos WHERE visible=true")
    fun getVisibleEquipos(): List<kkEquiposEntity>

}
