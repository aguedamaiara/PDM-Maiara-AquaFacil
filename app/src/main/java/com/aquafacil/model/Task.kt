package com.aquafacil.model

data class Task(
    val id: String,
    val title: String,
    val dates: String,
    val frequency: String, // Adicionado: "Diária", "Semanal", etc.
    val completed: Boolean = false,
    val completedDate: Long? = null // Data quando foi marcada como completa (timestamp)
)