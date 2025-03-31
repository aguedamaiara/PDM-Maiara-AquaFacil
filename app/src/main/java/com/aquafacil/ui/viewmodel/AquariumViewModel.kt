package com.aquafacil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.aquafacil.model.FishSpecies
import com.aquafacil.model.AquaticPlant
import com.aquafacil.model.Equipment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AquariumViewModel : ViewModel() {

    // Inicializa o Firestore e o Firebase Auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    // Estado do aquário atual (usado durante o cadastro)
    private val _aquarium = MutableLiveData(
        Aquarium(
            id = "",
            userId = "",
            type = AquariumType.FRESHWATER ,
            size = 0.0,
            fishQuantity = "1", // Valor padrão
            isPlanted = false,
            hasEquipment = false
        )
    )
    val aquarium: LiveData<Aquarium> get() = _aquarium

    // Lista de aquários do usuário
    private val _aquariums = MutableLiveData<List<Aquarium>>()
    val aquariums: LiveData<List<Aquarium>> get() = _aquariums

    // Funções para atualizar o estado do aquário atual
    fun setType(type: AquariumType) {
        _aquarium.value = _aquarium.value?.copy(type = type)
    }

    fun setSize(size: String) {
        val sizeValue = size.toDoubleOrNull() ?: 0.0
        _aquarium.value = _aquarium.value?.copy(size = sizeValue)
    }

    fun setFishQuantity(quantity: String) {
        _aquarium.value = _aquarium.value?.copy(fishQuantity = quantity)
    }

    fun setPlanted(isPlanted: Boolean) {
        _aquarium.value = _aquarium.value?.copy(isPlanted = isPlanted)
    }

    fun setHasEquipment(hasEquipment: Boolean) {
        _aquarium.value = _aquarium.value?.copy(hasEquipment = hasEquipment)
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun setName(name: String) {
        _aquarium.value = _aquarium.value?.copy(name = name)
    }


    // Função para salvar o aquário no Firestore
    fun saveAquarium(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val aquarium = _aquarium.value?.copy(
                id = UUID.randomUUID().toString(), // Gera um ID único para o aquário
                userId = userId
            )
            if (aquarium != null) {
                db.collection("users")
                    .document(userId) // Documento do usuário
                    .collection("aquariums") // Subcoleção de aquários
                    .document(aquarium.id) // ID do aquário
                    .set(aquarium)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            } else {
                onFailure(Exception("Aquário inválido"))
            }
        } else {
            onFailure(Exception("Usuário não autenticado"))
        }
    }

    // Função para carregar os aquários do usuário
    fun loadAquariums() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users")
                .document(userId) // Documento do usuário
                .collection("aquariums") // Subcoleção de aquários
                .get()
                .addOnSuccessListener { result ->
                    val aquariumsList = result.toObjects(Aquarium::class.java)
                    _aquariums.value = aquariumsList
                }
                .addOnFailureListener { e ->
                    println("Erro ao carregar aquários: ${e.message}")
                }
        }
    }
}
/*

class AquariumViewModel : ViewModel() {

    // Inicializa o Firestore e o Firebase Auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    // Estado do aquário atual (usado durante o cadastro)
    private val _aquarium = MutableLiveData(Aquarium(
        id = "", // Será gerado pelo Firestore
        userId = "", // Será definido ao salvar
        type = AquariumType.FRESHWATER , // Valor padrão
        size = 0.0, // Valor padrão
        fishSpecies = emptyList(), // Lista vazia inicialmente
        aquaticPlants = emptyList(), // Lista vazia inicialmente
        equipment = emptyList() // Lista vazia inicialmente
    ))
    val aquarium: LiveData<Aquarium> get() = _aquarium

    // Lista de aquários do usuário
    private val _aquariums = MutableLiveData<List<Aquarium>>()
    val aquariums: LiveData<List<Aquarium>> get() = _aquariums

    // Funções para atualizar o estado do aquário atual
    fun setType(type: AquariumType) {
        _aquarium.value = _aquarium.value?.copy(type = type)
    }

    fun setSize(size: String) {
        _aquarium.value = _aquarium.value?.copy(size = size.toDouble())
    }

    fun updateFishSpecies(species: List<FishSpecies>) {
        _aquarium.value = _aquarium.value?.copy(fishSpecies = species)
    }

    fun updatePlants(plants: List<AquaticPlant>) {
        _aquarium.value = _aquarium.value?.copy(aquaticPlants = plants)
    }

    fun updateEquipment(equipment: List<Equipment>) {
        _aquarium.value = _aquarium.value?.copy(equipment = equipment)
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Função para salvar o aquário no Firestore
    fun saveAquarium(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val aquarium = _aquarium.value?.copy(userId = auth.currentUser?.uid ?: "")
        if (aquarium != null) {
            db.collection("aquariums")
                .add(aquarium)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    onFailure(e)
                }
        }
    }

    // Função para carregar os aquários do usuário
    fun loadAquariums() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("aquariums")
                .whereEqualTo("userId", userId) // Filtra os aquários pelo ID do usuário
                .get()
                .addOnSuccessListener { result ->
                    val aquariumsList = result.toObjects(Aquarium::class.java)
                    _aquariums.value = aquariumsList
                }
                .addOnFailureListener { e ->
                    // Trate o erro aqui
                    println("Erro ao carregar aquários: ${e.message}")
                }
        }
    }
}*/
