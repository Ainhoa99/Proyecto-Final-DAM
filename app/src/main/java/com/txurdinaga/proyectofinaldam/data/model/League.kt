package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO LIGAS
 */
@Serializable
data class League(
    val leagueId: Int? = null,
    var leagueName: String? = null
)
