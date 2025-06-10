package com.example.aplicacion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class HotwordListener {
    private Context context;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private OnCommandDetectedListener callback;
    private Handler handler = new Handler();

    public interface OnCommandDetectedListener {
        void onCommandDetected(String comando);
    }

    public HotwordListener(Context context, OnCommandDetectedListener listener) {
        this.context = context;
        this.callback = listener;
        initRecognizer();
    }

    private void initRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                //Toast.makeText(context, "🎤 Escuchando...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    for (String phrase : matches) {
                        phrase = phrase.toLowerCase();
                        if (phrase.contains("asistente") || phrase.contains("chatbot")) {
                            callback.onCommandDetected("chatbot");
                        } else if (phrase.contains("citas")) {
                            callback.onCommandDetected("citas");
                        } else if (phrase.contains("medicamento") || phrase.contains("medicacion")) {
                            callback.onCommandDetected("medicacion");
                        } else if (phrase.contains("tracker")) {
                            callback.onCommandDetected("tracker");
                        } else if (phrase.contains("inicio") || phrase.contains("ir a inicio")) {
                            callback.onCommandDetected("inicio");
                        }
                    }
                }
                restartListening();
            }

            @Override public void onError(int error) {
                handler.postDelayed(() -> speechRecognizer.startListening(recognizerIntent), 1000);
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void restartListening() {
        handler.postDelayed(() -> speechRecognizer.startListening(recognizerIntent), 1000);
    }

    public void start() {
        speechRecognizer.startListening(recognizerIntent);
    }

    public void stop() {
        speechRecognizer.stopListening();
        speechRecognizer.destroy();
    }
}