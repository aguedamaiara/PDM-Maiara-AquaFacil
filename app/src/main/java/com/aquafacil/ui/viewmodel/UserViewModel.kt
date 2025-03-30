package com.aquafacil.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.aquafacil.model.Aquarium
import com.aquafacil.model.User

class UserViewModel : ViewModel() {
    var user = mutableStateOf<User?>(null)
        private set

    fun createUser(id: String, name: String, email: String, password: String) {
        user.value = User(id, name, email, password)
    }
}