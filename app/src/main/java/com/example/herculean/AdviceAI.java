package com.example.herculean;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AdviceAI {
    // Gemini
    private static final String GEMINI_API_KEY = "";
    //private final Executor executor = Executors.newFixedThreadPool(3);
    private final Executor executor = Executors.newSingleThreadExecutor(); //Runnable::run
    private GenerativeModelFutures model;
    // TTS
    TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean allowInterruption = false;


    public interface onResultTextCallback {
        void onResultText(String text);
    }

    public void setAllowInterrupt(boolean allow) {
        this.allowInterruption = allow;
    }

    public AdviceAI(Context context, String GEMINI_API_KEY) {
        model = GenerativeModelFutures.from(new GenerativeModel("gemini-2.0-flash", GEMINI_API_KEY));
        // Initializing TTS language and speechrate
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    int result = tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1f);
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        ttsReady = true;
                        Log.i(TAG, "TTS initialized successfully.");
                    }
                    else {
                        Log.e(TAG, "TTS language not supported or missing data.");
                    }
                }
                else {
                    Log.e(TAG, "TTS initialization failed.");
                }
            }
        });
    }

    public void sendToGeminiText(String originalPrompt, Runnable onDone, Runnable onError) {
        String formattedPrompt =
                "You are a knowledgeable fitness and workout coach integrated into an Android app that gives short, practical exercise tips through voice feedback. " +
                        "Provide clear, motivational, and concise advice about workouts, training form, recovery, or fitness routines. " +
                        "Avoid long explanations or unnecessary details — keep responses under 3 sentences and easy to understand when spoken aloud. " +
                        "Use an encouraging and positive tone. " +
                        originalPrompt;

        Content content = new Content.Builder()
                .addText(formattedPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String responseText = result != null ? result.getText() : null;
                if (responseText == null || responseText.isEmpty()) {
                    tts.speak("I didn't get any response.", TextToSpeech.QUEUE_FLUSH, null);
                    if (onError != null) onError.run();
                } else {
                    tts.speak(responseText, TextToSpeech.QUEUE_FLUSH, null);
                    if (onDone != null) onDone.run();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                tts.speak("Error: " + t.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
                if (onError != null) onError.run();
                Log.e(TAG, "Gemini call failed", t);
            }
        }, executor);
    }

    public void release() {
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            }
            catch (Exception e) {
                Log.e(TAG, "Error shutting down TTS", e);
            }
        }
    }
}


