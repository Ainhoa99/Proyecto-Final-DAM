package com.txurdinaga.proyectofinaldam.ui

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [kkEquiposEntity::class, kkCategoryEntity::class, kkLigasEntity::class, kkFotosEntity::class, kkPatrocinadoresEntity::class, kkPartidosEntity::class, kkOcupacionesEntity::class, kkUsersEntity::class], version = 3, exportSchema = false)
abstract class kkAppDatabase: RoomDatabase() {
    abstract val kkequipostDao: kkEquiposDao
    abstract val kkcategoryDao: kkCategoryDao
    abstract val kkligasDao: kkLigasDao
    abstract val kkfotosDao: kkFotosDao
    abstract val kkPatrocinadoresDao: kkPatrocinadoresDao
    abstract val kkpartidosDao: kkPartidosDao
    abstract val kkOcupacionesDao: kkOcupacionesDao
    abstract val kkUsersDao: kkUsersDao


    companion object {
        const val DATABASE_NAME = "db-asig-prof"
    }
}