package com.example.tensiguard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tensiguard.R;
import com.example.tensiguard.model.PressureReading;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para el RecyclerView del historial de presión arterial
 */
public class PressureHistoryAdapter extends RecyclerView.Adapter<PressureHistoryAdapter.ViewHolder> {
    private List<PressureReading> readings = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(PressureReading reading);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void updateReadings(List<PressureReading> newReadings) {
        this.readings.clear();
        this.readings.addAll(newReadings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pressure_reading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PressureReading reading = readings.get(position);
        holder.bind(reading);
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvPressure, tvClassification, tvDate, tvTime, tvCircumstances;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvPressure = itemView.findViewById(R.id.tv_pressure);
            tvClassification = itemView.findViewById(R.id.tv_classification);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvCircumstances = itemView.findViewById(R.id.tv_circumstances);

            // Listener para click en la tarjeta
            cardView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(readings.get(position));
                    }
                }
            });
        }

        public void bind(PressureReading reading) {
            // Mostrar valores de presión
            String pressureText = reading.getSystolic() + "/" + reading.getDiastolic();
            tvPressure.setText(pressureText);

            // Mostrar clasificación
            tvClassification.setText(reading.getClassification());

            // Aplicar color según la clasificación
            setClassificationColor(reading.getClassification());

            // Mostrar fecha y hora
            tvDate.setText(dateFormat.format(reading.getTimestamp()));
            tvTime.setText(timeFormat.format(reading.getTimestamp()));

            // Mostrar circunstancias si existen
            if (reading.getCircumstances() != null && !reading.getCircumstances().trim().isEmpty()) {
                tvCircumstances.setVisibility(View.VISIBLE);
                tvCircumstances.setText(reading.getCircumstances());
            } else {
                tvCircumstances.setVisibility(View.GONE);
            }
        }

        private void setClassificationColor(String classification) {
            int color;

            if (classification.contains("Normal")) {
                color = itemView.getContext().getResources().getColor(android.R.color.holo_green_dark);
            } else if (classification.contains("Elevada")) {
                color = itemView.getContext().getResources().getColor(android.R.color.holo_orange_light);
            } else if (classification.contains("Hipertensión")) {
                color = itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark);
            } else if (classification.contains("Crisis")) {
                color = itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
            } else if (classification.contains("Baja")) {
                color = itemView.getContext().getResources().getColor(android.R.color.holo_blue_light);
            } else {
                color = itemView.getContext().getResources().getColor(android.R.color.black);
            }

            tvClassification.setTextColor(color);
        }
    }
}
