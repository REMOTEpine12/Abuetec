package com.example.aplicacion;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.List;

public class MedicationActivity extends AppCompatActivity {

    private LinearLayout contenedorTarjetas;
    private daoMedicamentos dao;
    private String horaSeleccionada = "08:00"; // hora por defecto

    private HotwordListener hotwordListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarHotwordListener();
        contenedorTarjetas = findViewById(R.id.contenedorTarjetas);
        dao = new daoMedicamentos(this);

        Button btnAgregar = findViewById(R.id.btnAgregarMedicamento);
        btnAgregar.setOnClickListener(view -> mostrarDialogoAgregarMedicamento());

        cargarMedicamentos();
    }

    private void mostrarDialogoAgregarMedicamento() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_medicamento, null);

        EditText etNombre = view.findViewById(R.id.etNombreMed);
        EditText etDosis = view.findViewById(R.id.etDosis);
        EditText etFrecuencia = view.findViewById(R.id.etFrecuencia);
        EditText etDuracion = view.findViewById(R.id.etDuracion);

        // Selector de hora al hacer click en cualquier campo editable
        view.setOnClickListener(v -> seleccionarHora());

        new AlertDialog.Builder(this)
                .setTitle("Agregar Medicamento")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString();
                    String dosis = etDosis.getText().toString();
                    int frecuenciaHoras = Integer.parseInt(etFrecuencia.getText().toString());
                    int duracionDias = Integer.parseInt(etDuracion.getText().toString());

                    int totalDosis = duracionDias == 0 ? 100 : (24 / frecuenciaHoras) * duracionDias;
                    Medicamento nuevo = new Medicamento(nombre, dosis, frecuenciaHoras + " horas", horaSeleccionada, totalDosis);

                    dao.insertar(nuevo);
                    NotificacionMed.scheduleDosisNotifications(this, nombre, frecuenciaHoras, duracionDias);

                    cargarMedicamentos();
                    Toast.makeText(this, "Medicamento guardado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void seleccionarHora() {
        Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute);
        }, hora, minuto, true);
        dialog.show();
    }

    private void cargarMedicamentos() {
        contenedorTarjetas.removeAllViews();
        List<Medicamento> lista = dao.verTodos();
        if (lista == null || lista.isEmpty()) {
            Toast.makeText(this, "No hay medicamentos guardados", Toast.LENGTH_SHORT).show();
            return;
        }
        for (Medicamento m : lista) {
            View tarjeta = LayoutInflater.from(this).inflate(R.layout.tarjeta_medicamento, contenedorTarjetas, false);

            TextView tvNombre = tarjeta.findViewById(R.id.tvNombre);
            TextView tvDosis = tarjeta.findViewById(R.id.tvDosis);
            TextView tvFrecuencia = tarjeta.findViewById(R.id.tvFrecuencia);
            ProgressBar progressBar = tarjeta.findViewById(R.id.progressMedic);
            TextView txtProgreso = tarjeta.findViewById(R.id.txtProgreso);

            tvNombre.setText(m.getNombre());
            tvDosis.setText(m.getDosis());
            tvFrecuencia.setText("Cada " + m.getFrecuencia());

            int progreso = m.getDosisTomadas();
            int total = m.getDosisTotales();
            progressBar.setMax(total);
            progressBar.setProgress(progreso);
            txtProgreso.setText(progreso + "/" + total);

            ImageButton btnAgregar = tarjeta.findViewById(R.id.btnTomarDosis);
            btnAgregar.setOnClickListener(view -> {
                if (progreso < total) {
                    dao.actualizarDosisTomadas(m.getId(), progreso + 1);
                    cargarMedicamentos();
                }
            });

            contenedorTarjetas.addView(tarjeta);
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
