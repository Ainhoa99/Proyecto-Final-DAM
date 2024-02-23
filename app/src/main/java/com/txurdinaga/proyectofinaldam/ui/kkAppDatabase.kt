package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [kkEquiposEntity::class, kkCategoryEntity::class, kkLigasEntity::class], version = 3, exportSchema = false)
abstract class kkAppDatabase: RoomDatabase() {
    abstract val kkequipostDao: kkEquiposDao
    abstract val kkcategoryDao: kkCategoryDao
    abstract val kkligasDao: kkLigasDao

    companion object {
        const val DATABASE_NAME = "db-asig-prof"
    }
}