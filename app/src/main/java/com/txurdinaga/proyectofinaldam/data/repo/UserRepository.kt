package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.User
import com.txurdinaga.proyectofinaldam.data.repo.Constants.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.Constants.REGISTER_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.SERVER_URL
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface IUserRepository {
    suspend fun register(user: User)
}
private object Constants {
    const val SERVER_URL = "http://192.168.214.250:8080"
    const val API_ENTRY_POINT = "/api/v1"

    const val REGISTER_ROUTE = "/user/register"
}
class UserRepository() : IUserRepository {


    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun register(user: User) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("$SERVER_URL$API_ENTRY_POINT$REGISTER_ROUTE") {
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(user)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("USER_REPOSITORY", "REGISTER: SUCCESS")
        } else {
            Log.d("USER_REPOSITORY", "REGISTER: ERROR")
        }
    }

}