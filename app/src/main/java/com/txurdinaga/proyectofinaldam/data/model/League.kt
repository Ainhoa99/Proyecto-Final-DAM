package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO LIGAS
 */
@Serializable
data class League(
    val leagueId: Int? = null,
    val leagueName: String? = null
)
