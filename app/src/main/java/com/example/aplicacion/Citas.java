package com.example.aplicacion;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Citas extends AppCompatActivity {
    private HotwordListener hotwordListener;
    LinearLayout contenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_citas);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarHotwordListener();
        contenedor = findViewById(R.id.CitasContainer);
        Button btnAgregar = findViewById(R.id.btnAgregarCita);
        btnAgregar.setOnClickListener(v -> mostrarFormularioCita());

        mostrarCitas();
    }

    private void mostrarCitas() {
        contenedor.removeAllViews();
        daoCitas dao = new daoCitas(this);
        ArrayList<Cita> citas = dao.verTodas();

        for (Cita c : citas) {
            View card = getLayoutInflater().inflate(R.layout.card_cita, contenedor, false);

            ((TextView) card.findViewById(R.id.nombreDoctor)).setText(c.getNombreDoctor());
            ((TextView) card.findViewById(R.id.especialidadDoctor)).setText(c.getEspecialidad());
            ((TextView) card.findViewById(R.id.fechaHora)).setText(c.getFechaHora());
            TextView estado = card.findViewById(R.id.estadoCita);
            estado.setText(c.getEstado());

            // Cambiar estado a completada al hacer clic si está pendiente
            if (c.getEstado().equalsIgnoreCase("Pendiente")) {
                card.setOnClickListener(v -> {
                    c.setEstado("Completada");
                    dao.actualizarEstado(c.getId(), "Completada");
                    mostrarCitas(); // recargar
                });
            }

            contenedor.addView(card);
        }
    }

    private void mostrarFormularioCita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Cita");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del doctor");
        layout.addView(inputNombre);

        EditText inputEspecialidad = new EditText(this);
        inputEspecialidad.setHint("Especialidad");
        layout.addView(inputEspecialidad);

        // Campo de fecha
        EditText inputFecha = new EditText(this);
        inputFecha.setHint("Seleccionar fecha");
        inputFecha.setFocusable(false);
        layout.addView(inputFecha);

        // Campo de hora
        EditText inputHora = new EditText(this);
        inputHora.setHint("Seleccionar hora");
        inputHora.setFocusable(false);
        layout.addView(inputHora);

        final Calendar calendar = Calendar.getInstance();

        // Selector de fecha
        inputFecha.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        inputFecha.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        // Selector de hora
        inputHora.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        inputHora.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );
            timePicker.show();
        });

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String especialidad = inputEspecialidad.getText().toString().trim();
            String fecha = inputFecha.getText().toString().trim();
            String hora = inputHora.getText().toString().trim();

            if (!nombre.isEmpty() && !especialidad.isEmpty() && !fecha.isEmpty() && !hora.isEmpty()) {
                String fechaHora = fecha + " - " + hora;

                Cita nuevaCita = new Cita();
                nuevaCita.setNombreDoctor(nombre);
                nuevaCita.setEspecialidad(especialidad);
                nuevaCita.setFechaHora(fechaHora);
                nuevaCita.setEstado("Pendiente");

                daoCitas dao = new daoCitas(this);
                dao.insertar(nuevaCita);
                programarNotificacionCita(nombre, fechaHora);
                mostrarCitas(); // recarga
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void programarNotificacionCita(String nombreDoctor, String fechaHora) {
        try {
            // Fecha esperada en formato: "dd MMMM yyyy - hh:mm a"
            SimpleDateFormat formato = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault());
            Date fecha = formato.parse(fechaHora);
            if (fecha == null) return;

            // Notificar 20 minutos antes
            long tiempoEnMillis = fecha.getTime();

            if (tiempoEnMillis < System.currentTimeMillis()) {
                // La hora ya pasó, no se programa
                return;
            }

            Intent intent = new Intent(this, CitaReceiver.class);
            intent.putExtra("nombreDoctor", nombreDoctor);
            intent.putExtra("fechaHora", fechaHora);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, (int) tiempoEnMillis, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoEnMillis, pendingIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void iniciarHotwordListener() {
        hotwordListener = new HotwordListener(this, comando -> {
            runOnUiThread(() -> {
                switch (comando) {
                    case "chatbot":
                        Toast.makeText(this, "Abriendo asistente...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, ChatbotActivity.class));
                        break;
                    case "citas":
                        Toast.makeText(this, "Abriendo citas...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Citas.class));
                        break;
                    case "medicacion":
                        Toast.makeText(this, "Abriendo medicación...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MedicationActivity.class));
                        break;
                    case "tracker":
                        Toast.makeText(this, "Abriendo tracker...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, Tracker.class));
                        break;
                    case "inicio":
                        Toast.makeText(this, "Regresando a inicio...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, activity_main.class));
                        break;
                    default:
                        Toast.makeText(this, "Comando no reconocido.", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        });
        hotwordListener.start();
    }


    public void CITAS(View view) {
        startActivity(new Intent(this, Citas.class));
    }

    public void medicina(View view) {
        startActivity(new Intent(this, MedicationActivity.class));
    }

    public void tracker(View view) {
        startActivity(new Intent(this, Tracker.class));
    }

    public void chatbot(View view) {
        startActivity(new Intent(this, ChatbotActivity.class));
    }

    public void home(View view) {
        startActivity(new Intent(this, activity_main.class));
    }
}
