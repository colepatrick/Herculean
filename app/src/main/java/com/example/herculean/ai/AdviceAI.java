package com.example.herculean.ai;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.example.herculean.GlobalData;
import com.example.herculean.workout.Workout;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AdviceAI {

    private static AdviceAI instance;

    // Gemini
    private final Executor executor = Executors.newSingleThreadExecutor(); //Runnable::run
    private GenerativeModelFutures model;

    // TTS
    TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean allowInterruption = false;
    private static final String gemini_api_key = GlobalData.gemini_api_key;

    private String messageHistory = "";

    public interface onResultTextCallback {
        void onResultText(String text);
        void onError(String errorMessage);
    }

    public static synchronized AdviceAI getInstance(Context context) {
        if (instance == null) {
            instance = new AdviceAI(context.getApplicationContext());
        }
        return instance;
    }

    public void setAllowInterrupt(boolean allow) {
        this.allowInterruption = allow;
    }

    private AdviceAI(Context context) {
        String apiKey = gemini_api_key;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            apiKey = gemini_api_key;
        }

        boolean hasApiKey = apiKey != null && !apiKey.trim().isEmpty();
        if (!hasApiKey) {
            Toast.makeText(context, "Gemini API key is missing.", Toast.LENGTH_SHORT).show();
            apiKey = "";
        }

        model = GenerativeModelFutures.from(new GenerativeModel("gemini-2.0-flash", apiKey));
        // Initializing TTS language and speechrate
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    int result = tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1.0f);
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        ttsReady = true;
                        Log.d(TAG, "TTS initialized successfully.");
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

    public void sendToGeminiText(String userPrompt, boolean speakResponse, onResultTextCallback callBack, Runnable onDone, Runnable onError) {
        String context = "";
        for(Workout workout : GlobalData.currentUser.getRecentWorkouts(LocalDate.now().minusDays(7)).getWorkouts()) {
            context += workout.toString() + " ";
        }
        context += "Goal: " + GlobalData.currentUser.getUserGoal().toString() +
                "\nSchedule: " + GlobalData.currentUser.getUserSchedule().toString();

        messageHistory += "User: " + userPrompt;
        String formattedPrompt =
                "You are a knowledgeable fitness and workout coach integrated into an Android app that gives short, practical exercise tips through voice feedback. " +
                        "Provide clear, motivational, and concise advice about workouts, training form, recovery, or fitness routines. " +
                        "Avoid long explanations or unnecessary details — keep responses under 3 sentences and easy to understand when spoken aloud. " +
                        "Use an encouraging and positive tone. Here is a list of workouts, given with date, workout description, muscle group, and amount:" +
                        context + "Here is the full user message history, the last one is unanswered. Answer it." + messageHistory;

        Content content = new Content.Builder()
                .addText(formattedPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> future = model.generateContent(content);

        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String responseText = (result != null) ? result.getText() : null;
                messageHistory += "AI: " + responseText;
                if (responseText == null || responseText.isEmpty()) {
                    // Errors
                    if (speakResponse && ttsReady) {
                        safeSpeak("Prompt did not go through.");
                    }
                    if (callBack != null) {
                        callBack.onError("Prompt did not go through.");
                    }
                    if (onError != null) {
                        onError.run();
                    }
                    return;
                }
                // Success
                if (callBack != null) {
                    callBack.onResultText(responseText);
                }
                if (speakResponse && ttsReady) {
                    safeSpeak(responseText);
                }
                if (onDone != null) {
                    onDone.run();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "Gemini call failed", t);
                String errorMsg = "Error: " + t.getMessage();
                if (speakResponse && ttsReady) {
                    safeSpeak(errorMsg);
                }
                if (callBack != null) {
                    callBack.onError(errorMsg);
                }
                if (onError != null) onError.run();
            }
        }, executor);
    }

    private void safeSpeak(String text) {
        if (!ttsReady) {
            return;
        }
        if (!allowInterruption) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
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


