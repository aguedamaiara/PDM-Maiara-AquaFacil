package com.aquafacil.model

data class User(
    val id: String, // Identificador único do usuário
    val name: String,
    val email: String,
    val password: String, // Em um app real, isso deve ser criptografado!
){
    // Construtor sem argumentos necessário para o Firestore
    constructor() : this("", "", "", "")
}