package com.txurdinaga.proyectofinaldam.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "equipos",
    foreignKeys = [
        ForeignKey(
            entity = kkCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoria"],
            onDelete = ForeignKey.SET_NULL),
        ForeignKey(
            entity = kkLigasEntity::class,
            parentColumns = ["id"],
            childColumns = ["liga"],
            onDelete = ForeignKey.SET_NULL)
    ]
)
data class kkEquiposEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "campo")
    var campo: String?,

    @ColumnInfo(name = "categoria")
    var categoria: Long?,

    @ColumnInfo(name = "liga")
    var liga: Long?,

    @ColumnInfo(name = "escudo")
    var escudo: String?,

    @ColumnInfo(name = "isUnkina")
    var isUnkina: Boolean,

    @ColumnInfo(name = "visible")
    var visible: Boolean

) {
    override fun toString(): String {
        return name
    }
}