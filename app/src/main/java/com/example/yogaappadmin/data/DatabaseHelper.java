package com.example.yogaappadmin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yogaappadmin.model.ClassModel;
import com.example.yogaappadmin.model.TeacherModel;
import com.example.yogaappadmin.model.YogaTypeModel;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME    = "yoga_app.db";
    private static final int    DATABASE_VERSION = 4;

    // --- CLASSES table ---
    public static final String TABLE_CLASSES        = "classes";
    public static final String COLUMN_ID            = "id";
    public static final String COLUMN_TITLE         = "title";
    public static final String COLUMN_TEACHER_NAME  = "teacher_name";
    public static final String COLUMN_DAY           = "day_of_week";
    public static final String COLUMN_TIME          = "time";
    public static final String COLUMN_CAPACITY      = "capacity";
    public static final String COLUMN_DURATION      = "duration";
    public static final String COLUMN_PRICE         = "price";
    public static final String COLUMN_TYPE          = "class_type";
    public static final String COLUMN_DESCRIPTION   = "description";

    // --- TEACHERS table ---
    public static final String TABLE_TEACHERS         = "teachers";
    public static final String COLUMN_TEACHER_ID      = "id";
    public static final String COLUMN_TEACHER_NAME_T  = "name";
    public static final String COLUMN_TEACHER_BIO     = "bio";
    public static final String COLUMN_TEACHER_CLASSES = "classes_csv";

    // --- CLASS_TYPES table ---
    public static final String TABLE_CLASS_TYPES  = "class_types";
    public static final String COLUMN_TYPE_ID     = "id";
    public static final String COLUMN_TYPE_NAME   = "type_name";
    public static final String COLUMN_TYPE_DESC   = "description";

    // --- CREATE statements ---
    private static final String CREATE_CLASSES_TABLE =
            "CREATE TABLE " + TABLE_CLASSES + " ("
                    + COLUMN_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TITLE        + " TEXT NOT NULL, "
                    + COLUMN_TEACHER_NAME + " TEXT NOT NULL, "
                    + COLUMN_DAY          + " TEXT NOT NULL, "
                    + COLUMN_TIME         + " TEXT NOT NULL, "
                    + COLUMN_CAPACITY     + " INTEGER NOT NULL, "
                    + COLUMN_DURATION     + " INTEGER NOT NULL, "
                    + COLUMN_PRICE        + " REAL NOT NULL, "
                    + COLUMN_TYPE         + " TEXT NOT NULL, "
                    + COLUMN_DESCRIPTION  + " TEXT"
                    + ");";

    private static final String CREATE_TEACHERS_TABLE =
            "CREATE TABLE " + TABLE_TEACHERS + " ("
                    + COLUMN_TEACHER_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TEACHER_NAME_T + " TEXT NOT NULL, "
                    + COLUMN_TEACHER_BIO    + " TEXT, "
                    + COLUMN_TEACHER_CLASSES+ " TEXT"
                    + ");";

    private static final String CREATE_CLASS_TYPES_TABLE =
            "CREATE TABLE " + TABLE_CLASS_TYPES + " ("
                    + COLUMN_TYPE_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TYPE_NAME + " TEXT NOT NULL, "
                    + COLUMN_TYPE_DESC + " TEXT"
                    + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASSES_TABLE);
        db.execSQL(CREATE_TEACHERS_TABLE);
        db.execSQL(CREATE_CLASS_TYPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // v1→2: add teachers & class_types tables
        if (oldVersion < 2) {
            db.execSQL(CREATE_TEACHERS_TABLE);
            db.execSQL(CREATE_CLASS_TYPES_TABLE);
        }
        // v2→3: shit goes down hill
        if (oldVersion < 3) {
            ;
        }
        // v3→4: add teacher_name column to classes
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_CLASSES
                    + " ADD COLUMN " + COLUMN_TEACHER_NAME
                    + " TEXT NOT NULL DEFAULT ''");
        }
    }

    // --- CLASSES CRUD ---
    public boolean insertClass(String title,
                               String teacherName,
                               String day,
                               String time,
                               int capacity,
                               int duration,
                               double price,
                               String type,
                               String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TITLE,        title);
        v.put(COLUMN_TEACHER_NAME, teacherName);
        v.put(COLUMN_DAY,          day);
        v.put(COLUMN_TIME,         time);
        v.put(COLUMN_CAPACITY,     capacity);
        v.put(COLUMN_DURATION,     duration);
        v.put(COLUMN_PRICE,        price);
        v.put(COLUMN_TYPE,         type);
        v.put(COLUMN_DESCRIPTION,  description);
        long id = db.insert(TABLE_CLASSES, null, v);
        db.close();
        return id != -1;
    }

    public int updateClass(long id,
                           String title,
                           String teacherName,
                           String day,
                           String time,
                           int capacity,
                           int duration,
                           double price,
                           String type,
                           String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TITLE,        title);
        v.put(COLUMN_TEACHER_NAME, teacherName);
        v.put(COLUMN_DAY,          day);
        v.put(COLUMN_TIME,         time);
        v.put(COLUMN_CAPACITY,     capacity);
        v.put(COLUMN_DURATION,     duration);
        v.put(COLUMN_PRICE,        price);
        v.put(COLUMN_TYPE,         type);
        v.put(COLUMN_DESCRIPTION,  description);
        int rows = db.update(
                TABLE_CLASSES, v,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    public int deleteClass(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(
                TABLE_CLASSES,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    public List<ClassModel> getAllClassesList() {
        List<ClassModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_CLASSES, null,
                null, null, null, null,
                COLUMN_ID + " DESC"
        );
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new ClassModel(
                        c.getLong(   c.getColumnIndexOrThrow(COLUMN_ID)),
                        c.getString( c.getColumnIndexOrThrow(COLUMN_TITLE)),
                        c.getString( c.getColumnIndexOrThrow(COLUMN_TEACHER_NAME)),
                        c.getString( c.getColumnIndexOrThrow(COLUMN_DAY)),
                        c.getString( c.getColumnIndexOrThrow(COLUMN_TIME)),
                        c.getInt(    c.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                        c.getInt(    c.getColumnIndexOrThrow(COLUMN_DURATION)),
                        c.getDouble( c.getColumnIndexOrThrow(COLUMN_PRICE)),
                        c.getString( c.getColumnIndexOrThrow(COLUMN_TYPE)),
                        c.getString( c.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                ));
            }
            c.close();
        }
        return list;
    }

    // --- TEACHERS CRUD ---
    public boolean insertTeacher(String name, String bio, String classesCsv) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TEACHER_NAME, name);
        v.put(COLUMN_TEACHER_BIO, bio);
        v.put(COLUMN_TEACHER_CLASSES, classesCsv);
        long id = db.insert(TABLE_TEACHERS, null, v);
        db.close();
        return id != -1;
    }

    public int updateTeacher(long id, String name, String bio, String classesCsv) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TEACHER_NAME, name);
        v.put(COLUMN_TEACHER_BIO, bio);
        v.put(COLUMN_TEACHER_CLASSES, classesCsv);
        int rows = db.update(
                TABLE_TEACHERS, v,
                COLUMN_TEACHER_ID + "=?", new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    public int deleteTeacher(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(
                TABLE_TEACHERS,
                COLUMN_TEACHER_ID + "=?", new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    public List<TeacherModel> getAllTeachersModels() {
        List<TeacherModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_TEACHERS, null,
                null, null, null, null,
                COLUMN_TEACHER_NAME_T + " COLLATE NOCASE"
        );
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new TeacherModel(
                        c.getLong  (c.getColumnIndexOrThrow(COLUMN_TEACHER_ID)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_TEACHER_NAME_T)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_TEACHER_BIO)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_TEACHER_CLASSES))
                ));
            }
            c.close();
        }
        return list;
    }

    // --- CLASS_TYPES CRUD ---
    public boolean insertYogaType(String name, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TYPE_NAME, name);
        v.put(COLUMN_TYPE_DESC, desc);
        long id = db.insert(TABLE_CLASS_TYPES, null, v);
        db.close();
        return id != -1;
    }

    public int updateYogaType(long id, String name, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_TYPE_NAME, name);
        v.put(COLUMN_TYPE_DESC, desc);
        int rows = db.update(
                TABLE_CLASS_TYPES, v,
                COLUMN_TYPE_ID + "=?", new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    public int deleteYogaType(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(
                TABLE_CLASS_TYPES,
                COLUMN_TYPE_ID + "=?", new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    public List<YogaTypeModel> getAllYogaTypesModels() {
        List<YogaTypeModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_CLASS_TYPES, null,
                null, null, null, null,
                COLUMN_TYPE_NAME + " COLLATE NOCASE"
        );
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new YogaTypeModel(
                        c.getLong  (c.getColumnIndexOrThrow(COLUMN_TYPE_ID)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COLUMN_TYPE_DESC))
                ));
            }
            c.close();
        }
        return list;
    }
}
