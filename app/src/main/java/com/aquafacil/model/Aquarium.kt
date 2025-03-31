//package com.aquafacil.model
//
//data class Aquarium(
//    val id: String = "", // Identificador único do aquário
//    val userId: String = "", // Relaciona o aquário ao usuário
//    val type: AquariumType = AquariumType.FRESHWATER , // Tipo de aquário (água doce ou salgada)
//    val size: Double = 0.0, // Tamanho em litros
//    val fishSpecies: List<FishSpecies> = emptyList(), // Lista de espécies de peixes
//    val aquaticPlants: List<AquaticPlant> = emptyList(), // Lista de plantas aquáticas
//    val equipment: List<Equipment> = emptyList() // Lista de equipamentos
//) {
//    // Construtor sem argumentos necessário para o Firestore
//    constructor() : this("", "", AquariumType.FRESHWATER , 0.0, emptyList(), emptyList(), emptyList())
//}
//enum class AquariumType {
//    FRESHWATER , // Água doce
//    SALTWATER // Água salgada
//}

// Remover AquaticPlant
package com.aquafacil.model

data class Aquarium(
    val id: String = "",
    val userId: String = "",
    val name: String = "", // Novo campo para o nome do aquário
    val type: AquariumType = AquariumType.FRESHWATER ,
    val size: Double = 0.0,
    val fishQuantity: String = "", // Quantidade de peixes (1, 1-5, 5-10, ou mais de 10)
    val isPlanted: Boolean = false, // Se o aquário é plantado
    val hasEquipment: Boolean = false // Se o aquário tem equipamentos
) {
    constructor() : this("", "","", AquariumType.FRESHWATER , 0.0, "", false, false)
}
enum class AquariumType {
    FRESHWATER ,
    SALTWATER
}
fun AquariumType.getDisplayName(): String {
    return when (this) {
        AquariumType.FRESHWATER -> "Água Doce"
        AquariumType.SALTWATER -> "Água Salgada"
    }
}