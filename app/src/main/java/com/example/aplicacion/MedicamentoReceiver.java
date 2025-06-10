package com.example.aplicacion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MedicamentoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String nombreMed = intent.getStringExtra("nombre");
        if (nombreMed == null) nombreMed = "Tu medicamento";

        // Intent al tocar la notificación
        Intent i = new Intent(context, MedicationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Crear canal si es necesario
        String canalId = "medicamentos_canal";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Recordatorios de Medicamentos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(canal);
        }

        // Crear notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_medic) // Asegúrate que existe este ícono
                .setContentTitle("Hora de tu medicamento")
                .setContentText("Toma: " + nombreMed)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Lanzar notificación
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
