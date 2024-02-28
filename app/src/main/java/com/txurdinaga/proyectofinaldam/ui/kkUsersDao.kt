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

    @Update
    fun update(user: kkUsersEntity)

}
