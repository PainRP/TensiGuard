package com.example.tensiguard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.tensiguard.model.PressureReading;
import com.example.tensiguard.presenter.PressurePresenter;

/**
 * Activity para mostrar los resultados del análisis de presión arterial
 */
public class ResultActivity extends AppCompatActivity {
    private TextView tvClassification, tvPressureValues, tvTimestamp, tvAiReport;
    private Button btnEmergencyCall, btnBack;
    private CardView cardEmergency;

    private PressurePresenter presenter;
    private PressureReading currentReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        presenter = new PressurePresenter(this);

        initViews();
        loadReadingData();
        setupListeners();
    }

    private void initViews() {
        tvClassification = findViewById(R.id.tv_classification);
        tvPressureValues = findViewById(R.id.tv_pressure_values);
        tvTimestamp = findViewById(R.id.tv_timestamp);
        tvAiReport = findViewById(R.id.tv_ai_report);
        btnEmergencyCall = findViewById(R.id.btn_emergency_call);
        btnBack = findViewById(R.id.btn_back);
        cardEmergency = findViewById(R.id.card_emergency);
    }

    private void loadReadingData() {
        long readingId = getIntent().getLongExtra("reading_id", -1);

        if (readingId != -1) {
            currentReading = presenter.getReadingById(readingId);

            if (currentReading != null) {
                displayReadingData();
            } else {
                tvClassification.setText("Error al cargar los datos");
                btnBack.setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayReadingData() {
        // Mostrar clasificación con emoji
        tvClassification.setText(currentReading.getClassification());

        // Aplicar color según la clasificación
        setClassificationColor();

        // Mostrar valores de presión
        String pressureText = currentReading.getSystolic() + "/" + currentReading.getDiastolic() + " mmHg";
        tvPressureValues.setText(pressureText);

        // Mostrar fecha y hora
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(
            "dd/MM/yyyy 'a las' HH:mm", java.util.Locale.getDefault());
        tvTimestamp.setText("Registrado el " + dateFormat.format(currentReading.getTimestamp()));

        // Mostrar reporte de IA
        if (currentReading.getAiReport() != null && !currentReading.getAiReport().isEmpty()) {
            tvAiReport.setText(currentReading.getAiReport());
        } else {
            tvAiReport.setText("Análisis no disponible");
        }

        // Mostrar botón de emergencia si es necesario
        if (presenter.isDangerousReading(currentReading.getSystolic(), currentReading.getDiastolic())) {
            cardEmergency.setVisibility(View.VISIBLE);
        } else {
            cardEmergency.setVisibility(View.GONE);
        }
    }

    private void setClassificationColor() {
        String classification = currentReading.getClassification();
        int color;

        if (classification.contains("Normal")) {
            color = getResources().getColor(android.R.color.holo_green_dark);
        } else if (classification.contains("Elevada")) {
            color = getResources().getColor(android.R.color.holo_orange_light);
        } else if (classification.contains("Hipertensión")) {
            color = getResources().getColor(android.R.color.holo_orange_dark);
        } else if (classification.contains("Crisis")) {
            color = getResources().getColor(android.R.color.holo_red_dark);
        } else if (classification.contains("Baja")) {
            color = getResources().getColor(android.R.color.holo_blue_light);
        } else {
            color = getResources().getColor(android.R.color.black);
        }

        tvClassification.setTextColor(color);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEmergencyCall.setOnClickListener(v -> makeEmergencyCall());
    }

    private void makeEmergencyCall() {
        // Número de emergencia (911 en México, 112 en Europa, etc.)
        String emergencyNumber = "911";

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));

        try {
            startActivity(callIntent);
        } catch (SecurityException e) {
            // Si no tiene permisos, abrir el marcador
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + emergencyNumber));
            startActivity(dialIntent);
        }
    }

    @Override
    public void onBackPressed() {
        // Regresar a MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
