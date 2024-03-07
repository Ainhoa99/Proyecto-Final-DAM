package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageMetadata (
    val fileName: String,
    val teamId: Int?,
    val gallery: Boolean
)