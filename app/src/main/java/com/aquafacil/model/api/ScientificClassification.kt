package com.aquafacil.model.api

import com.google.gson.annotations.SerializedName

data class ScientificClassification(
    val domain: String,
    val kingdom: String,
    val phylum: String,
    @SerializedName("class") val clazz: String, // Mapeia "class" para "clazz"
    val order: String,
    val superfamily: String?,
    val family: String?
)