package com.example.aplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class daoCitas {
    private final SQLiteDatabase db;

    public daoCitas(Context context) {
        MiBD dbHelper = new MiBD(context);
        db = dbHelper.getWritableDatabase();
    }

    public boolean insertar(Cita cita) {
        ContentValues cv = new ContentValues();
        cv.put("nombre_doctor", cita.getNombreDoctor());
        cv.put("especialidad", cita.getEspecialidad());
        cv.put("fecha_hora", cita.getFechaHora());
        cv.put("estado", cita.getEstado());
        return db.insert("citas", null, cv) != -1;
    }
    public void actualizarEstado(int idCita, String nuevoEstado) {
        ContentValues cv = new ContentValues();
        cv.put("estado", nuevoEstado);
        db.update("citas", cv, "idcita=?", new String[]{String.valueOf(idCita)});
    }

    public ArrayList<Cita> verTodas() {
        ArrayList<Cita> citas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM citas ORDER BY fecha_hora", null);
        if (cursor.moveToFirst()) {
            do {
                Cita c = new Cita();
                c.setId(cursor.getInt(cursor.getColumnIndexOrThrow("idcita")));
                c.setNombreDoctor(cursor.getString(cursor.getColumnIndexOrThrow("nombre_doctor")));
                c.setEspecialidad(cursor.getString(cursor.getColumnIndexOrThrow("especialidad")));
                c.setFechaHora(cursor.getString(cursor.getColumnIndexOrThrow("fecha_hora")));
                c.setEstado(cursor.getString(cursor.getColumnIndexOrThrow("estado")));
                citas.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return citas;
    }
}
