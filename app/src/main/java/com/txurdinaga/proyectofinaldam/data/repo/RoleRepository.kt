package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.Role
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.CREATE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.DELETE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.GET_ALL_ROLES
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.SERVER_URL
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.TAG
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsRole.UPDATE_ROUTE
import com.txurdinaga.proyectofinaldam.util.CreateError
import com.txurdinaga.proyectofinaldam.util.DeleteError
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.RegisterError
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
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

interface IRoleRepository {
    suspend fun create(role: Role)
    suspend fun update(role: Role)
    suspend fun delete(role: Role)
    suspend fun getAllRoles(): List<Role>
}

private object ConstantsRole {
    const val TAG = "ROLE_REPOSITORY"

    const val SERVER_URL = "https://sardina-server.duckdns.org"
    const val API_ENTRY_POINT = "/api/v1"

    const val CREATE_ROUTE = "/role/create"
    const val UPDATE_ROUTE = "/role/update"
    const val DELETE_ROUTE = "/role/delete"
    const val GET_ALL_ROLES = "/roles"
}

class RoleRepository : IRoleRepository {
    private var token = EncryptedPrefsUtil.getToken()

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Permite al serializador ignorar campos no esperados
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun create(role: Role) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("$SERVER_URL$API_ENTRY_POINT$CREATE_ROUTE") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(role)
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

    override suspend fun update(role: Role) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("$SERVER_URL$API_ENTRY_POINT$UPDATE_ROUTE/${role.roleId}") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(role)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "CREATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "CREATE: ERROR")
            throw UpdateError()
        }
    }

    override suspend fun delete(role: Role) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("$SERVER_URL$API_ENTRY_POINT$DELETE_ROUTE/${role.roleId}") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(role)
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

    override suspend fun getAllRoles(): List<Role> {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("$SERVER_URL$API_ENTRY_POINT$GET_ALL_ROLES") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d(TAG, "GET ALL: SUCCESS")
            val responseBody = response.bodyAsText()
            return Json.decodeFromString<List<Role>>(responseBody)

        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d(TAG, "GET ALL: ERROR")
            throw GetAllError()
        }
    }

}