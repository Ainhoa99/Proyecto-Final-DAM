package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String? = null,
    val token: String? = null, // TODO Almacenar el token junto al usuario o siempre fuera?
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val surname: String? = null,
    val picture: String? = null,
    val isAdmin: Boolean? = null,
    val isFirstLogin: Boolean? = null,
    val lastSeen: Long? = null,
    val dateOfBirth: Long? = null,
    val teamId: Int? = null,
    val roleId: Int? = null,
    val familyId: String? = null,
    val isActive: Boolean? = null
)

class UserBuilder { // Builder Pattern
    var userId: String? = null
    var token: String? = null
    var email: String? = null
    var password: String? = null
    var name: String? = null
    var surname: String? = null
    var picture: String? = null
    var isAdmin: Boolean? = null
    var isFirstLogin: Boolean? = null
    var lastSeen: Long? = null
    var dateOfBirth: Long? = null
    var teamId: Int? = null
    var roleId: Int? = null
    var familyId: String? = null
    var isActive: Boolean? = null

    fun userId(userId: String?) = apply { this.userId = userId }
    fun token(token: String?) = apply { this.token = token }
    fun email(email: String?) = apply { this.email = email }
    fun password(password: String?) = apply { this.password = password }
    fun name(name: String?) = apply { this.name = name }
    fun surname(surname: String?) = apply { this.surname = surname }
    fun picture(picture: String?) = apply { this.picture = picture }
    fun isAdmin(isAdmin: Boolean?) = apply { this.isAdmin = isAdmin }
    fun isFirstLogin(isFirstLogin: Boolean?) = apply { this.isFirstLogin = isFirstLogin }
    fun lastSeen(lastSeen: Long?) = apply { this.lastSeen = lastSeen }
    fun dateOfBirth(dateOfBirth: Long?) = apply { this.dateOfBirth = dateOfBirth }
    fun teamId(teamId: Int?) = apply { this.teamId = teamId }
    fun roleId(roleId: Int?) = apply { this.roleId = roleId }
    fun familyId(familyId: String?) = apply { this.familyId = familyId }
    fun isActive(isActive: Boolean?) = apply { this.isActive = isActive }

    fun build() = User(
        userId = userId,
        token = token,
        email = email,
        password = password,
        name = name,
        surname = surname,
        picture = picture,
        isAdmin = isAdmin,
        isFirstLogin = isFirstLogin,
        lastSeen = lastSeen,
        dateOfBirth = dateOfBirth,
        teamId = teamId,
        roleId = roleId,
        familyId = familyId,
        isActive = isActive
    )
}
