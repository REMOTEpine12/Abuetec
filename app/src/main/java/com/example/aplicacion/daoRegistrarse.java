package com.example.aplicacion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class daoRegistrarse {
    private final SQLiteDatabase cx;
    private final MiBD dbhelper; // ✅ Declaración correcta

    public daoRegistrarse(Context c) {
        dbhelper = new MiBD(c); // inicializamos el helper
        cx = dbhelper.getWritableDatabase(); // obtenemos la conexión activa
    }

    public void cerrar() {
        dbhelper.close(); // ✅ cerrar correctamente
    }

    public boolean insertar(registrarse c) {
        ContentValues cv = new ContentValues();
        cv.put("nombre", c.getNombre());
        cv.put("contraseña", c.getPassword());
        cv.put("teléfono", c.getTelefono());
        cv.put("email", c.getCorreo());
        cv.put("edad", c.getEdad());
        cv.put("genero", c.getGenero());

        long result = cx.insert("cuenta", null, cv);
        Log.d("BD", "Insertando usuario: " + result);
        return result != -1;
    }

    public boolean eliminar(int id) {
        int result = cx.delete("cuenta", "idcuenta=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean editar(registrarse c) {
        ContentValues cv = new ContentValues();
        cv.put("nombre", c.getNombre());
        cv.put("contraseña", c.getPassword());
        cv.put("teléfono", c.getTelefono());
        cv.put("email", c.getCorreo());
        cv.put("edad", c.getEdad());
        cv.put("genero", c.getGenero());

        int result = cx.update("cuenta", cv, "idcuenta=?", new String[]{String.valueOf(c.getId())});
        return result > 0;
    }

    public ArrayList<registrarse> verTodos() {
        ArrayList<registrarse> registros = new ArrayList<>();
        Cursor cursor = cx.rawQuery("SELECT * FROM cuenta", null);

        if (cursor.moveToFirst()) {
            do {
                registrarse c = new registrarse();
                c.setId(cursor.getInt(cursor.getColumnIndex("idcuenta")));
                c.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                c.setPassword(cursor.getString(cursor.getColumnIndex("contraseña")));
                c.setTelefono(cursor.getString(cursor.getColumnIndex("teléfono")));
                c.setCorreo(cursor.getString(cursor.getColumnIndex("email")));
                c.setEdad(cursor.getInt(cursor.getColumnIndex("edad")));
                c.setGenero(cursor.getString(cursor.getColumnIndex("genero")));

                registros.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return registros;
    }

    public registrarse verUno(int id) {
        registrarse c = null;
        Cursor cursor = cx.rawQuery("SELECT * FROM cuenta WHERE idcuenta=?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            c = new registrarse();
            c.setId(cursor.getInt(cursor.getColumnIndex("idcuenta")));
            c.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
            c.setPassword(cursor.getString(cursor.getColumnIndex("contraseña")));
            c.setTelefono(cursor.getString(cursor.getColumnIndex("teléfono")));
            c.setCorreo(cursor.getString(cursor.getColumnIndex("email")));
            c.setEdad(cursor.getInt(cursor.getColumnIndex("edad")));
            c.setGenero(cursor.getString(cursor.getColumnIndex("genero")));
        }
        cursor.close();
        return c;
    }

    public boolean validarUsuario(String correo, String password){
        Cursor cursor = cx.rawQuery("SELECT * FROM cuenta WHERE email=? AND contraseña=?",
                new String[]{correo, password});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }
}
