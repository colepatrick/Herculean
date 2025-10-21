package com.example.herculean;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdviceAI {
    private static final String GEMINI_API_KEY = "";
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor(); //Runnable::run
    //private final Executor executor = Executors.newFixedThreadPool(3);
    private static final int RESULT_SPEECH = 1;


    // Google Gemini Toggle
    Boolean gemini_called = false;
}
