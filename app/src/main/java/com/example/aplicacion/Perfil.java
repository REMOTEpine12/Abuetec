package com.example.aplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Perfil extends AppCompatActivity {

    private TextView tvNombre, tvCorreo, tvTelefono, tvEdad, tvGenero;
    private daoRegistrarse daoR;
    private registrarse usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Asumimos que recibimos el ID del usuario
        int idUsuario = getIntent().getIntExtra("id", -1);

        daoR = new daoRegistrarse(this);
        usuario = daoR.verUno(idUsuario);

        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvEdad = findViewById(R.id.tvEdad);
        tvGenero = findViewById(R.id.tvGenero);

        if (usuario != null) {
            tvNombre.setText("Nombre: " + usuario.getNombre());
            tvCorreo.setText("Correo: " + usuario.getCorreo());
            tvTelefono.setText("Teléfono: " + usuario.getTelefono());
            tvEdad.setText("Edad: " + usuario.getEdad());
            tvGenero.setText("Género: " + usuario.getGenero());
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }

    }

    public void editarPerfil(View view) {
        Intent intent = new Intent(this, activity_main.class);
        intent.putExtra("id", usuario.getId());
        startActivity(intent);
    }

    public void eliminarPerfil(View view) {
        boolean eliminado = daoR.eliminar(usuario.getId());
        if (eliminado) {
            Toast.makeText(this, "Perfil eliminado", Toast.LENGTH_SHORT).show();
            finish(); // cerrar pantalla
        } else {
            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        daoR.cerrar();
    }

}