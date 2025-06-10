package com.example.aplicacion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {
    private LinearLayout messageContainer;
    private ScrollView scrollView;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private View chatInputLayout;
    private HotwordListener hotwordListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatbot);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a vistas
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        messageContainer = findViewById(R.id.messageContainer);
        scrollView = findViewById(R.id.scrollMessages);
        chatInputLayout = findViewById(R.id.chatInputLayout);
        GeminiClient geminiClient = new GeminiClient();
        iniciarHotwordListener();
        // Ajuste visual al aparecer teclado
        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
            boolean keyboardVisible = heightDiff > 250;
            chatInputLayout.setTranslationY(keyboardVisible ? -heightDiff / 2f : 0);
        });

        // Botón de enviar mensaje
        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                agregarMensajeUsuario(userMessage);
                editTextMessage.setText("");
                ocultarTeclado();
                scrollAlFinal();

                // Llamada a GeminiClient con contexto
                geminiClient.sendMessage(this, userMessage, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        Log.e("GeminiError", "Fallo al conectar con Gemini: " + e.getMessage());
                        runOnUiThread(() -> {
                            Toast.makeText(ChatbotActivity.this, "Error al conectar con el asistente. Revisa tu conexión." + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String json = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                JSONArray candidates = jsonObject.getJSONArray("candidates");
                                JSONObject firstCandidate = candidates.getJSONObject(0);
                                JSONObject content = firstCandidate.getJSONObject("content");
                                JSONArray parts = content.getJSONArray("parts");
                                String replyText = parts.getJSONObject(0).getString("text");

                                String limpio = limpiarFormatoMarkdown(replyText);

                                runOnUiThread(() -> {
                                    agregarMensajeBot(limpio);
                                    scrollAlFinal();
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) scrollAlFinal();
        });
    }

    private void agregarMensajeUsuario(String mensaje) {
        TextView userTextView = new TextView(this);
        userTextView.setText(mensaje);
        userTextView.setTextSize(16);
        userTextView.setTextColor(Color.WHITE);
        userTextView.setBackgroundColor(Color.parseColor("#00BCD4"));
        userTextView.setPadding(24, 16, 24, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(48, 12, 0, 12);
        params.gravity = Gravity.END;
        userTextView.setLayoutParams(params);

        messageContainer.addView(userTextView);
    }

    private void agregarMensajeBot(String mensaje) {
        TextView botTextView = new TextView(this);
        botTextView.setText(mensaje);
        botTextView.setTextSize(16);
        botTextView.setTextColor(Color.BLACK);
        botTextView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        botTextView.setPadding(24, 16, 24, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 12, 48, 12);
        botTextView.setLayoutParams(params);

        messageContainer.addView(botTextView);
    }

    private void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void scrollAlFinal() {
        scrollView.postDelayed(() -> scrollView.fullScroll(View.FOCUS_DOWN), 100);
    }

    private String limpiarFormatoMarkdown(String texto) {
        return texto
                .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
                .replaceAll("__(.*?)__", "$1")
                .replaceAll("\\*(.*?)\\*", "$1")
                .replaceAll("_(.*?)_", "$1")
                .replaceAll("`(.*?)`", "$1");
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
    public void home(View view) {
        Intent intent = new Intent(this, activity_main.class);
        startActivity(intent);
    }
}
