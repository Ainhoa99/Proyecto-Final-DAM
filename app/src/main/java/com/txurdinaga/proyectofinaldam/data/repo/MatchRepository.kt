package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.BuildConfig
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

class MatchRepository : IMatchRepository {
    private var token = EncryptedPrefsUtil.getToken()

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
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("${SERVER_URL}${API_ENTRY_POINT}${CREATE_ROUTE}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(match)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "CREATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "CREATE: ERROR")
            throw CreateError()
        }
    }

    override suspend fun update(match: Match) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("${SERVER_URL}${API_ENTRY_POINT}${UPDATE_ROUTE}/${match.matchId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(match)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "UPDATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "UPDATE: ERROR")
            throw UpdateError()
        }
    }

    override suspend fun delete(match: Match) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("${SERVER_URL}${API_ENTRY_POINT}${DELETE_ROUTE}/${match.matchId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "DELETE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "DELETE: ERROR")
            throw DeleteError()
        }
    }

    override suspend fun getAllMatches(): List<Match> {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("${SERVER_URL}${API_ENTRY_POINT}${GET_ALL_MATCHES}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "GET ALL: SUCCESS")
            val responseBody = response.bodyAsText()
            return Json.decodeFromString(responseBody)
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "GET ALL: ERROR")
            throw GetAllError()
        }
    }

    companion object {
        private const val TAG = "MATCH_REPOSITORY"

        private const val SERVER_URL: String = BuildConfig.SERVER_URL
        private const val API_ENTRY_POINT = "/api/v1"

        private const val CREATE_ROUTE = "/match/create"
        private const val UPDATE_ROUTE = "/match/update"
        private const val DELETE_ROUTE = "/match/delete"
        private const val GET_ALL_MATCHES = "/matches"
    }
}
