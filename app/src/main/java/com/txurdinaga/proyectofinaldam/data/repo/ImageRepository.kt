package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.txurdinaga.proyectofinaldam.BuildConfig
import com.txurdinaga.proyectofinaldam.data.model.Category
import com.txurdinaga.proyectofinaldam.data.model.ImageMetadata
import com.txurdinaga.proyectofinaldam.util.DeleteError
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
import com.txurdinaga.proyectofinaldam.util.GetAllError
import com.txurdinaga.proyectofinaldam.util.LoginError
import com.txurdinaga.proyectofinaldam.util.UploadError
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

interface IImageRepository {
    suspend fun uploadImage(image: File, metadata: ImageMetadata): String
    suspend fun getImage(imageId: String): GlideUrl
    suspend fun getAllImages(): List<ImageMetadata>
    suspend fun deleteImage(imageId: String)
}

class ImageRepository : IImageRepository {
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

    override suspend fun uploadImage(image: File, metadata: ImageMetadata): String =
        withContext(Dispatchers.IO) {
            val imageData = image.readBytes()

            val formData = formData {
                append(
                    "data",
                    Json.encodeToString(ImageMetadata.serializer(), metadata),
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    })
                append("file", imageData, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"${metadata.fileName}.jpg\"")
                })
            }

            token = EncryptedPrefsUtil.getToken()

            val response: HttpResponse = client.submitFormWithBinaryData(
                url = "${SERVER_URL}${API_ENTRY_POINT}${UPLOAD_ROUTE}",
                formData = formData,
                block = {
                    header(HttpHeaders.Authorization, "Bearer $token") 
                }
            )

            if (response.status.isSuccess()) {
                Log.d("IMAGE_REPOSITORY", "UPLOAD: SUCCESS")
                return@withContext response.bodyAsText()

            } else {
                Log.d("IMAGE_REPOSITORY", "UPLOAD: ERROR - ${response.bodyAsText()}")
                throw UploadError()
            }
        }

    // TODO Handle errors
    override suspend fun getImage(imageId: String): GlideUrl {
        token = EncryptedPrefsUtil.getToken()

        val glideUrl = GlideUrl(
            "${SERVER_URL}${API_ENTRY_POINT}${GET_ROUTE}/${imageId}",
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
        return glideUrl
    }

    override suspend fun getAllImages(): List<ImageMetadata> {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.get("${SERVER_URL}${API_ENTRY_POINT}${GET_ALL_IMAGES}") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        if (response.status.isSuccess()) {
            Log.d("IMAGE_REPOSITORY", "GET ALL: SUCCESS")
            val json = response.bodyAsText()
            val images = Json.decodeFromString<List<ImageMetadata>>(json)

            for (image in images) {
                val glideUrl = GlideUrl(
                    "${SERVER_URL}${API_ENTRY_POINT}${GET_ROUTE}/${image.imageId}",
                    LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                )
                image.url = glideUrl
            }

            return images

        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d("IMAGE_REPOSITORY", "GET ALL: ERROR")
            throw GetAllError()
        }
    }

    override suspend fun deleteImage(imageId: String) {
        token = EncryptedPrefsUtil.getToken()
        val response: HttpResponse = withContext(Dispatchers.IO) {
            client.delete("${SERVER_URL}${API_ENTRY_POINT}${DELETE_ROUTE}/$imageId") {
                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )
            }
        }
        if (response.status.isSuccess()) {
            Log.d("IMAGE_REPOSITORY", "DELETE: SUCCESS")
        } else if (response.status == HttpStatusCode.Unauthorized) {
            throw LoginError()
        } else {
            Log.d("IMAGE_REPOSITORY", "DELETE: ERROR")
            throw DeleteError()
        }
    }

    companion object {
        private const val SERVER_URL: String = BuildConfig.SERVER_URL
        private const val API_ENTRY_POINT = "/api/v1"
        private const val UPLOAD_ROUTE = "/image/upload"
        private const val GET_ROUTE = "/image"
        private const val GET_ALL_IMAGES = "/images"
        private const val DELETE_ROUTE = "/image"
    }
}
