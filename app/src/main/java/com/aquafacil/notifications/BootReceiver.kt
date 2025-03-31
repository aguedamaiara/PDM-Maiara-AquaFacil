package com.aquafacil.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aquafacil.ui.viewmodel.AquariumViewModel

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
                val notificationHelper = NotificationHelper(context)
                notificationHelper.showNotification(
                    "AquaFacil",
                    "Seus lembretes foram reiniciados",
                    false
                )

                // Aqui você deverá adicionar a lógica para recarregar
                // e reagendar todas as notificações pendentes
            }
        } catch (e: Exception) {
            Log.e("BootReceiver", "Erro ao processar boot completed", e)
        }
    }
}