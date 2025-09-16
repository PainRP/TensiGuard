package com.example.tensiguard.model;

public class UserProfile {
    private String name;
    private float weight;
    private float height;
    private String gender;
    private String emergencyContact;

    public UserProfile() {}

    public UserProfile(String name, float weight, float height, String gender) {
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    // Calcular IMC
    public float getBMI() {
        if (height > 0 && weight > 0) {
            float heightInMeters = height / 100f;
            return weight / (heightInMeters * heightInMeters);
        }
        return 0;
    }

    public String getBMICategory() {
        float bmi = getBMI();
        if (bmi < 18.5) return "Bajo peso";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Sobrepeso";
        else return "Obesidad";
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty()
            && weight > 0 && height > 0
            && gender != null && !gender.trim().isEmpty();
    }
}
