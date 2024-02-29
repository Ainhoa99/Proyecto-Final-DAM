package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO CATEGORÍAS
 */
@Serializable
data class Category(
    val categoryId: Int? = null,
    var categoryName: String? = null
)
