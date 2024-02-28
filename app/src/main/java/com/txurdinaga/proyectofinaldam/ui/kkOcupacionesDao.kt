package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkOcupacionesDao {
    @Insert
    fun insert(ocupaciones: kkOcupacionesEntity)

    @Update
    fun update(ocupaciones: kkOcupacionesEntity)

    @Delete
    fun delete(ocupaciones: kkOcupacionesEntity)


    @Query("SELECT * FROM ocupaciones WHERE name = :name")
    fun getOcupacionByName(name: String): List<kkOcupacionesEntity>

    @Query("SELECT * FROM ocupaciones WHERE id = :id")
    fun getOcupacionById(id: Int): kkOcupacionesEntity


    @Query("SELECT * FROM ocupaciones")
    fun getAllOcupaciones(): List<kkOcupacionesEntity>

}