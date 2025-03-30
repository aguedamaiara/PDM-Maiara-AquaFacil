package com.aquafacil.model.api

import com.aquafacil.model.FishSpecies
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class FishApiService {
    private val client = OkHttpClient()

    fun fetchFishSpecies(): List<FishSpecies>? {
        return try {
            val request = Request.Builder()
                .url("https://fish-species.p.rapidapi.com/fish_api/fishes")
                .get()
                .addHeader("x-rapidapi-key", "7e3c08c390msh321eee865fb4a22p142841jsnb22e8960796d")
                .addHeader("x-rapidapi-host", "fish-species.p.rapidapi.com")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.string()?.let { json ->
                    Gson().fromJson(json, Array<FishSpecies>::class.java).toList()
                }
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
