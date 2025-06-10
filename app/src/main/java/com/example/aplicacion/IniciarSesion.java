package com.example.aplicacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IniciarSesion extends AppCompatActivity {
    EditText etEmailPhone, etPassword;
    private daoRegistrarse daoR;
    CheckBox checkboxRemember;
    Button btnSignIn;
    TextView tvForgotPassword, tvRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etEmailPhone = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        btnSignIn = findViewById(R.id.signInBtn);
        tvRegister = findViewById(R.id.registerText);

        daoR = new daoRegistrarse(this);


        // tvForgotPassword.setOnClickListener(view -> {
        //    Toast.makeText(this, "Se envió un correo con tu contraseña", Toast.LENGTH_SHORT).show();
        // });


    }
    public void boton(View vista){
        String emailPhone = etEmailPhone.getText().toString();
        String password = etPassword.getText().toString();

        if (emailPhone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete los dos campos", Toast.LENGTH_SHORT).show();
        }
        boolean valido = daoR.validarUsuario(emailPhone, password);
        if (valido){
            Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, activity_main.class);
            startActivity(intent);
            finish();
        } else{
            Toast.makeText(this, "Correo o contraseña incorrectos",Toast.LENGTH_SHORT).show();
        }

    }
    public void Registrarse(View Vista){
        Intent intento = new Intent(this, registrarse.class);
        startActivity(intento);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        daoR.cerrar();
    }

}