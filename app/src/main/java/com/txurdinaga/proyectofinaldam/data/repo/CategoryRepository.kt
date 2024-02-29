package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.txurdinaga.proyectofinaldam.data.model.Category
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsCategory.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsCategory.CREATE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsCategory.DELETE_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsCategory.GET_ALL_CATEGORIES
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsCategory.SERVER_URL
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsCategory.UPDATE_ROUTE
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

interface ICategoryRepository {
    suspend fun create(category: Category)
    suspend fun update(category: Category)
    suspend fun delete(category: Category)
    suspend fun getAllCategories(): List<Category>
}

private object ConstantsCategory {
    const val SERVER_URL = "http://10.0.2.2:8080"
    const val API_ENTRY_POINT = "/api/v1"

    const val CREATE_ROUTE = "/category/create"
    const val UPDATE_ROUTE = "/category/update"
    const val DELETE_ROUTE = "/category/delete"
    const val GET_ALL_CATEGORIES = "/categories"
}

class CategoryRepository : ICategoryRepository {
    private val token = EncryptedPrefsUtil.getToken()

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Permite al serializador ignorar campos no esperados
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun create(category: Category) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.post("$SERVER_URL$API_ENTRY_POINT$CREATE_ROUTE") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(category)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("CATEGORY_REPOSITORY", "CREATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d("CATEGORY_REPOSITORY", "CREATE: ERROR")
            throw CreateError()
        }
    }

    override suspend fun update(category: Category) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.put("$SERVER_URL$API_ENTRY_POINT$UPDATE_ROUTE/${category.categoryId}") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(category)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("CATEGORY_REPOSITORY", "CREATE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d("CATEGORY_REPOSITORY", "CREATE: ERROR")
            throw UpdateError()
        }
    }

    override suspend fun delete(category: Category) {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("$SERVER_URL$API_ENTRY_POINT$DELETE_ROUTE/${category.categoryId}") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
                setBody(category)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("CATEGORY_REPOSITORY", "DELETE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d("CATEGORY_REPOSITORY", "DELETE: ERROR")
            throw DeleteError()
        }
    }

    override suspend fun getAllCategories(): List<Category> {
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("$SERVER_URL$API_ENTRY_POINT$GET_ALL_CATEGORIES") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
                contentType(ContentType.Application.Json)
            }
        }
        if (response.status.isSuccess()) {
            Log.d("CATEGORY_REPOSITORY", "GET ALL: SUCCESS")
            val responseBody = response.bodyAsText()
            return Json.decodeFromString<List<Category>>(responseBody)

        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d("CATEGORY_REPOSITORY", "GET ALL: ERROR")
            throw GetAllError()
        }
    }

}