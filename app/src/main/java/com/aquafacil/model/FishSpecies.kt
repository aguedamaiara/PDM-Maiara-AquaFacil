package com.aquafacil.model

data class FishSpecies(
    val id: String = "", // Valor padrão vazio
    val name: String = "",
    val scientificName: String? = null, // Nome científico (opcional)
    val imageUrl: String? = "" // Imagem opcional
) {
    // Este construtor sem parâmetros será usado pelo Firebase Firestore
}
