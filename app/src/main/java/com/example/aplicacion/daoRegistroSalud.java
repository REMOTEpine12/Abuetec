package com.example.aplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class daoRegistroSalud {
    private final SQLiteDatabase cx;
    private final MiBD dbhelper;

    public daoRegistroSalud(Context c) {
        dbhelper = new MiBD(c);
        cx = dbhelper.getWritableDatabase();
    }

    public void cerrar() {
        dbhelper.close();
    }

    public boolean insertarRegistro(String tipo, String valor) {
        ContentValues cv = new ContentValues();
        cv.put("tipo", tipo);
        cv.put("valor", valor);

        long result = cx.insert("registro_salud", null, cv);
        return result != -1;
    }

    public ArrayList<registroSalud> verTodos() {
        ArrayList<registroSalud> lista = new ArrayList<>();
        Cursor cursor = cx.rawQuery("SELECT * FROM registro_salud ORDER BY fecha DESC", null);

        if (cursor.moveToFirst()) {
            do {
                registroSalud r = new registroSalud();
                r.setId(cursor.getInt(cursor.getColumnIndex("idregistro")));
                r.setTipo(cursor.getString(cursor.getColumnIndex("tipo")));
                r.setValor(cursor.getString(cursor.getColumnIndex("valor")));
                r.setFecha(cursor.getString(cursor.getColumnIndex("fecha")));
                lista.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
