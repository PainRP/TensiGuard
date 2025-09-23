package com.example.tensiguard.model;

import java.util.Date;

/**
 * Modelo de datos para una lectura de presión arterial
 */
public class PressureReading {
    private long id;
    private int systolic;
    private int diastolic;
    private String circumstances;
    private Date timestamp;
    private String aiReport;
    private String classification;
    private String userName;
    private int weight;
    private int height;
    private String gender;
    private String recommendations; // Added field

    // Constructor vacío
    public PressureReading() {}

    // Constructor completo
    public PressureReading(int systolic, int diastolic, String circumstances,
                          Date timestamp, String aiReport, String classification,
                          String userName, int weight, int height, String gender, String recommendations) { // Added recommendations to constructor
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.circumstances = circumstances;
        this.timestamp = timestamp;
        this.aiReport = aiReport;
        this.classification = classification;
        this.userName = userName;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.recommendations = recommendations; // Initialize recommendations
    }

    // Getters y Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getSystolic() { return systolic; }
    public void setSystolic(int systolic) { this.systolic = systolic; }

    public int getDiastolic() { return diastolic; }
    public void setDiastolic(int diastolic) { this.diastolic = diastolic; }

    public String getCircumstances() { return circumstances; }
    public void setCircumstances(String circumstances) { this.circumstances = circumstances; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getAiReport() { return aiReport; }
    public void setAiReport(String aiReport) { this.aiReport = aiReport; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // Added methods
    public String getPressureString() {
        return String.format("%d/%d mmHg", systolic, diastolic);
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    /**
     * Clasifica la presión arterial según los estándares de la OMS
     */
    public static String classifyPressure(int systolic, int diastolic) {
        if (systolic < 90 || diastolic < 60) {
            return "Baja 📉";
        } else if (systolic <= 120 && diastolic <= 80) {
            return "Normal ✅";
        } else if (systolic <= 129 && diastolic <= 80) {
            return "Elevada ⚠️";
        } else if (systolic <= 139 || diastolic <= 89) {
            return "Hipertensión Etapa 1 🔸";
        } else if (systolic <= 179 || diastolic <= 119) {
            return "Hipertensión Etapa 2 🔶";
        } else {
            return "Crisis Hipertensiva 🚨";
        }
    }

    /**
     * Determina si la presión está en rango peligroso
     */
    public static boolean isDangerous(int systolic, int diastolic) {
        return systolic >= 180 || diastolic >= 120;
    }
}
