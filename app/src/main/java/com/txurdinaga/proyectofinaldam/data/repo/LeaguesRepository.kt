package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.Leage
import com.txurdinaga.proyectofinaldam.data.model.League
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.CREATE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.DELETE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.GET_ALL_LEAGES
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.GET_ALL_LEAGUES
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.SERVER_URL
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsLeage.UPDATE_ROUTE
import com.txurdinaga.proyectofinaldam.util.RegisterError
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface ILeagueRepository {
    suspend fun create(league: League)
    suspend fun update(league: League)
    suspend fun delete(league: League)
    suspend fun getAllLeagues(): List<League>
}
private object ConstantsLeage {
    const val SERVER_URL = "http://192.168.214.250:8080"
    const val API_ENTRY_POINT = "/api/v1"

    const val CREATE_ROUTE = "/league/create"
    const val UPDATE_ROUTE = "/league/update"
    const val DELETE_ROUTE = "/league/delete"
    const val GET_ALL_LEAGUES = "/leagues"
}
class LeageRepository() : ILeagueRepository {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Permite al serializador ignorar campos no esperados
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun create(league: League) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("$SERVER_URL$API_ENTRY_POINT}$CREATE_ROUTE") {
                contentType(ContentType.Application.Json)
                setBody(league)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("LEAGE_LEAGE", "CREATE: SUCCESS")
        } else {
            Log.d("LEAGE_LEAGE", "CREATE: ERROR")
            throw RegisterError()
        }
    }

    override suspend fun update(league: League) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("$SERVER_URL$API_ENTRY_POINT}$UPDATE_ROUTE/${league.id}") {
                contentType(ContentType.Application.Json)
                setBody(league)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("LEAGE_LEAGE", "UPDATE: SUCCESS")
        } else {
            Log.d("LEAGE_LEAGE", "UPDATE: ERROR")
            throw RegisterError()
        }
    }

    override suspend fun delete(league: League) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("$SERVER_URL$API_ENTRY_POINT}$DELETE_ROUTE/${league.id}") {
                contentType(ContentType.Application.Json)
                setBody(league)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("LEAGE_LEAGE", "DELETE: SUCCESS")
        } else {
            Log.d("LEAGE_LEAGE", "DELETE: ERROR")
            throw RegisterError()
        }
    }

    override suspend fun getAllLeagues(): List<League> {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("$SERVER_URL$API_ENTRY_POINT}$GET_ALL_LEAGUES") {
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("LEAGE_LEAGE", "GET: SUCCESS")
            val responseBy = response.bodyAsText()
            return 
        } else {
            Log.d("LEAGE_LEAGE", "GET: ERROR")
            throw RegisterError()
        }
    }

}