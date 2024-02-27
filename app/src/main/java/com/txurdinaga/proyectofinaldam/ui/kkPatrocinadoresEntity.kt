package com.txurdinaga.proyectofinaldam.ui

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patrocinadores")
data class kkPatrocinadoresEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "foto")
    var foto: String,

    @ColumnInfo(name = "isPatrocinador")
    var isPatrocinador: Boolean,

    @ColumnInfo(name = "activo")
    var activo: Boolean,

    @ColumnInfo(name = "dinero")
    var dinero: Double
)
