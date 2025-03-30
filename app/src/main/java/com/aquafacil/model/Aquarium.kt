package com.aquafacil.model

data class Aquarium(
    val id: String = "", // Identificador único do aquário
    val userId: String = "", // Relaciona o aquário ao usuário
    val type: AquariumType = AquariumType.FRESHWATER, // Tipo de aquário (água doce ou salgada)
    val size: Double = 0.0, // Tamanho em litros
    val fishSpecies: List<FishSpecies> = emptyList(), // Lista de espécies de peixes
    val aquaticPlants: List<AquaticPlant> = emptyList(), // Lista de plantas aquáticas
    val equipment: List<Equipment> = emptyList() // Lista de equipamentos
) {
    // Construtor sem argumentos necessário para o Firestore
    constructor() : this("", "", AquariumType.FRESHWATER, 0.0, emptyList(), emptyList(), emptyList())
}
enum class AquariumType {
    FRESHWATER, // Água doce
    SALTWATER // Água salgada
}
