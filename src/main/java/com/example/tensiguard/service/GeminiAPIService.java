package com.example.tensiguard.service;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;

/**
 * Servicio para comunicarse con la Gemini API de Google
 *
 * Este servicio permite enviar consultas a la API de Gemini para obtener análisis
 * personalizados de presión arterial usando inteligencia artificial.
 *
 * Para usar este servicio:
 * 1. Obtén una API key de Google AI Studio (https://makersuite.google.com/app/apikey)
 * 2. Reemplaza "TU_API_KEY_AQUI" con tu API key real
 * 3. Asegúrate de tener permisos de internet en el AndroidManifest.xml
 *
 * @author TensiGuard Team
 * @version 1.0
 */
public class GeminiAPIService {

    // CONFIGURACIÓN DE LA API
    private static final String API_KEY = "Apikey"; // TODO: Reemplazar con la API key real
    // URL correcta según la documentación oficial de Gemini API v1
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent";

    // Cliente HTTP para realizar las peticiones
    private OkHttpClient client;
    // Parser JSON para manejar las respuestas
    private Gson gson;

    /**
     * Interfaz de callback para manejar las respuestas de la API
     * Implementa esta interfaz para recibir los resultados de forma asíncrona
     */
    public interface ApiCallback {
        /**
         * Se ejecuta cuando la API responde exitosamente
         * @param response Análisis generado por la IA
         */
        void onSuccess(String response);

        /**
         * Se ejecuta cuando ocurre un error
         * @param error Descripción del error ocurrido
         */
        void onError(String error);
    }

    /**
     * Constructor del servicio
     * Inicializa el cliente HTTP y el parser JSON
     */
    public GeminiAPIService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    /**
     * Genera un análisis personalizado de presión arterial usando Gemini AI
     *
     * Este método construye un prompt personalizado con los datos del paciente
     * y lo envía a la API de Gemini para obtener un análisis detallado.
     *
     * @param systolic Presión sistólica en mmHg
     * @param diastolic Presión diastólica en mmHg
     * @param userName Nombre del usuario
     * @param weight Peso en kilogramos
     * @param height Altura en centímetros
     * @param gender Género del usuario
     * @param circumstances Circunstancias especiales (opcional)
     * @param callback Callback para recibir la respuesta
     */
    public void analyzeBloodPressure(int systolic, int diastolic, String userName,
                                   int weight, int height, String gender,
                                   String circumstances, ApiCallback callback) {

        // Construir el prompt personalizado
        String prompt = buildPrompt(systolic, diastolic, userName, weight, height, gender, circumstances);

        // Ejecutar la petición de forma asíncrona
        new ApiRequestTask(callback).execute(prompt);
    }

    /**
     * Construye el prompt personalizado para la API de Gemini
     *
     * Este método crea un prompt estructurado que incluye:
     * - Datos del paciente
     * - Instrucciones específicas para la IA
     * - Formato de respuesta deseado
     *
     * @param systolic Presión sistólica
     * @param diastolic Presión diastólica
     * @param userName Nombre del usuario
     * @param weight Peso del usuario
     * @param height Altura del usuario
     * @param gender Género del usuario
     * @param circumstances Circunstancias adicionales
     * @return Prompt formateado para la API
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
     *
     * Esta clase extiende AsyncTask para evitar bloquear el hilo principal
     * mientras se realiza la petición a la API de Gemini.
     */
    private class ApiRequestTask extends AsyncTask<String, Void, String> {
        private ApiCallback callback;
        private String errorMessage;

        /**
         * Constructor de la tarea asíncrona
         * @param callback Callback para devolver el resultado
         */
        public ApiRequestTask(ApiCallback callback) {
            this.callback = callback;
        }

        /**
         * Ejecuta la petición HTTP en un hilo de fondo
         * @param prompts Array con el prompt a enviar
         * @return Respuesta de la API o null si hay error
         */
        @Override
        protected String doInBackground(String... prompts) {
            try {
                String prompt = prompts[0];

                // Construir el JSON según el formato oficial de Gemini API
                JsonObject requestBody = buildRequestBody(prompt);

                // Configurar la petición HTTP con la API key en el header
                RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-goog-api-key", API_KEY) // Header correcto para la API key
                        .build();

                // Ejecutar la petición
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseGeminiResponse(responseBody);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles";
                    errorMessage = "Error en la API: " + response.code() + " - " + response.message() + "\nDetalles: " + errorBody;
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

        /**
         * Se ejecuta en el hilo principal después de completar la petición
         * @param result Resultado de la petición o null si hay error
         */
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
     * Construye el cuerpo de la petición según el formato oficial de Gemini API
     *
     * Estructura JSON:
     * {
     *   "contents": [{
     *     "parts": [{"text": "prompt aquí"}]
     *   }]
     * }
     *
     * @param prompt Texto del prompt para la IA
     * @return JsonObject con la estructura correcta
     */
    private JsonObject buildRequestBody(String prompt) {
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

        return requestBody;
    }

    /**
     * Parsea la respuesta de la API de Gemini
     *
     * Estructura esperada de la respuesta:
     * {
     *   "candidates": [{
     *     "content": {
     *       "parts": [{"text": "respuesta aquí"}]
     *     }
     *   }]
     * }
     *
     * @param responseBody JSON de respuesta de la API
     * @return Texto generado por la IA o mensaje de error
     */
    private String parseGeminiResponse(String responseBody) {
        try {
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            // Verificar si hay candidatos en la respuesta
            if (jsonResponse.has("candidates")) {
                JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();

                    // Extraer el contenido del primer candidato
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

            // Si no se encuentra el formato esperado, verificar si hay error
            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMessage = error.has("message") ? error.get("message").getAsString() : "Error desconocido";
                return "Error de la API: " + errorMessage;
            }

            return "No se pudo obtener una respuesta válida de la IA. Verifica tu API key y conexión.";

        } catch (Exception e) {
            return "Error al procesar la respuesta de la IA: " + e.getMessage();
        }
    }
}
