package com.aquafacil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquafacil.model.FishSpecies
import com.aquafacil.model.api.FishApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FishViewModel : ViewModel() {
    private val fishApiService = FishApiService()

    private val _fishSpecies = MutableStateFlow<List<FishSpecies>>(emptyList())
    val fishSpecies: StateFlow<List<FishSpecies>> get() = _fishSpecies

    fun loadFishSpecies() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = fishApiService.fetchFishSpecies()
            result?.let {
                _fishSpecies.value = it
            }
        }
    }
}