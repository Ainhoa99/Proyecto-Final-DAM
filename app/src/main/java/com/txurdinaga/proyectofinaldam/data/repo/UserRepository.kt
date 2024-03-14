package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.BuildConfig
import com.txurdinaga.proyectofinaldam.data.model.User
import com.txurdinaga.proyectofinaldam.data.repo.Constants.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.Constants.DELETE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.GET_ALL_USERS_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.GET_USER_BY_ID_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.IS_ADMIN_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.LOGIN_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.REGISTER_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.Constants.SERVER_URL
import com.txurdinaga.proyectofinaldam.data.repo.Constants.TAG
import com.txurdinaga.proyectofinaldam.data.repo.Constants.UPDATE_ROUTE
import com.txurdinaga.proyectofinaldam.util.DeleteError
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.GetByIdError
import com.txurdinaga.proyectofinaldam.util.IsAdminRequestError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.RegisterError
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface IUserRepository {
    suspend fun login(email: String, password: String): String
    suspend fun isAdmin():Boolean
    suspend fun register(user: User)
    suspend fun update(user: User)
    suspend fun delete(user: User)
    suspend fun getUser(userId: String): User
    suspend fun getAllUsers(): List<User>
}
private object Constants {
    const val TAG = "USER_REPOSITORY"

    val SERVER_URL: String = BuildConfig.SERVER_URL
    const val API_ENTRY_POINT = "/api/v1"

    const val LOGIN_ROUTE = "/user/login"
    const val REGISTER_ROUTE = "/user/register"
    const val UPDATE_ROUTE = "/user/update"
    const val DELETE_ROUTE = "/user/delete"
    const val GET_ALL_USERS_ROUTE = "/users"
    const val GET_USER_BY_ID_ROUTE = "/user"
    const val IS_ADMIN_ROUTE = "/user/isAdmin"
}

class UserRepository() : IUserRepository {
    private var token = EncryptedPrefsUtil.getToken()

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    override suspend fun login(email: String, password: String): String {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO){
            client.post("$SERVER_URL$API_ENTRY_POINT$LOGIN_ROUTE") {
                contentType(ContentType.Application.Json)
                setBody(User(email = email, password = password))
            }
        }

        if (response.status.isSuccess()) {
            Log.d("USER_REPOSITORY", "LOGIN: SUCCESS")
            val user = Json.parseToJsonElement(response.bodyAsText()).jsonObject["user"]?.jsonObject
            return user?.get("token")?.jsonPrimitive?.content ?: throw LoginError()
        } else {
            Log.d("USER_REPOSITORY", "LOGIN: ERROR")
            throw LoginError()
        }
    }

    override suspend fun isAdmin():Boolean{
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("$SERVER_URL$API_ENTRY_POINT$IS_ADMIN_ROUTE") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "IS ADMIN: SUCCESS")
            val responseBody = response.bodyAsText()
            return Json.decodeFromString(responseBody)
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "IS ADMIN: ERROR")
            throw IsAdminRequestError()
        }
    }

    override suspend fun register(user: User) {
        token = EncryptedPrefsUtil.getToken()
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
            throw RegisterError()
        }
    }

    override suspend fun update(user: User) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("$SERVER_URL$API_ENTRY_POINT$UPDATE_ROUTE/${user.userId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(user)
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

    override suspend fun delete(user: User) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("$SERVER_URL$API_ENTRY_POINT$DELETE_ROUTE/${user.userId}") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
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

    override suspend fun getUser(userId: String): User {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("$SERVER_URL$API_ENTRY_POINT$GET_USER_BY_ID_ROUTE/$userId") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "GET BY ID: SUCCESS")
            val responseBody = response.bodyAsText()
            return json.decodeFromString(responseBody)
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "GET BY ID: ERROR")
            throw GetByIdError()
        }
    }

    override suspend fun getAllUsers(): List<User> {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("$SERVER_URL$API_ENTRY_POINT$GET_ALL_USERS_ROUTE") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "GET ALL: SUCCESS")
            val responseBody = response.bodyAsText()
            val users = json.decodeFromString<List<Map<String, User>>>(responseBody).map { it.values.first() }
            return users
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "GET ALL: ERROR")
            throw GetAllError()
        }
    }

    companion object {
        val json = Json{
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true}
    }

}