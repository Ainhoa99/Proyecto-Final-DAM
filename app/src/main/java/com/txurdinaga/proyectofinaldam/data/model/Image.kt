package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Image (
    val fileName: String,
    val teamId: Int? = null,
    val gallery: Boolean = false
)