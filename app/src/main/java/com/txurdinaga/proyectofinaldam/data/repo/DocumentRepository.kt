package com.txurdinaga.proyectofinaldam.data.repo

import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.txurdinaga.proyectofinaldam.BuildConfig
import com.txurdinaga.proyectofinaldam.data.model.DocumentMetadata
import com.txurdinaga.proyectofinaldam.data.model.ImageMetadata
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import java.io.File

interface IDocumentRepository {
    suspend fun uploadDocument(document: File, metadata: DocumentMetadata): Int
    suspend fun getDocument(documentId: Int): File
    suspend fun getAllDocuments(): List<DocumentMetadata>
    suspend fun deleteDocument(documentId: Int)
}

class DocumentRepository : IDocumentRepository {
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

    companion object {
        private const val SERVER_URL: String = BuildConfig.SERVER_URL
        private const val API_ENTRY_POINT = "/api/v1"
        private const val UPLOAD_ROUTE = "/document/upload"
        private const val GET_ROUTE = "/document"
        private const val GET_ALL_IMAGES = "/documents"
        private const val DELETE_ROUTE = "/document"
    }

    override suspend fun uploadDocument(document: File, metadata: DocumentMetadata): Int =
        withContext(Dispatchers.IO) {
            val documentData = document.readBytes()

            val formData = formData {
                append(
                    "data",
                    Json.encodeToString(DocumentMetadata.serializer(), metadata),
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    })
                append("file", documentData, Headers.build {
                    append(HttpHeaders.ContentType, "application/pdf")
                    append(HttpHeaders.ContentDisposition, "filename=\"${metadata.fileName}.pdf\"")
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
                Log.d("DOCUMENT_REPOSITORY", "UPLOAD: SUCCESS")
                return@withContext response.bodyAsText().toInt()

            } else {
                Log.d("DOCUMENT_REPOSITORY", "UPLOAD: ERROR - ${response.bodyAsText()}")
                throw UploadError()
            }
        }

    override suspend fun getDocument(documentId: Int): File {
        TODO("Not yet implemented")
    }

    override suspend fun getAllDocuments(): List<DocumentMetadata> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDocument(documentId: Int) {
        TODO("Not yet implemented")
    }
}