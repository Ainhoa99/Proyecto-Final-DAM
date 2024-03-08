package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO OCUPACIÓN
 */
@Serializable
data class Role(
    val roleId: Int,
    var roleName: String
)

{
    override fun toString(): String {
        return roleName
    }
}