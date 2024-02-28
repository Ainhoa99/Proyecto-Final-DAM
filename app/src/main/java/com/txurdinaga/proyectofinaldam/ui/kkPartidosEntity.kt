package com.txurdinaga.proyectofinaldam.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partido")
data class kkPartidosEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "id_equipo_1")
    var id_equipo1: Int,

    @ColumnInfo(name = "id_equipo_2")
    var id_equipo2: Int,

    @ColumnInfo(name = "puntos_equipo_1")
    var puntos1: Int?,

    @ColumnInfo(name = "puntos_equipo_2")
    var puntos2: Int?,

    @ColumnInfo(name = "fecha")
    var fecha: Long,

    @ColumnInfo(name = "hora")
    var hora: Long, // Cambiado a Long, ya que Time no es la mejor opción

    @ColumnInfo(name = "campo_donde_jugar")
    var local: String
) {
    override fun toString(): String {
        return "$id: $id_equipo1 vs $id_equipo2, $puntos1 - $puntos2, $fecha, $hora, $local"
    }
}