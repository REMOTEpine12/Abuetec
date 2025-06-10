package com.example.aplicacion;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class GeminiClient {
    private static final String API_KEY = "AIzaSyBXOcTuLhosRhhhwo0agK07wDnVLSvIUm8";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    private final OkHttpClient client = new OkHttpClient();

    public void sendMessage(Context context, String message, Callback callback) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        MiBD dbHelper = new MiBD(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder infoUsuario = new StringBuilder();
        StringBuilder datosMedicos = new StringBuilder();
        boolean hayDatos = false;

        // === Obtener información del usuario (nombre, edad, sexo) ===
        Cursor cursorUsuario = db.rawQuery("SELECT nombre, edad, genero FROM cuenta LIMIT 1", null);
        if (cursorUsuario.moveToFirst()) {
            String nombre = cursorUsuario.getString(0);
            int edad = cursorUsuario.getInt(1);
            String sexo = cursorUsuario.getString(2);

            infoUsuario.append("El usuario se llama ").append(nombre)
                    .append(", tiene ").append(edad).append(" años")
                    .append(" y es ").append(sexo.toLowerCase()).append(".\n");
        } else {
            infoUsuario.append("No se encontró información del perfil del usuario.\n");
        }
        cursorUsuario.close();

        // === Obtener últimos registros médicos ===
        String[] tipos = {"presión", "temperatura", "pulso", "glucosa", "oxigenación"};
        String[] etiquetas = {"Presión arterial", "Temperatura", "Pulso", "Glucosa", "Oxigenación"};
        String[] unidades = {"", "°C", " lpm", " mg/dL", " %"};

        datosMedicos.append("Estos son los datos médicos actuales del usuario:\n");

        for (int i = 0; i < tipos.length; i++) {
            Cursor c = db.rawQuery(
                    "SELECT valor FROM registro_salud WHERE tipo = ? ORDER BY fecha DESC LIMIT 1",
                    new String[]{tipos[i]}
            );
            if (c.moveToFirst()) {
                String valor = c.getString(0);
                datosMedicos.append("- ").append(etiquetas[i]).append(": ").append(valor).append(unidades[i]).append("\n");
                hayDatos = true;
            }
            c.close();
        }

        db.close();

        if (!hayDatos) {
            datosMedicos.setLength(0);
            datosMedicos.append("No se encontraron datos médicos recientes.\n");
        }

        // === Prompt final ===
        String systemPrompt = "Eres un asistente virtual diseñado para ayudar a personas adultas mayores. " +
                "Habla con frases cortas, sin tecnicismos, y con mucha claridad. Sé paciente, amable y tranquilizador.\n\n" +
                infoUsuario.toString() +
                datosMedicos.toString();

        if (hayDatos) {
            systemPrompt += "\nSi el usuario pide recomendaciones, por favor ten en cuenta estos valores.";
        } else {
            systemPrompt += "\nAun sin datos médicos, sugiere actividades seguras y útiles.";
        }

        try {
            String requestBodyString = "{\n" +
                    "  \"contents\": [\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"" + escapeJson(systemPrompt) + "\"}\n" +
                    "      ]\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"parts\": [\n" +
                    "        {\"text\": \"" + escapeJson(message) + "\"}\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            RequestBody body = RequestBody.create(JSON, requestBodyString);
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al generar la solicitud del asistente.", Toast.LENGTH_LONG).show();
        }
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
