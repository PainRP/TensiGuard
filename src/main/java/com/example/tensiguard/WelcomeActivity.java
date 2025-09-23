package com.example.tensiguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tensiguard.presenter.PressurePresenter;

/**
 * Activity de bienvenida para configuración inicial del usuario
 */
public class WelcomeActivity extends AppCompatActivity {
    private EditText etName, etWeight, etHeight;
    private RadioGroup rgGender;
    private Button btnSave;
    private PressurePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        presenter = new PressurePresenter(this);

        // Verificar si no es la primera vez
        if (!presenter.isFirstTime()) {
            startMainActivity();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etWeight = findViewById(R.id.et_weight);
        etHeight = findViewById(R.id.et_height);
        rgGender = findViewById(R.id.rg_gender);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void saveUserData() {
        String name = etName.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        // Validaciones
        if (name.isEmpty()) {
            etName.setError("El nombre es requerido");
            return;
        }

        if (weightStr.isEmpty()) {
            etWeight.setError("El peso es requerido");
            return;
        }

        if (heightStr.isEmpty()) {
            etHeight.setError("La altura es requerida");
            return;
        }

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Por favor selecciona el sexo", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int weight = Integer.parseInt(weightStr);
            int height = Integer.parseInt(heightStr);

            if (weight < 20 || weight > 300) {
                etWeight.setError("Peso debe estar entre 20 y 300 kg");
                return;
            }

            if (height < 100 || height > 250) {
                etHeight.setError("Altura debe estar entre 100 y 250 cm");
                return;
            }

            RadioButton selectedGender = findViewById(selectedGenderId);
            String gender = selectedGender.getText().toString();

            // Guardar datos
            presenter.saveUserData(name, weight, height, gender);

            // Ir a MainActivity
            startMainActivity();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
