package com.example.aplicacion;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.AccessControlContext;

public class MiBD extends SQLiteOpenHelper{
    private static final String Database_Name = "Sistema_Salud";
    private static final int Database_version = 2;

    public MiBD(Context context){
        super(context, Database_Name, null, Database_version);
    }



    @Override
    public void onCreate(SQLiteDatabase db){
        String sqlCrearTabla = "CREATE TABLE IF NOT EXISTS cuenta (" +
                "idcuenta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "contraseña TEXT, " +
                "teléfono TEXT, " +
                "email TEXT, " +
                "edad INTEGER, " +
                "genero TEXT)";
        db.execSQL(sqlCrearTabla);
        String sqlCrearTablaSalud = "CREATE TABLE IF NOT EXISTS registro_salud (" +
                "idregistro INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tipo TEXT NOT NULL, " +
                "valor TEXT NOT NULL, " +
                "fecha DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(sqlCrearTablaSalud);
        String sqlCrearTablaCitas = "CREATE TABLE IF NOT EXISTS citas (" +
                "idcita INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_doctor TEXT NOT NULL, " +
                "especialidad TEXT NOT NULL, " +
                "fecha_hora TEXT NOT NULL, " +
                "estado TEXT NOT NULL DEFAULT 'Pendiente')";
        db.execSQL(sqlCrearTablaCitas);
        String sqlMedicamentos = "CREATE TABLE IF NOT EXISTS medicamentos (" +
                "idmedicamento INTEGER PRIMARY KEY AUTOINCREMENT, " +  // CAMBIO AQUÍ
                "nombre TEXT NOT NULL, " +
                "dosis TEXT NOT NULL, " +
                "frecuencia TEXT NOT NULL, " +
                "total_dosis INTEGER, " +
                "dosis_tomadas INTEGER, " +
                "hora TEXT NOT NULL)";

        db.execSQL(sqlMedicamentos);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS cuenta");
        db.execSQL("DROP TABLE IF EXISTS citas");
        db.execSQL("DROP TABLE IF EXISTS registro_salud");
        db.execSQL("DROP TABLE IF EXISTS medicamentos");
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }






}
