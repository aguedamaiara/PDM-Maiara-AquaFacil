package com.aquafacil.model

data class AquaticPlant(
    val id: String,
    val name: String,
    val scientificName: String? = null // Nome científico (opcional)
)
{
    // Construtor sem argumentos necessário para o Firestore
    constructor() : this("", "", null)
}