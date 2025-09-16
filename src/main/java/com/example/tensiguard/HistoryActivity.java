package com.example.tensiguard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tensiguard.adapter.PressureHistoryAdapter;
import com.example.tensiguard.model.PressureReading;
import com.example.tensiguard.presenter.PressurePresenter;
import java.util.List;

/**
 * Activity para mostrar el historial de lecturas de presión arterial
 */
public class HistoryActivity extends AppCompatActivity implements PressurePresenter.HistoryView {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyHistory;
    private Toolbar toolbar;

    private PressurePresenter presenter;
    private PressureHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        presenter = new PressurePresenter(this);
        presenter.setHistoryView(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_history);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyHistory = findViewById(R.id.tv_empty_history);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Historial de Presión");

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new PressureHistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Listener para clicks en elementos del historial
        adapter.setOnItemClickListener(reading -> {
            Intent intent = new Intent(HistoryActivity.this, ResultActivity.class);
            intent.putExtra("reading_id", reading.getId());
            startActivity(intent);
        });
    }

    private void loadHistory() {
        presenter.loadHistory();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyHistory.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showHistory(List<PressureReading> readings) {
        recyclerView.setVisibility(View.VISIBLE);
        tvEmptyHistory.setVisibility(View.GONE);
        adapter.updateReadings(readings);
    }

    @Override
    public void showError(String message) {
        recyclerView.setVisibility(View.GONE);
        tvEmptyHistory.setVisibility(View.VISIBLE);
        tvEmptyHistory.setText("Error: " + message);
    }

    @Override
    public void showEmptyHistory() {
        recyclerView.setVisibility(View.GONE);
        tvEmptyHistory.setVisibility(View.VISIBLE);
        tvEmptyHistory.setText("No hay registros de presión arterial.\n\n¡Comienza registrando tu primera lectura!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar historial al volver a la actividad
        loadHistory();
    }
}
