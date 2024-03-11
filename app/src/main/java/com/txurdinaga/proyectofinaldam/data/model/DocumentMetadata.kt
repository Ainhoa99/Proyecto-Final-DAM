package com.txurdinaga.proyectofinaldam.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DocumentMetadata (
    val fileName: String,
    val documentId: String? = null,
    val document: String // Sirve como especie de constante o enum para los diferentes tipos de documentos, como normativa, inscripciones, autorizacion, alta deporte escolar etc
    )