package com.example.aplicacion;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class activity_main extends AppCompatActivity {
    Button btnChatbot;
    ProgressBar medicationProgress;
    ImageView profileIcon;
    ImageButton btnMessages;
    private HotwordListener hotwordListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        solicitarPermisos();
        try {
            TextView tvPresion = findViewById(R.id.tvPresionValor);
            TextView tvTemperatura = findViewById(R.id.tvTemperaturaValor);
            TextView tvPulso = findViewById(R.id.tvPulsoValor);

            String presion = "--", temperatura = "--", pulso = "--";

            daoRegistroSalud dao = new daoRegistroSalud(this);
            ArrayList<registroSalud> registros = dao.verTodos();

            if (registros != null && !registros.isEmpty()) {
                for (registroSalud r : registros) {
                    if (r.getTipo() == null || r.getValor() == null) continue;

                    switch (r.getTipo().toLowerCase()) {
                        case "presión":
                            if (presion.equals("--")) presion = r.getValor();
                            break;
                        case "temperatura":
                            if (temperatura.equals("--")) temperatura = r.getValor();
                            break;
                        case "pulso":
                            if (pulso.equals("--")) pulso = r.getValor();
                            break;
                    }
                }
            }

            if (tvPresion != null) tvPresion.setText(presion);
            if (tvTemperatura != null) tvTemperatura.setText(temperatura);
            if (tvPulso != null) tvPulso.setText(pulso);

        } catch (Exception e) {
            e.printStackTrace(); // Así lo ves en Logcat
            Toast.makeText(this, "Error al cargar datos del tracker", Toast.LENGTH_SHORT).show();
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        // Botón de asistente en sección principal
        btnChatbot = findViewById(R.id.btnChatbot);
        btnChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(activity_main.this, ChatbotActivity.class);
            startActivity(intent);
        });

        // Icono de perfil superior
        profileIcon = findViewById(R.id.profile);
        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Ir a perfil", Toast.LENGTH_SHORT).show();
            // Aquí podrías abrir una nueva actividad de perfil
        });

        // Barra de progreso de medicación
        medicationProgress = findViewById(R.id.medicationProgress);
        int totalDosis = 6;
        int tomadas = 3;
        int porcentaje = (tomadas * 100) / totalDosis;
        medicationProgress.setProgress(porcentaje);

        // Botón de asistente virtual desde barra inferior (Messages)
        btnMessages = findViewById(R.id.btnMessages);
        btnMessages.setOnClickListener(v -> {
            Intent intent = new Intent(activity_main.this, ChatbotActivity.class);
            startActivity(intent);
        });
    }
    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ArrayList<String> permisos = new ArrayList<>();

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.POST_NOTIFICATIONS);
            }

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.RECORD_AUDIO);
                iniciarHotwordListener();
            }

            if (!permisos.isEmpty()) {
                requestPermissions(permisos.toArray(new String[0]), 1001);
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hotwordListener != null) {
            hotwordListener.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarHotwordListener();
        } else {
            Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
        }
    }
    public void CITAS(View view){
        Intent intent = new Intent(activity_main.this, Citas.class);
        startActivity(intent);
    }
    public void medicina(View view){
        Intent intent = new Intent(activity_main.this, MedicationActivity.class);
        startActivity(intent);
    }
    public void tracker(View view){
        Intent intent = new Intent(activity_main.this, Tracker.class);
        startActivity(intent);
    }
    public void chatbot(View view){
        Intent intent = new Intent(activity_main.this, ChatbotActivity.class);
        startActivity(intent);
    }
}