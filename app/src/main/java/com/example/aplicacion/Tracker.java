package com.example.aplicacion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Tracker extends AppCompatActivity {

    daoRegistroSalud dao; // Instancia del DAO
    private HotwordListener hotwordListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tracker);
        TextView tvNombre = findViewById(R.id.nombreUsuario);
        TextView tvEdad = findViewById(R.id.edad);
        TextView tvCorreo = findViewById(R.id.correo);  // Puedes cambiar este por género si prefieres
        iniciarHotwordListener();
        SQLiteDatabase db = new MiBD(this).getReadableDatabase();

// Puedes filtrar por un ID si manejas login; aquí solo se toma el primer usuario
        Cursor c = db.rawQuery("SELECT nombre, edad, email FROM cuenta LIMIT 1", null);

        if (c.moveToFirst()) {
            String nombre = c.getString(0);
            int edad = c.getInt(1);
            String correo = c.getString(2);

            tvNombre.setText(nombre);
            tvEdad.setText(edad + " años");
            tvCorreo.setText(correo); // O usa el campo 'email' si prefieres
        } else {
            tvNombre.setText("Usuario no registrado");
            tvEdad.setText("--");
            tvCorreo.setText("--");
        }

        c.close();
        db.close();

        dao = new daoRegistroSalud(this); // Inicializa el DAO

        //Cards ID
        LinearLayout cardSugar = findViewById(R.id.cardSugar);
        LinearLayout cardPres = findViewById(R.id.presionCard);
        LinearLayout cardTemp = findViewById(R.id.cardTemp);
        LinearLayout cardPulso = findViewById(R.id.cardPulso);
        LinearLayout cardOxig = findViewById(R.id.cardOxigen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardSugar.setOnClickListener(view -> {
            mostrarDialogoIngreso("Ingresar nivel de azúcar", this, valor -> {
                dao.insertarRegistro("azúcar", valor);
                Toast.makeText(this, "Azúcar registrada: " + valor, Toast.LENGTH_SHORT).show();
            });
        });

        cardPres.setOnClickListener(view -> {
            mostrarDialogoIngreso("Ingresar presión arterial", this, valor -> {
                dao.insertarRegistro("presión", valor);
                Toast.makeText(this, "Presión registrada: " + valor, Toast.LENGTH_SHORT).show();
            });
        });

        cardTemp.setOnClickListener(view -> {
            mostrarDialogoIngreso("Ingresar temperatura", this, valor -> {
                dao.insertarRegistro("temperatura", valor);
                Toast.makeText(this, "Temperatura registrada: " + valor, Toast.LENGTH_SHORT).show();
            });
        });

        cardPulso.setOnClickListener(view -> {
            mostrarDialogoIngreso("Ingresar pulso", this, valor -> {
                dao.insertarRegistro("pulso", valor);
                Toast.makeText(this, "Pulso registrado: " + valor, Toast.LENGTH_SHORT).show();
            });
        });

        cardOxig.setOnClickListener(view -> {
            mostrarDialogoIngreso("Ingresar oxigenación", this, valor -> {
                dao.insertarRegistro("oxigenación", valor);
                Toast.makeText(this, "Oxigenación registrada: " + valor, Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void CITAS(View view){
        startActivity(new Intent(this, Citas.class));
    }

    public void medicina(View view){
        startActivity(new Intent(this, MedicationActivity.class));
    }

    public void tracker(View view){
        startActivity(new Intent(this, Tracker.class));
    }

    public void chatbot(View view){
        startActivity(new Intent(this, ChatbotActivity.class));
    }

    public void home(View view){
        startActivity(new Intent(this, activity_main.class));
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
    public void mostrarDialogoIngreso(String titulo, Context context, OnValorIngresadoListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String valor = input.getText().toString();
            listener.onValorIngresado(valor);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public interface OnValorIngresadoListener {
        void onValorIngresado(String valor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dao.cerrar(); // Cierra la base de datos cuando se destruye la actividad
    }
}
