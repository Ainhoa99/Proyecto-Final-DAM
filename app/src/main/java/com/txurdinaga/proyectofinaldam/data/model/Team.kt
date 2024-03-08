package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

/**
 * MODELO EQUIPO
 */
@Serializable
data class Team(
    val teamId: Int,
    var teamName: String,
    var stadium: String? = null,
    var categoryId: Int? = null,
    var leagueId: Int? = null,
    var logo: String? = null,
    var picturesConsent: Boolean? = null,
    var isTeamUnkina: Boolean? = null
)
{
    override fun toString(): String {
        return teamName
    }
}