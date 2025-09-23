package com.example.tensiguard.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tensiguard.model.PressureReading;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper para la base de datos SQLite - Patrón Singleton
 */
public class PressureDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tensiguard.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla de lecturas de presión
    private static final String TABLE_PRESSURE = "pressure_readings";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SYSTOLIC = "systolic";
    private static final String COLUMN_DIASTOLIC = "diastolic";
    private static final String COLUMN_CIRCUMSTANCES = "circumstances";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_AI_REPORT = "ai_report";
    private static final String COLUMN_CLASSIFICATION = "classification";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_GENDER = "gender";

    private static PressureDBHelper instance;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // Singleton pattern
    public static synchronized PressureDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PressureDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private PressureDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PRESSURE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SYSTOLIC + " INTEGER NOT NULL, " +
                COLUMN_DIASTOLIC + " INTEGER NOT NULL, " +
                COLUMN_CIRCUMSTANCES + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT NOT NULL, " +
                COLUMN_AI_REPORT + " TEXT, " +
                COLUMN_CLASSIFICATION + " TEXT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_WEIGHT + " INTEGER, " +
                COLUMN_HEIGHT + " INTEGER, " +
                COLUMN_GENDER + " TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESSURE);
        onCreate(db);
    }

    /**
     * Insertar nueva lectura de presión
     */
    public long insertPressureReading(PressureReading reading) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SYSTOLIC, reading.getSystolic());
        values.put(COLUMN_DIASTOLIC, reading.getDiastolic());
        values.put(COLUMN_CIRCUMSTANCES, reading.getCircumstances());
        values.put(COLUMN_TIMESTAMP, dateFormat.format(reading.getTimestamp()));
        values.put(COLUMN_AI_REPORT, reading.getAiReport());
        values.put(COLUMN_CLASSIFICATION, reading.getClassification());
        values.put(COLUMN_USER_NAME, reading.getUserName());
        values.put(COLUMN_WEIGHT, reading.getWeight());
        values.put(COLUMN_HEIGHT, reading.getHeight());
        values.put(COLUMN_GENDER, reading.getGender());

        long id = db.insert(TABLE_PRESSURE, null, values);
        db.close();
        return id;
    }

    /**
     * Obtener todas las lecturas ordenadas por fecha (más reciente primero)
     */
    public List<PressureReading> getAllReadings() {
        List<PressureReading> readings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PRESSURE +
                      " ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 50";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PressureReading reading = new PressureReading();
                reading.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                reading.setSystolic(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYSTOLIC)));
                reading.setDiastolic(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIASTOLIC)));
                reading.setCircumstances(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIRCUMSTANCES)));

                try {
                    String timestampStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                    reading.setTimestamp(dateFormat.parse(timestampStr));
                } catch (ParseException e) {
                    reading.setTimestamp(new Date());
                }

                reading.setAiReport(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AI_REPORT)));
                reading.setClassification(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSIFICATION)));
                reading.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
                reading.setWeight(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
                reading.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)));
                reading.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));

                readings.add(reading);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return readings;
    }

    /**
     * Obtener lectura por ID
     */
    public PressureReading getReadingById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PRESSURE + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        PressureReading reading = null;
        if (cursor.moveToFirst()) {
            reading = new PressureReading();
            reading.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            reading.setSystolic(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SYSTOLIC)));
            reading.setDiastolic(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIASTOLIC)));
            reading.setCircumstances(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CIRCUMSTANCES)));

            try {
                String timestampStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                reading.setTimestamp(dateFormat.parse(timestampStr));
            } catch (ParseException e) {
                reading.setTimestamp(new Date());
            }

            reading.setAiReport(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AI_REPORT)));
            reading.setClassification(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASSIFICATION)));
            reading.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)));
            reading.setWeight(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)));
            reading.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)));
            reading.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
        }

        cursor.close();
        db.close();
        return reading;
    }

    /**
     * Eliminar todas las lecturas (para pruebas)
     */
    public void clearAllReadings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRESSURE, null, null);
        db.close();
    }
}
