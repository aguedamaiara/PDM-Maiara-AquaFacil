package com.aquafacil.model

data class Equipment(
    val id: String,
    val name: String, // Ex.: Filtro, Iluminação, Sistema de CO₂
    val type: EquipmentType // Tipo de equipamento
){
    // Construtor sem argumentos necessário para o Firestore
    constructor() : this("", "", EquipmentType.OTHER)
}
// Enum para o tipo de equipamento
enum class EquipmentType {
    FILTER,
    LIGHTING,
    CO2_SYSTEM,
    OTHER
}