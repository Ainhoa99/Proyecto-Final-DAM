package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.txurdinaga.proyectofinaldam.data.model.ImageMetadata
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsImage.API_ENTRY_POINT
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsImage.GET_ROUTE
import com.txurdinaga.proyectofinaldam.data.repo.ConstantsImage.SERVER_URL
import com.txurdinaga.proyectofinaldam.util.EncryptedPrefsUtil
import com.txurdinaga.proyectofinaldam.util.UploadError
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

interface IImageRepository {
    suspend fun uploadImage(image: File, metadata: ImageMetadata): String
    suspend fun getImage(imageId: String): GlideUrl
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

    override suspend fun uploadImage(image: File, metadata: ImageMetadata): String = withContext(Dispatchers.IO) {
        val imageData = image.readBytes()

        val formData = formData {
            append("data", Json.encodeToString(ImageMetadata.serializer(), metadata), Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            })
            append("file", imageData, Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"${metadata.fileName}.jpg\"")
            })
        }


        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "${ConstantsImage.SERVER_URL}${ConstantsImage.API_ENTRY_POINT}${ConstantsImage.UPLOAD_ROUTE}",
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
        val glideUrl = GlideUrl(
            "$SERVER_URL${API_ENTRY_POINT}${GET_ROUTE}/${imageId}",
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
        return glideUrl
    }


}

private object ConstantsImage {
    const val SERVER_URL = "https://sardina-server.duckdns.org"
    const val API_ENTRY_POINT = "/api/v1"
    const val UPLOAD_ROUTE = "/image/upload"
    const val GET_ROUTE = "/image"
}
