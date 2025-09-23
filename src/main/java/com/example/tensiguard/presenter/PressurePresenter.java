package com.example.tensiguard.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.tensiguard.database.PressureDBHelper;
import com.example.tensiguard.model.PressureReading;
import com.example.tensiguard.service.GeminiAPIService;
import java.util.Date;
import java.util.List;

/**
 * Presenter para manejar la lógica de negocio de las lecturas de presión
 * Implementa el patrón MVP
 */
public class PressurePresenter {
    private PressureDBHelper dbHelper;
    private GeminiAPIService apiService;
    private SharedPreferences preferences;

    public interface PressureView {
        void showLoading();
        void hideLoading();
        void showResult(PressureReading reading);
        void showError(String message);
        void showValidationError(String message);
    }

    public interface HistoryView {
        void showLoading();
        void hideLoading();
        void showHistory(List<PressureReading> readings);
        void showError(String message);
        void showEmptyHistory();
    }

    private PressureView pressureView;
    private HistoryView historyView;

    public PressurePresenter(Context context) {
        dbHelper = PressureDBHelper.getInstance(context);
        apiService = new GeminiAPIService();
        preferences = context.getSharedPreferences("TensiGuardPrefs", Context.MODE_PRIVATE);
    }

    public void setPressureView(PressureView view) {
        this.pressureView = view;
    }

    public void setHistoryView(HistoryView view) {
        this.historyView = view;
    }

    /**
     * Valida y procesa una nueva lectura de presión arterial
     */
    public void processNewReading(int systolic, int diastolic, String circumstances) {
        // Validar datos
        String validationError = validatePressureData(systolic, diastolic);
        if (validationError != null) {
            if (pressureView != null) {
                pressureView.showValidationError(validationError);
            }
            return;
        }

        if (pressureView != null) {
            pressureView.showLoading();
        }

        // Obtener datos del usuario
        UserData userData = getUserData();

        // Clasificar la presión
        String classification = PressureReading.classifyPressure(systolic, diastolic);

        // Crear objeto de lectura
        PressureReading reading = new PressureReading();
        reading.setSystolic(systolic);
        reading.setDiastolic(diastolic);
        reading.setCircumstances(circumstances);
        reading.setTimestamp(new Date());
        reading.setClassification(classification);
        reading.setUserName(userData.name);
        reading.setWeight(userData.weight);
        reading.setHeight(userData.height);
        reading.setGender(userData.gender);

        // Obtener análisis de la IA
        apiService.analyzeBloodPressure(
            systolic, diastolic, userData.name, userData.weight,
            userData.height, userData.gender, circumstances,
            new GeminiAPIService.ApiCallback() {
                @Override
                public void onSuccess(String aiResponse) {
                    reading.setAiReport(aiResponse);

                    // Guardar en base de datos
                    long id = dbHelper.insertPressureReading(reading);
                    reading.setId(id);

                    if (pressureView != null) {
                        pressureView.hideLoading();
                        pressureView.showResult(reading);
                    }
                }

                @Override
                public void onError(String error) {
                    // Guardar sin reporte de IA
                    reading.setAiReport("No se pudo obtener análisis de IA: " + error);

                    long id = dbHelper.insertPressureReading(reading);
                    reading.setId(id);

                    if (pressureView != null) {
                        pressureView.hideLoading();
                        pressureView.showResult(reading);
                    }
                }
            }
        );
    }

    /**
     * Valida los datos de presión arterial
     */
    private String validatePressureData(int systolic, int diastolic) {
        if (systolic <= 0 || diastolic <= 0) {
            return "Los valores de presión deben ser números positivos";
        }

        if (systolic < 50 || systolic > 300) {
            return "La presión sistólica debe estar entre 50 y 300 mmHg";
        }

        if (diastolic < 30 || diastolic > 200) {
            return "La presión diastólica debe estar entre 30 y 200 mmHg";
        }

        if (diastolic >= systolic) {
            return "La presión diastólica no puede ser mayor o igual que la sistólica";
        }

        return null; // Datos válidos
    }

    /**
     * Obtiene los datos del usuario de SharedPreferences
     */
    private UserData getUserData() {
        UserData userData = new UserData();
        userData.name = preferences.getString("user_name", "Usuario");
        userData.weight = preferences.getInt("user_weight", 70);
        userData.height = preferences.getInt("user_height", 170);
        userData.gender = preferences.getString("user_gender", "Masculino");
        return userData;
    }

    /**
     * Verifica si es la primera vez que se ejecuta la app
     */
    public boolean isFirstTime() {
        return preferences.getBoolean("is_first_time", true);
    }

    /**
     * Guarda los datos del usuario
     */
    public void saveUserData(String name, int weight, int height, String gender) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_name", name);
        editor.putInt("user_weight", weight);
        editor.putInt("user_height", height);
        editor.putString("user_gender", gender);
        editor.putBoolean("is_first_time", false);
        editor.apply();
    }

    /**
     * Carga el historial de lecturas
     */
    public void loadHistory() {
        if (historyView != null) {
            historyView.showLoading();
        }

        try {
            List<PressureReading> readings = dbHelper.getAllReadings();

            if (historyView != null) {
                historyView.hideLoading();

                if (readings.isEmpty()) {
                    historyView.showEmptyHistory();
                } else {
                    historyView.showHistory(readings);
                }
            }
        } catch (Exception e) {
            if (historyView != null) {
                historyView.hideLoading();
                historyView.showError("Error al cargar el historial: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene una lectura específica por ID
     */
    public PressureReading getReadingById(long id) {
        return dbHelper.getReadingById(id);
    }

    /**
     * Verifica si una lectura está en rango peligroso
     */
    public boolean isDangerousReading(int systolic, int diastolic) {
        return PressureReading.isDangerous(systolic, diastolic);
    }

    /**
     * Clase interna para datos del usuario
     */
    private static class UserData {
        String name;
        int weight;
        int height;
        String gender;
    }
}
