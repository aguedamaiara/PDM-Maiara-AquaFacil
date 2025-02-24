package com.aquafacil.model

data class Aquarium(
    val type: AquariumType, // Água doce ou salgada
    val size: Int, // Tamanho em litros
    val species: Set<String>, // Espécies de peixes
    val plants: Set<String>, // Plantas aquáticas
    val equipment: Set<String> // Equipamentos
)

enum class AquariumType {
    FRESHWATER, // Água doce
    SALTWATER // Água salgada
}
