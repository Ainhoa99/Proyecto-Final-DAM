package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO EQUIPO
 */
@Serializable
data class Team(
    val teamId: Int? = null,
    val teamName: String? = null,
    val stadium: String? = null,
    val categoryId: Int? = null,
    val leagueId: Int? = null,
    val logo: String? = null,
    val picturesConsent: Boolean? = null,
    val isTeamUnkina: Boolean? = null
)