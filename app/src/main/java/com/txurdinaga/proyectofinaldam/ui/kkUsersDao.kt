package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface kkUsersDao {
    @Insert
    fun insert(user: kkUsersEntity)

    @Delete
    fun delete(user: kkUsersEntity)

    @Update
    fun update(user: kkUsersEntity)

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<kkUsersEntity>

    @Query("SELECT * FROM users WHERE equipoId = :equipoId")
    fun getUsersByEquipo(equipoId:Int): List<kkUsersEntity>

}
