package com.aquafacil.model.api

data class FishSpeciesResponse(
    val id: Int,
    val name: String,
    val url: String,
    val img_src_set: Map<String, String>,
    val meta: MetaData
)