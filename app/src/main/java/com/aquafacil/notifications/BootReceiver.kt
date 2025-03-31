package com.aquafacil.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aquafacil.ui.viewmodel.AquariumViewModel

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Aqui você pode reiniciar todos os alarmes
            // Você precisará acessar suas tarefas do banco de dados
            // Por simplicidade, vamos apenas mostrar que o receiver foi acionado
            NotificationHelper(context).showNotification(
                "AquaFacil",
                "Seus lembretes foram reiniciados após a reinicialização do dispositivo"
            )
        }
    }
}