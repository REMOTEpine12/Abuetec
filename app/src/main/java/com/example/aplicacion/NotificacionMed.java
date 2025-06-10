package com.example.aplicacion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificacionMed {
    public static void scheduleDosisNotifications(Context context, String nombreMed, int frecuenciaHoras, int duracionDias) {
        if (frecuenciaHoras <= 0) {
            return; // O lanza excepción si prefieres forzarlo
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalo = frecuenciaHoras * 60 * 60 * 1000L;
        long tiempoInicial = System.currentTimeMillis();

        int dosisTotales = (duracionDias == 0) ? 1 : (24 / frecuenciaHoras) * duracionDias;

        for (int i = 0; i < dosisTotales; i++) {
            Intent intent = new Intent(context, MedicamentoReceiver.class);  // <- Usa un receiver correcto
            intent.putExtra("nombre", nombreMed);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    nombreMed.hashCode() + i, // ID único por medicamento
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            long tiempo = tiempoInicial + (i * intervalo);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempo, pendingIntent);
        }
    }

}
