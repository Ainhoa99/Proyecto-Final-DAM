package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.Match
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

interface IMatchRepository {
    suspend fun create(match: Match)
    suspend fun update(match: Match)
    suspend fun delete(match: Match)
    suspend fun getAllMatches(): List<Match>
}

private object ConstantsMatch {
    const val TAG = "MATCH_REPOSITORY"

    const val SERVER_URL = "http://10.0.2.2:8080"
    const val API_ENTRY_POINT = "/api/v1"

    const val CREATE_ROUTE = "/match/create"
    const val UPDATE_ROUTE = "/match/update"
    const val DELETE_ROUTE = "/match/delete"
    const val GET_ALL_MATCHES = "/matches"
}

class MatchRepository : IMatchRepository {
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

    override suspend fun create(match: Match) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("${ConstantsMatch.SERVER_URL}${ConstantsMatch.API_ENTRY_POINT}${ConstantsMatch.CREATE_ROUTE}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(match)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsMatch.TAG, "CREATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsMatch.TAG, "CREATE: ERROR")
            throw CreateError()
        }
    }

    override suspend fun update(match: Match) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("${ConstantsMatch.SERVER_URL}${ConstantsMatch.API_ENTRY_POINT}${ConstantsMatch.UPDATE_ROUTE}/${match.matchId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(match)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsMatch.TAG, "UPDATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsMatch.TAG, "UPDATE: ERROR")
            throw UpdateError()
        }
    }

    override suspend fun delete(match: Match) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("${ConstantsMatch.SERVER_URL}${ConstantsMatch.API_ENTRY_POINT}${ConstantsMatch.DELETE_ROUTE}/${match.matchId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsMatch.TAG, "DELETE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsMatch.TAG, "DELETE: ERROR")
            throw DeleteError()
        }
    }

    override suspend fun getAllMatches(): List<Match> {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("${ConstantsMatch.SERVER_URL}${ConstantsMatch.API_ENTRY_POINT}${ConstantsMatch.GET_ALL_MATCHES}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status.isSuccess()) {
            Log.d(ConstantsMatch.TAG, "GET ALL: SUCCESS")
            val responseBody = response.bodyAsText()
            return Json.decodeFromString(responseBody)
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(ConstantsMatch.TAG, "GET ALL: ERROR")
            throw GetAllError()
        }
    }
}
