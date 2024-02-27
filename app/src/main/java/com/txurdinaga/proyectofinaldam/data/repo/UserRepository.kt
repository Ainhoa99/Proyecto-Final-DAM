package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.User
import com.txurdinaga.proyectofinaldam.data.repo.Constants.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.Constants.LOGIN_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.REGISTER_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.SERVER_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface IUserRepository {
    suspend fun login(email: String, password: String): String?
    suspend fun register(user: User)
    suspend fun update(user: User)
    suspend fun delete(user: User)
}
private object Constants {
    const val SERVER_URL = "http://10.0.2.2:8080"
    const val API_ENTRY_POINT = "/api/v1"

    const val LOGIN_ROUTE = "/user/login"
    const val REGISTER_ROUTE = "/user/register"
    const val UPDATE_ROUTE = "/user/update"
    const val DELETE_ROUTE = "/user/delete"
}
class UserRepository() : IUserRepository {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Permite al serializador ignorar campos no esperados
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun login(email: String, password: String): String? {
        val response: HttpResponse = withContext(Dispatchers.IO){
            client.post("$SERVER_URL$API_ENTRY_POINT$LOGIN_ROUTE") {
                contentType(ContentType.Application.Json)
                setBody(User(email = email, password = password))
            }
        }

        if (response.status.isSuccess()) {
            Log.d("USER_REPOSITORY", "LOGIN: SUCCESS")
            val user = Json.parseToJsonElement(response.bodyAsText()).jsonObject["user"]?.jsonObject
            return user?.get("token")?.jsonPrimitive?.content
        }

        Log.d("USER_REPOSITORY", "LOGIN: ERROR")
        return null
    }

    override suspend fun register(user: User) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("$SERVER_URL$API_ENTRY_POINT$REGISTER_ROUTE") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("USER_REPOSITORY", "REGISTER: SUCCESS")
        } else {
            Log.d("USER_REPOSITORY", "REGISTER: ERROR")
        }
    }

    override suspend fun update(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(user: User) {
        TODO("Not yet implemented")
    }

}