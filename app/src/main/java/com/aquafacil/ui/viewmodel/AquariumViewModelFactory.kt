package com.aquafacil.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AquariumViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AquariumViewModel::class.java)) {
            return AquariumViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}