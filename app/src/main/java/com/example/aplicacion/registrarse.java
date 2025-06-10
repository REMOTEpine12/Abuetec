package com.example.aplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

public class registrarse extends AppCompatActivity {
    private int id;
    private String nombre;
    private String password;
    private String telefono;
    private String correo;
    private int edad;
    private String genero;

    private daoRegistrarse daoR;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }



    EditText etPhone, etEmail, etEdad, etSexo, etFirstName, etPassword;
    RadioButton rbPhone, rbEmail;
    RadioGroup rgContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);
        daoR = new daoRegistrarse(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Referencias a los elementos
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etEdad = findViewById(R.id.etEdad);
        etSexo = findViewById(R.id.etSexo);
        etFirstName = findViewById(R.id.etFirstName);
        etPassword = findViewById(R.id.etPassword);

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        daoR.cerrar();
    }

    public void Registrarse(View view){
        Intent inten = new Intent(this, activity_main.class);
        startActivity(inten);
    }

    public void Cancelar(View view){
        finish();
    }

    // Función pa registrarse
    public void Registrar(View view) {
        String nombre = ((EditText) findViewById(R.id.etFirstName)).getText().toString();
        int edad = Integer.parseInt(etEdad.getText().toString());
        String telefono = etPhone.getText().toString();
        String correo = etEmail.getText().toString();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
        String genero = etSexo.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        //Crear el obejto y guardarlo en la base
        registrarse nuevoUsuario = new registrarse();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEdad(edad);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setGenero(genero);

        //Crear la instancia de daoRegistro y guardar el usuario
        daoRegistrarse daoR = new daoRegistrarse(this);
        boolean exito = daoR.insertar(nuevoUsuario);

        if (exito){
            Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, activity_main.class);
            startActivity(intent);

        } else{
            Toast.makeText(this, "Error al registrar Usurio", Toast.LENGTH_SHORT).show();
        }



    }

}
