package com.txurdinaga.proyectofinaldam.ui

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "fotos",
    foreignKeys = [ForeignKey(entity = kkEquiposEntity::class, parentColumns = ["id"], childColumns = ["equipoId"], onDelete = ForeignKey.SET_NULL)])

data class kkFotosEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "title")
    @NonNull
    var title: String,

    @ColumnInfo(name = "temporada")
    @NonNull
    var temporada: String,

    @ColumnInfo(name = "equipoId")
    var equipoId: Int?,

    @ColumnInfo(name = "galeria")
    @NonNull
    var galeria: Boolean,

    )