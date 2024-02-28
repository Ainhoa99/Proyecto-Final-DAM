package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkLigasDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ligas: kkLigasEntity)

    @Update
    fun update(ligas: kkLigasEntity)

    @Delete
    fun delete(ligas: kkLigasEntity)

    @Query("SELECT * FROM ligas WHERE name = :teacherName")
    fun getLigaByName(teacherName: String): List<kkLigasEntity>

    @Query("SELECT * FROM ligas WHERE id = :id")
    fun getLigaById(id: Int): kkLigasEntity

    @Query("SELECT * FROM ligas")
    fun getAllLigas(): List<kkLigasEntity>

    @Query("SELECT COUNT(*) FROM ligas WHERE name = :ligasName")
    fun countLigasByName(ligasName: String): Int

}