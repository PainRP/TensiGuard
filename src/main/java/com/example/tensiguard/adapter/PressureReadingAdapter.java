package com.example.tensiguard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tensiguard.R;
import com.example.tensiguard.model.PressureReading;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Adapter optimizado para mostrar lecturas de presión arterial
 * Usa DiffUtil para notificaciones eficientes de cambios
 */
public class PressureReadingAdapter extends ListAdapter<PressureReading, PressureReadingAdapter.ViewHolder> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final Context context;

    public PressureReadingAdapter(@NonNull Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<PressureReading> DIFF_CALLBACK = new DiffUtil.ItemCallback<PressureReading>() {
        @Override
        public boolean areItemsTheSame(@NonNull PressureReading oldItem, @NonNull PressureReading newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull PressureReading oldItem, @NonNull PressureReading newItem) {
            return Objects.equals(oldItem.getPressureString(), newItem.getPressureString()) &&
                   Objects.equals(oldItem.getClassification(), newItem.getClassification()) &&
                   Objects.equals(oldItem.getCircumstances(), newItem.getCircumstances()) &&
                   Objects.equals(oldItem.getRecommendations(), newItem.getRecommendations()) &&
                   Objects.equals(oldItem.getTimestamp(), newItem.getTimestamp());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pressure_reading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PressureReading reading = getItem(position);
        holder.bind(reading);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvPressure;
        private final TextView tvClassification;
        private final TextView tvCircumstances;
        private final TextView tvRecommendations;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date); // Changed to snake_case
            tvPressure = itemView.findViewById(R.id.tv_pressure); // Changed to snake_case
            tvClassification = itemView.findViewById(R.id.tv_classification); // Changed to snake_case
            tvCircumstances = itemView.findViewById(R.id.tv_circumstances); // Changed to snake_case
            tvRecommendations = itemView.findViewById(R.id.tv_recommendations); // Changed to snake_case
        }

        public void bind(@NonNull PressureReading reading) {
            // Fecha y hora
            tvDate.setText(dateFormat.format(reading.getTimestamp()));

            // Presión con formato de recursos
            tvPressure.setText(context.getString(R.string.pressure_format, reading.getPressureString()));

            // Clasificación
            String classification = reading.getClassification();
            tvClassification.setText(classification != null ?
                classification : context.getString(R.string.unclassified));

            // Circunstancias (opcional)
            setOptionalText(tvCircumstances, reading.getCircumstances());

            // Recomendaciones (opcional)
            setOptionalText(tvRecommendations, reading.getRecommendations());

            // Color según clasificación
            int classificationColor = getClassificationColor(classification);
            tvClassification.setTextColor(classificationColor);
        }

        private void setOptionalText(@NonNull TextView textView, String text) {
            if (text != null && !text.trim().isEmpty()) {
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }

        private int getClassificationColor(String classification) {
            if (classification == null) {
                return ContextCompat.getColor(context, R.color.text_primary);
            }

            String lowerClassification = classification.toLowerCase();
            if (lowerClassification.contains("normal")) {
                return ContextCompat.getColor(context, R.color.pressure_normal);
            } else if (lowerClassification.contains("elevada")) {
                return ContextCompat.getColor(context, R.color.pressure_elevated);
            } else if (lowerClassification.contains("hipertensión") || lowerClassification.contains("alta")) {
                return ContextCompat.getColor(context, R.color.pressure_high);
            } else if (lowerClassification.contains("crisis")) {
                return ContextCompat.getColor(context, R.color.pressure_crisis);
            } else if (lowerClassification.contains("baja")) {
                return ContextCompat.getColor(context, R.color.pressure_low);
            }
            return ContextCompat.getColor(context, R.color.text_primary);
        }
    }
}
