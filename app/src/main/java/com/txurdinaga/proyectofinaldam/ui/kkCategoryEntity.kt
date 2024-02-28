package com.txurdinaga.proyectofinaldam.ui

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class kkCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NonNull
    var id: Int = 0,

    @ColumnInfo(name = "name")
    @NonNull
    var name: String,
)
{
    override fun toString(): String {
        return name
    }
}