package com.aquafacil.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var userName by remember { mutableStateOf("Carregando...") }
    var userEmail by remember { mutableStateOf("Carregando...") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) } // Mensagem de sucesso
    val PrimaryButtonColor = Color(0xFF03A9F4)

    // Buscar informações do usuário no Firestore
    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(currentUser.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    userName = document.getString("name") ?: "Nome não disponível"
                    userEmail = currentUser.email ?: "E-mail não disponível"
                } else {
                    Log.d("PerfilScreen", "Documento não encontrado")
                }
            }.addOnFailureListener { exception ->
                Log.w("PerfilScreen", "Erro ao acessar o documento", exception)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Perfil do Usuário") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Informações do usuário
            Text(text = "Nome: $userName", fontSize = 18.sp)
            Text(text = "E-mail: $userEmail", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Exibir mensagem de sucesso, se houver
            successMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Green) // Mensagem de sucesso em verde
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão para editar perfil
            Button(onClick = { showEditDialog = true }) {
                Text(text = "Editar Perfil")
            }
            // Botão para mudar senha
            Button(onClick = { showChangePasswordDialog = true }) {
                Text(text = "Mudar Senha")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para logout
            Button(
                onClick = {
                    auth.signOut() // Desloga o usuário
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true } // Remove as telas anteriores da pilha
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Sair", color = Color.White)
            }
        }
    }


    // Diálogo de edição de perfil
    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentEmail = userEmail,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newEmail ->
                updateUserProfile(auth, newName, newEmail) {
                    userName = newName
                    userEmail = newEmail
                    showEditDialog = false
                }
            }
        )
    }

    // Diálogo de mudança de senha
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            auth = auth, // Passando auth como parâmetro
            onDismiss = { showChangePasswordDialog = false },
            onSave = { oldPassword, newPassword ->
                changeUserPassword(auth, oldPassword, newPassword, {
                    successMessage = "Senha alterada com sucesso!" // Mensagem de sucesso
                    showChangePasswordDialog = false
                }, { error ->
                    // Handle error (you can show a message if needed)
                })
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    var newEmail by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Editar Perfil") },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nome") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("E-mail") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(newName, newEmail) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

fun updateUserProfile(auth: FirebaseAuth, newName: String, newEmail: String, onSuccess: () -> Unit) {
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    if (user != null) {
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.update("name", newName)
            .addOnSuccessListener {
                Log.d("PerfilScreen", "Nome atualizado com sucesso")
            }
            .addOnFailureListener { e ->
                Log.w("PerfilScreen", "Erro ao atualizar nome", e)
            }

        user.updateEmail(newEmail)
            .addOnSuccessListener {
                userDocRef.update("email", newEmail)
                    .addOnSuccessListener {
                        Log.d("PerfilScreen", "E-mail atualizado no Firestore")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.w("PerfilScreen", "Erro ao atualizar e-mail no Firestore", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("PerfilScreen", "Erro ao atualizar e-mail no Firebase Auth", e)
            }
    }
}

@Composable
fun ChangePasswordDialog(
    auth: FirebaseAuth, // Recebendo auth como parâmetro
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Mudar Senha") },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Senha Atual") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nova Senha") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Nova Senha") }
                )
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newPassword == confirmPassword) {
                    changeUserPassword(auth, oldPassword, newPassword, {
                        onSave(oldPassword, newPassword)
                    }, { error ->
                        errorMessage = error
                    })
                } else {
                    errorMessage = "As senhas não coincidem!"
                }
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

fun changeUserPassword(
    auth: FirebaseAuth,
    oldPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val user = auth.currentUser
    val email = user?.email

    if (user != null && email != null) {
        val credential = EmailAuthProvider.getCredential(email, oldPassword)

        // Reautenticar o usuário com a senha antiga
        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Se a reautenticação for bem-sucedida, altere a senha
                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Log.d("PerfilScreen", "Senha alterada com sucesso!")
                        onSuccess() // Chama a função de sucesso
                    } else {
                        // Exibe o erro de alteração de senha
                        Log.w("PerfilScreen", "Erro ao alterar senha", updateTask.exception)
                        onError("Erro ao alterar a senha! Tente novamente.")
                    }
                }
            } else {
                // Exibe o erro de reautenticação
                Log.w("PerfilScreen", "Falha na reautenticação", authTask.exception)
                onError("Senha atual incorreta ou erro na reautenticação!")
            }
        }
    } else {
        onError("Erro: Não foi possível recuperar os dados do usuário!")
    }
}
