package com.protesq.dilogrenmeeng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "EnglishLearning.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_WORDS = "words";
    public static final String TABLE_GRAMMAR = "grammar";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_LANGUAGE = "language";

    // Create table statements
    private static final String CREATE_TABLE_WORDS = "CREATE TABLE " + TABLE_WORDS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_LANGUAGE + " TEXT"
            + ")";

    private static final String CREATE_TABLE_GRAMMAR = "CREATE TABLE " + TABLE_GRAMMAR + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_CONTENT + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORDS);
        db.execSQL(CREATE_TABLE_GRAMMAR);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GRAMMAR);
        onCreate(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // Insert some initial words
        String[][] words = {
            {"Door", "Kapı"},
            {"Window", "Pencere"},
            {"Book", "Kitap"},
            {"Table", "Masa"},
            {"Chair", "Sandalye"},
            {"House", "Ev"},
            {"Car", "Araba"},
            {"Tree", "Ağaç"},
            {"Sun", "Güneş"},
            {"Moon", "Ay"},
            {"Water", "Su"},
            {"Food", "Yemek"},
            {"Time", "Zaman"},
            {"Day", "Gün"},
            {"Night", "Gece"},
            {"School", "Okul"},
            {"Friend", "Arkadaş"},
            {"Family", "Aile"},
            {"City", "Şehir"},
            {"Country", "Ülke"}
        };

        for (String[] word : words) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, word[0]);
            values.put(COLUMN_LANGUAGE, "English");
            db.insert(TABLE_WORDS, null, values);

            values = new ContentValues();
            values.put(COLUMN_TITLE, word[1]);
            values.put(COLUMN_LANGUAGE, "Turkish");
            db.insert(TABLE_WORDS, null, values);
        }

        // Insert some initial grammar content
        String[][] grammar = {
            {"Present Simple", "We use present simple to talk about habits and routines."},
            {"Past Simple", "We use past simple to talk about completed actions in the past."},
            {"Future Simple", "We use future simple to talk about predictions and promises."},
            {"Present Continuous", "We use present continuous to talk about actions happening now."}
        };

        for (String[] g : grammar) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TITLE, g[0]);
            values.put(COLUMN_CONTENT, g[1]);
            db.insert(TABLE_GRAMMAR, null, values);
        }
    }

    public Cursor getAllWords() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WORDS, null, null, null, null, null, null);
    }

    public Cursor getAllGrammar() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GRAMMAR, null, null, null, null, null, null);
    }

    public Cursor getWordsByLanguage(String language) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_WORDS, null, COLUMN_LANGUAGE + "=?", 
            new String[]{language}, null, null, null);
    }
} 