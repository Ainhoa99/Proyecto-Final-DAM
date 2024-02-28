package com.txurdinaga.proyectofinaldam.ui

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = kkEquiposEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = kkOcupacionesEntity::class,
            parentColumns = ["id"],
            childColumns = ["ocupacionId"],
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)

data class kkUsersEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "foto")
    var foto: String?,

    @ColumnInfo(name = "nombre")
    @NonNull
    var nombre: String,

    @ColumnInfo(name = "apellido")
    @NonNull
    var apellido: String,

    @ColumnInfo(name = "mail")
    @NonNull
    var mail: String,

    @ColumnInfo(name = "password")
    @NonNull
    var password: String,

    @ColumnInfo(name = "fecha_nacimiento")
    @NonNull
    var fecha_nacimiento: String,

    @ColumnInfo(name = "equipoId")
    @NonNull
    var equipoId: Int,

    @ColumnInfo(name = "ocupacionId")
    @NonNull
    var ocupacionId: Int,

    @ColumnInfo(name = "admin")
    @NonNull
    var admin: Boolean,

    @ColumnInfo(name = "activo")
    @NonNull
    var activo: Boolean,



    )

