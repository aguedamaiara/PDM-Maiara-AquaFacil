package com.aquafacil.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aquafacil.model.Aquarium
import com.aquafacil.model.AquariumType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class AquariumViewModel(private val context: Context) : ViewModel() {

    // Inicializa o Firestore e o Firebase Auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    // Estado do aquário atual (usado durante o cadastro)
    private val _aquarium = MutableLiveData(
        Aquarium(
            id = "",
            userId = "",
            name = "",
            type = AquariumType.FRESHWATER,
            size = 0.0,
            fishQuantity = "1", // Valor padrão
            isPlanted = false,
            hasEquipment = false
        )
    )
    val aquarium: LiveData<Aquarium> get() = _aquarium

    // TaskStateManager agora recebe o contexto
    val taskStateManager = TaskStateManager(this, context)

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

    fun updateAquarium(aquarium: Aquarium) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .collection("aquariums")
            .document(aquarium.id)
            .set(aquarium)
            .addOnSuccessListener {
                loadAquariums() // Atualiza a lista após a edição
            }
            .addOnFailureListener { e ->
                println("Erro ao atualizar aquário: ${e.message}")
            }
    }
}