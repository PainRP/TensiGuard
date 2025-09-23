package com.example.tensiguard.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tensiguard.model.PressureReading;
import com.example.tensiguard.model.UserProfile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tensiguard.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla de lecturas de presión
    private static final String TABLE_READINGS = "pressure_readings";
    private static final String COL_ID = "id";
    private static final String COL_SYSTOLIC = "systolic";
    private static final String COL_DIASTOLIC = "diastolic";
    private static final String COL_CIRCUMSTANCES = "circumstances";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_CLASSIFICATION = "classification";
    private static final String COL_RECOMMENDATIONS = "recommendations";

    // Tabla de perfil de usuario
    private static final String TABLE_USER = "user_profile";
    private static final String COL_USER_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_WEIGHT = "weight";
    private static final String COL_HEIGHT = "height";
    private static final String COL_GENDER = "gender";
    private static final String COL_EMERGENCY_CONTACT = "emergency_contact";

    private static DatabaseHelper instance;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de lecturas
        String createReadingsTable = "CREATE TABLE " + TABLE_READINGS + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_SYSTOLIC + " INTEGER NOT NULL," +
                COL_DIASTOLIC + " INTEGER NOT NULL," +
                COL_CIRCUMSTANCES + " TEXT," +
                COL_TIMESTAMP + " TEXT NOT NULL," +
                COL_CLASSIFICATION + " TEXT," +
                COL_RECOMMENDATIONS + " TEXT" +
                ")";

        // Crear tabla de usuario
        String createUserTable = "CREATE TABLE " + TABLE_USER + "(" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT NOT NULL," +
                COL_WEIGHT + " REAL NOT NULL," +
                COL_HEIGHT + " REAL NOT NULL," +
                COL_GENDER + " TEXT NOT NULL," +
                COL_EMERGENCY_CONTACT + " TEXT" +
                ")";

        db.execSQL(createReadingsTable);
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Métodos para PressureReading
    public long insertReading(PressureReading reading) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_SYSTOLIC, reading.getSystolic());
        values.put(COL_DIASTOLIC, reading.getDiastolic());
        values.put(COL_CIRCUMSTANCES, reading.getCircumstances());
        values.put(COL_TIMESTAMP, dateFormat.format(reading.getTimestamp()));
        values.put(COL_CLASSIFICATION, reading.getClassification());
        values.put(COL_RECOMMENDATIONS, reading.getRecommendations());

        return db.insert(TABLE_READINGS, null, values);
    }

    public List<PressureReading> getAllReadings() {
        List<PressureReading> readings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_READINGS, null, null, null, null, null, COL_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                PressureReading reading = cursorToReading(cursor);
                readings.add(reading);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return readings;
    }

    public List<PressureReading> getReadingsByDate(String date) {
        List<PressureReading> readings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COL_TIMESTAMP + " LIKE ?";
        String[] selectionArgs = { date + "%" };

        Cursor cursor = db.query(TABLE_READINGS, null, selection, selectionArgs, null, null, COL_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                PressureReading reading = cursorToReading(cursor);
                readings.add(reading);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return readings;
    }

    private PressureReading cursorToReading(Cursor cursor) {
        PressureReading reading = new PressureReading();
        reading.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
        reading.setSystolic(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SYSTOLIC)));
        reading.setDiastolic(cursor.getInt(cursor.getColumnIndexOrThrow(COL_DIASTOLIC)));
        reading.setCircumstances(cursor.getString(cursor.getColumnIndexOrThrow(COL_CIRCUMSTANCES)));
        reading.setClassification(cursor.getString(cursor.getColumnIndexOrThrow(COL_CLASSIFICATION)));
        reading.setRecommendations(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECOMMENDATIONS)));

        try {
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
            reading.setTimestamp(dateFormat.parse(timestamp));
        } catch (Exception e) {
            reading.setTimestamp(new Date());
        }

        return reading;
    }

    // Métodos para UserProfile
    public long insertOrUpdateUser(UserProfile user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, user.getName());
        values.put(COL_WEIGHT, user.getWeight());
        values.put(COL_HEIGHT, user.getHeight());
        values.put(COL_GENDER, user.getGender());
        values.put(COL_EMERGENCY_CONTACT, user.getEmergencyContact());

        // Verificar si ya existe un usuario
        if (getUserProfile() != null) {
            return db.update(TABLE_USER, values, COL_USER_ID + " = 1", null);
        } else {
            return db.insert(TABLE_USER, null, values);
        }
    }

    public UserProfile getUserProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, null, null, null, null, null, "1");

        UserProfile user = null;
        if (cursor.moveToFirst()) {
            user = new UserProfile();
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            user.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT)));
            user.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(COL_HEIGHT)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER)));
            user.setEmergencyContact(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMERGENCY_CONTACT)));
        }
        cursor.close();
        return user;
    }

    public boolean hasUserProfile() {
        return getUserProfile() != null;
    }
}
