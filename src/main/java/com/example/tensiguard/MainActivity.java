package com.example.tensiguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.tensiguard.model.PressureReading;
import com.example.tensiguard.presenter.PressurePresenter;
import com.google.android.material.navigation.NavigationView;

/**
 * Activity principal para ingresar datos de presión arterial
 */
public class MainActivity extends AppCompatActivity implements
    PressurePresenter.PressureView, NavigationView.OnNavigationItemSelectedListener {

    private EditText etSystolic, etDiastolic, etCircumstances;
    private Button btnAnalyze, btnTutorial;
    private ProgressBar progressBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private PressurePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new PressurePresenter(this);
        presenter.setPressureView(this);

        initViews();
        setupToolbar();
        setupNavigation();
        setupListeners();
    }

    private void initViews() {
        etSystolic = findViewById(R.id.et_systolic);
        etDiastolic = findViewById(R.id.et_diastolic);
        etCircumstances = findViewById(R.id.et_circumstances);
        btnAnalyze = findViewById(R.id.btn_analyze);
        btnTutorial = findViewById(R.id.btn_tutorial);
        progressBar = findViewById(R.id.progress_bar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupListeners() {
        btnAnalyze.setOnClickListener(v -> analyzeBloodPressure());
        btnTutorial.setOnClickListener(v -> showTutorial());
    }

    private void analyzeBloodPressure() {
        String systolicStr = etSystolic.getText().toString().trim();
        String diastolicStr = etDiastolic.getText().toString().trim();
        String circumstances = etCircumstances.getText().toString().trim();

        if (systolicStr.isEmpty()) {
            etSystolic.setError("Ingresa la presión sistólica");
            return;
        }

        if (diastolicStr.isEmpty()) {
            etDiastolic.setError("Ingresa la presión diastólica");
            return;
        }

        try {
            int systolic = Integer.parseInt(systolicStr);
            int diastolic = Integer.parseInt(diastolicStr);

            presenter.processNewReading(systolic, diastolic, circumstances);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor ingresa valores numéricos válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);
        btnAnalyze.setText("Analizando...");
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnAnalyze.setEnabled(true);
        btnAnalyze.setText("Analizar Presión");
    }

    @Override
    public void showResult(PressureReading reading) {
        // Limpiar campos
        etSystolic.setText("");
        etDiastolic.setText("");
        etCircumstances.setText("");

        // Ir a ResultActivity
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("reading_id", reading.getId());
        startActivity(intent);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showValidationError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Ya estamos en home
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_tutorial) {
            showTutorial();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
