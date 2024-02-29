package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO PARTIDOS
 */
@Serializable
data class Match(
    val matchId: Int? = null,
    val localTeam: Int? = null,
    val visitingTeam: Int? = null,
    val dateTime: Long? = null,
    val matchPlace: String? = null,
    val ptsLocalTeam: Int? = null,
    val ptsVisitingTeam: Int? = null
)