package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkPatrocinadoresDao {
    @Insert
    fun insert(patrocinadores: kkPatrocinadoresEntity)

    @Update
    fun update(patrocinadores: kkPatrocinadoresEntity)

    @Delete
    fun delete(patrocinadores: kkPatrocinadoresEntity)

    @Query("SELECT * FROM patrocinadores WHERE name = :patrocinadoresName")
    fun getTeacherByName(patrocinadoresName: String): kkPatrocinadoresEntity

    @Query("SELECT * FROM patrocinadores")
    fun getAllTeachers(): List<kkPatrocinadoresEntity>

    @Query("SELECT COUNT(*) FROM patrocinadores WHERE name = :patrocinadoresName")
    fun countTeachersByName(patrocinadoresName: String): Int

}