package com.aquafacil.model.api

import retrofit2.http.GET

interface FishSpeciesApi {
    @GET("species")
    suspend fun getFishSpecies(): List<FishSpeciesResponse>
}