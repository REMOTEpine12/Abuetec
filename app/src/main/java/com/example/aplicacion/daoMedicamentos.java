package com.example.aplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class daoMedicamentos {
    private final SQLiteDatabase db;
    private final MiBD dbHelper;

    public daoMedicamentos(Context context) {
        dbHelper = new MiBD(context);
        db = dbHelper.getWritableDatabase();
    }

    public void cerrar() {
        dbHelper.close();
    }

    public boolean insertar(Medicamento m) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", m.getNombre());
        valores.put("dosis", m.getDosis());
        valores.put("frecuencia", m.getFrecuencia());
        valores.put("hora", m.getHora());
        valores.put("total_dosis", m.getDosisTotales());
        valores.put("dosis_tomadas", m.getDosisTomadas());
        long resultado = db.insert("medicamentos", null, valores);
        return resultado != -1;
    }

    public boolean actualizarDosisTomadas(int id, int nuevasDosis) {
        ContentValues valores = new ContentValues();
        valores.put("dosis_tomadas", nuevasDosis);
        int resultado = db.update("medicamentos", valores, "idmedicamento=?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }

    public ArrayList<Medicamento> verTodos() {
        ArrayList<Medicamento> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM medicamentos", null);
        if (cursor.moveToFirst()) {
            do {
                Medicamento m = new Medicamento();
                m.setId(cursor.getInt(cursor.getColumnIndexOrThrow("idmedicamento")));
                m.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                m.setDosis(cursor.getString(cursor.getColumnIndexOrThrow("dosis")));
                m.setFrecuencia(cursor.getString(cursor.getColumnIndexOrThrow("frecuencia")));
                m.setHora(cursor.getString(cursor.getColumnIndexOrThrow("hora")));
                m.setDosisTotales(cursor.getInt(cursor.getColumnIndexOrThrow("total_dosis")));

                m.setDosisTomadas(cursor.getInt(cursor.getColumnIndexOrThrow("dosis_tomadas")));
                lista.add(m);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }


    public Medicamento verUno(int id) {
        Medicamento m = null;
        Cursor cursor = db.rawQuery("SELECT * FROM medicamentos WHERE idmedicamento=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            m = new Medicamento();
            m.setId(cursor.getInt(cursor.getColumnIndex("idmedicamento")));
            m.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
            m.setDosis(cursor.getString(cursor.getColumnIndex("dosis")));
            m.setFrecuencia(cursor.getString(cursor.getColumnIndex("frecuencia")));
            m.setHora(cursor.getString(cursor.getColumnIndex("hora")));
            m.setDosisTotales(cursor.getInt(cursor.getColumnIndexOrThrow("total_dosis")));

            m.setDosisTomadas(cursor.getInt(cursor.getColumnIndex("dosis_tomadas")));
        }
        cursor.close();
        return m;
    }

    public boolean eliminar(int id) {
        int resultado = db.delete("medicamentos", "idmedicamento=?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }
}
