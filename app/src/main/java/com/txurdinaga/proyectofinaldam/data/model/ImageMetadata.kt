package com.txurdinaga.proyectofinaldam.data.model

import com.bumptech.glide.load.model.GlideUrl
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ImageMetadata (
    val fileName: String,
    val teamId: Int?,
    val gallery: Boolean,
    val imageId: String? = null,
    @Contextual
    var url: GlideUrl? = null
)