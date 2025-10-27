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

    private static final String GEMINI_API_KEY = "";
    //private final Executor executor = Executors.newFixedThreadPool(3);
    private final Executor executor = Executors.newSingleThreadExecutor(); //Runnable::run
    TextToSpeech tts;
    private GenerativeModelFutures model;


    public AdviceAI(Context context, String GEMINI_API_KEY) {
        model = GenerativeModelFutures.from(new GenerativeModel("gemini-2.0-flash", GEMINI_API_KEY));
        // Initializing TTS language and speechrate
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    int result = tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1);
                }
            }
        });
    }

    public void sendToGeminiText(String originalPrompt, Runnable onDone, Runnable onError) {
        // Keep your instructions but no image
        String formattedPrompt =
                "You are a technical assistant being integrated into an Android app for interactive data visualization using charts, images, and gesture input." +
                        "Do not over explain; answer directly and concisely. " +
                        "Follow these formatting principles. " +
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
}

