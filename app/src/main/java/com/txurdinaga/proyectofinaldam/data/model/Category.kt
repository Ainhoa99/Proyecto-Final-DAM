package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int? = null,
    val name: String? = null
)
