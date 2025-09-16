package com.example.tensiguard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity para mostrar el tutorial de la aplicación
 */
public class TutorialActivity extends AppCompatActivity {
    private TextView tvTutorialContent;
    private Button btnClose;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        initViews();
        setupToolbar();
        setupTutorialContent();
        setupListeners();
    }

    private void initViews() {
        tvTutorialContent = findViewById(R.id.tv_tutorial_content);
        btnClose = findViewById(R.id.btn_close);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tutorial - TensiGuard");

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTutorialContent() {
        String tutorialText = "🩺 **BIENVENIDO A TENSIGUARD** 🩺\n\n" +
                "TensiGuard es tu asistente personal para monitorear tu presión arterial de manera inteligente.\n\n" +

                "📋 **CÓMO USAR LA APP:**\n\n" +

                "1️⃣ **REGISTRO DE PRESIÓN:**\n" +
                "• Ingresa tu presión sistólica (número mayor)\n" +
                "• Ingresa tu presión diastólica (número menor)\n" +
                "• Opcionalmente, añade circunstancias (ej: \"después de ejercicio\")\n" +
                "• Presiona \"Analizar Presión\"\n\n" +

                "2️⃣ **ANÁLISIS INTELIGENTE:**\n" +
                "• La app clasifica tu presión según estándares médicos\n" +
                "• Obtiene recomendaciones personalizadas de IA\n" +
                "• Guarda automáticamente en tu historial\n\n" +

                "3️⃣ **CLASIFICACIONES:**\n" +
                "• 📉 **Baja:** Menos de 90/60\n" +
                "• ✅ **Normal:** 120/80 o menos\n" +
                "• ⚠️ **Elevada:** 121-129 sistólica\n" +
                "• 🔸 **Hipertensión Etapa 1:** 130-139/80-89\n" +
                "• 🔶 **Hipertensión Etapa 2:** 140-179/90-119\n" +
                "• 🚨 **Crisis:** 180/120 o más\n\n" +

                "4️⃣ **FUNCIONES DE SEGURIDAD:**\n" +
                "• Si tu presión está en nivel crítico (≥180/120), aparecerá un botón de emergencia\n" +
                "• Puedes llamar directamente a servicios de emergencia\n\n" +

                "5️⃣ **HISTORIAL:**\n" +
                "• Accede desde el menú hamburguesa (☰)\n" +
                "• Ve todas tus lecturas ordenadas por fecha\n" +
                "• Toca cualquier registro para ver el análisis completo\n\n" +

                "6️⃣ **NAVEGACIÓN:**\n" +
                "• **Inicio:** Registrar nueva lectura\n" +
                "• **Historial:** Ver lecturas anteriores\n" +
                "• **Tutorial:** Esta ayuda (botón !)\n\n" +

                "⚠️ **IMPORTANTE:**\n" +
                "• Esta app NO reemplaza el diagnóstico médico profesional\n" +
                "• Consulta siempre con tu médico para decisiones de salud\n" +
                "• Usa un tensiómetro calibrado para mediciones precisas\n\n" +

                "💡 **CONSEJOS:**\n" +
                "• Mide tu presión en reposo\n" +
                "• Evita cafeína 30 min antes\n" +
                "• Toma múltiples mediciones\n" +
                "• Registra el contexto (estrés, ejercicio, etc.)\n\n" +

                "¡Mantén un control regular de tu presión arterial para una mejor salud cardiovascular! 💪❤️";

        tvTutorialContent.setText(tutorialText);
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }
}
