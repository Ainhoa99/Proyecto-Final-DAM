package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.Team
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.DeleteError
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.UpdateError
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface ITeamRepository {
    suspend fun create(team: Team)
    suspend fun update(team: Team)
    suspend fun delete(team: Team)
    suspend fun getAllTeams(): List<Team>
}

private object ConstantsTeam {
    const val TAG = "TEAM_REPOSITORY"

    const val SERVER_URL = "http://10.0.2.2:8080"
    const val API_ENTRY_POINT = "/api/v1"

    const val CREATE_ROUTE = "/team/create"
    const val UPDATE_ROUTE = "/team/update"
    const val DELETE_ROUTE = "/team/delete"
    const val GET_ALL_TEAMS = "/teams"
}

class TeamRepository : ITeamRepository {
    private val token = EncryptedPrefsUtil.getToken()

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun create(team: Team) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("${ConstantsTeam.SERVER_URL}${ConstantsTeam.API_ENTRY_POINT}${ConstantsTeam.CREATE_ROUTE}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(team)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsTeam.TAG, "CREATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsTeam.TAG, "CREATE: ERROR")
            throw CreateError()
        }
    }

    override suspend fun update(team: Team) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("${ConstantsTeam.SERVER_URL}${ConstantsTeam.API_ENTRY_POINT}${ConstantsTeam.UPDATE_ROUTE}/${team.teamId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(team)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsTeam.TAG, "UPDATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsTeam.TAG, "UPDATE: ERROR")
            throw UpdateError()
        }
    }

    override suspend fun delete(team: Team) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("${ConstantsTeam.SERVER_URL}${ConstantsTeam.API_ENTRY_POINT}${ConstantsTeam.DELETE_ROUTE}/${team.teamId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsTeam.TAG, "DELETE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsTeam.TAG, "DELETE: ERROR")
            throw DeleteError()
        }
    }

    override suspend fun getAllTeams(): List<Team> {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("${ConstantsTeam.SERVER_URL}${ConstantsTeam.API_ENTRY_POINT}${ConstantsTeam.GET_ALL_TEAMS}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsTeam.TAG, "GET ALL: SUCCESS")
            val responseBody = response.bodyAsText()
            return Json.decodeFromString(responseBody)
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsTeam.TAG, "GET ALL: ERROR")
            throw GetAllError()
        }
    }
}
