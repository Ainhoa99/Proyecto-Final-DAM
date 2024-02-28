package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int? = null, // TODO Almacenar el token junto al usuario o siempre fuera?
    val name: String? = null
)
