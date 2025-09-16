package com.example.tensiguard.service;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;

/**
 * Servicio para comunicarse con la Gemini API
 */
public class GeminiAPIService {
    private static final String API_KEY = "TU_API_KEY_AQUI"; // Reemplazar con la API key real
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + API_KEY;

    private OkHttpClient client;
    private Gson gson;

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public GeminiAPIService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    /**
     * Genera un análisis personalizado de presión arterial usando Gemini AI
     */
    public void analyzeBloodPressure(int systolic, int diastolic, String userName,
                                   int weight, int height, String gender,
                                   String circumstances, ApiCallback callback) {

        String prompt = buildPrompt(systolic, diastolic, userName, weight, height, gender, circumstances);

        new ApiRequestTask(callback).execute(prompt);
    }

    /**
     * Construye el prompt personalizado para la API
     */
    private String buildPrompt(int systolic, int diastolic, String userName,
                              int weight, int height, String gender, String circumstances) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("Actúa como un asistente médico especializado en presión arterial. ");
        prompt.append("Analiza los siguientes datos del paciente y proporciona un reporte personalizado:\n\n");

        prompt.append("DATOS DEL PACIENTE:\n");
        prompt.append("• Nombre: ").append(userName).append("\n");
        prompt.append("• Sexo: ").append(gender).append("\n");
        prompt.append("• Peso: ").append(weight).append(" kg\n");
        prompt.append("• Altura: ").append(height).append(" cm\n");
        prompt.append("• Presión sistólica: ").append(systolic).append(" mmHg\n");
        prompt.append("• Presión diastólica: ").append(diastolic).append(" mmHg\n");

        if (circumstances != null && !circumstances.trim().isEmpty()) {
            prompt.append("• Circunstancias: ").append(circumstances).append("\n");
        }

        prompt.append("\nPOR FAVOR PROPORCIONA:\n");
        prompt.append("1. Una evaluación clara del estado de la presión arterial\n");
        prompt.append("2. Explicación de qué significan estos valores\n");
        prompt.append("3. Recomendaciones específicas de estilo de vida\n");
        prompt.append("4. Consejos sobre cuándo buscar atención médica\n");
        prompt.append("5. Consejos de prevención personalizados\n\n");

        prompt.append("FORMATO: Responde en español, de manera clara y comprensible. ");
        prompt.append("Usa un tono profesional pero amigable. Máximo 300 palabras. ");
        prompt.append("IMPORTANTE: No reemplaces diagnóstico médico profesional.");

        return prompt.toString();
    }

    /**
     * Clase interna para realizar la petición HTTP de forma asíncrona
     */
    private class ApiRequestTask extends AsyncTask<String, Void, String> {
        private ApiCallback callback;
        private String errorMessage;

        public ApiRequestTask(ApiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... prompts) {
            try {
                String prompt = prompts[0];

                // Construir el JSON para la API de Gemini
                JsonObject requestBody = new JsonObject();
                JsonArray contents = new JsonArray();
                JsonObject content = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject part = new JsonObject();

                part.addProperty("text", prompt);
                parts.add(part);
                content.add("parts", parts);
                contents.add(content);
                requestBody.add("contents", contents);

                // Configurar la petición HTTP
                RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();

                // Ejecutar la petición
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseGeminiResponse(responseBody);
                } else {
                    errorMessage = "Error en la API: " + response.code() + " - " + response.message();
                    return null;
                }

            } catch (IOException e) {
                errorMessage = "Error de conexión: " + e.getMessage();
                return null;
            } catch (Exception e) {
                errorMessage = "Error inesperado: " + e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onError(errorMessage != null ? errorMessage : "Error desconocido");
            }
        }
    }

    /**
     * Parsea la respuesta de la API de Gemini
     */
    private String parseGeminiResponse(String responseBody) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            if (jsonResponse.has("candidates")) {
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    if (firstCandidate.has("content")) {
                        JsonObject content = firstCandidate.getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts.size() > 0) {
                                JsonObject firstPart = parts.get(0).getAsJsonObject();
                                if (firstPart.has("text")) {
                                    return firstPart.get("text").getAsString();
                                }
                            }
                        }
                    }
                }
            }

            return "No se pudo obtener una respuesta válida de la IA.";

        } catch (Exception e) {
            return "Error al procesar la respuesta de la IA: " + e.getMessage();
        }
    }
}
