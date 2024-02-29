package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO OCUPACIÓN
 */
@Serializable
data class Role(
    val roleId: Int? = null,
    val roleName: String? = null
)