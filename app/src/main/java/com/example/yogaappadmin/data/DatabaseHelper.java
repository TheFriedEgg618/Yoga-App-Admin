package com.example.yogaappadmin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yogaappadmin.model.ClassModel;

import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper manages the SQLite database for storing class schedules.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "yoga_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_CLASSES = "classes";

    // Class Table Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DAY = "day_of_week";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CAPACITY = "capacity";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_TYPE = "class_type";
    public static final String COLUMN_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DAY + " TEXT NOT NULL, "
                + COLUMN_TIME + " TEXT NOT NULL, "
                + COLUMN_CAPACITY + " INTEGER NOT NULL, "
                + COLUMN_DURATION + " INTEGER NOT NULL, "
                + COLUMN_PRICE + " REAL NOT NULL, "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT"
                + ");";
        db.execSQL(CREATE_CLASSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity, drop table and recreate. In production, handle migrations properly.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    /**
     * Inserts a new class record into the database.
     * @return true if insertion succeeded, false otherwise.
     */
    public boolean insertClass(String day, String time,
                               int capacity, int duration,
                               double price, String type,
                               String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_CLASSES, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Fetches all class records.
     */
    public Cursor getAllClasses() throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CLASSES,
                null, // all columns
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC");
    }

    /**
     * Deletes a class record by ID.
     * @return number of rows affected
     */
    public int deleteClass(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_CLASSES,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Updates an existing class record.
     * @return number of rows affected
     */
    public int updateClass(long id, String day, String time,
                           int capacity, int duration,
                           double price, String type,
                           String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DAY, day);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);

        int rows = db.update(TABLE_CLASSES,
                values,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /** Fetch all classes as a List<ClassModel>. */
    public List<ClassModel> getAllClassesList() {
        List<ClassModel> classes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_CLASSES, null,   // all columns, no where
                null, null, null, null,
                COLUMN_ID + " DESC"
        );
        if (c != null) {
            while (c.moveToNext()) {
                long   id   = c.getLong(c.getColumnIndexOrThrow(COLUMN_ID));
                String day  = c.getString(c.getColumnIndexOrThrow(COLUMN_DAY));
                String time = c.getString(c.getColumnIndexOrThrow(COLUMN_TIME));
                int    cap  = c.getInt(c.getColumnIndexOrThrow(COLUMN_CAPACITY));
                int    dur  = c.getInt(c.getColumnIndexOrThrow(COLUMN_DURATION));
                double pr   = c.getDouble(c.getColumnIndexOrThrow(COLUMN_PRICE));
                String typ  = c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE));
                String desc = c.getString(c.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                classes.add(new ClassModel(id, day, time, cap, dur, pr, typ, desc));
            }
            c.close();
        }
        return classes;
    }
}
