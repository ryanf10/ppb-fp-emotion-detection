package com.example.emotiondetection.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Database {
    private SQLiteDatabase db;
    private SQLiteOpenHelper openHelper;

    private static Database database;
    private static boolean isClose = true;

    private Database(Context context) {
        this.openHelper = new SQLiteOpenHelper(context, "db.sqlite", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };

        this.db = this.openHelper.getWritableDatabase();
        this.db.execSQL("CREATE TABLE IF NOT EXISTS emotion(id INTEGER PRIMARY KEY AUTOINCREMENT, nama TEXT UNIQUE, total INTEGER)");
        this.db.execSQL("CREATE TABLE IF NOT EXISTS detect(id INTEGER PRIMARY KEY AUTOINCREMENT, id_emotion INTEGER, probability REAL, timestamp TEXT, FOREIGN KEY(id_emotion) REFERENCES emotion(id))");
    }

    public static Database getInstance(Context context) {
        if (database == null || isClose) {
            database = new Database(context);
            isClose = false;
        }
        return database;
    }

    public static Database getInstance() throws Exception {
        if (database == null || isClose) {
            throw new Exception("Cannot");
        }
        return database;
    }


    public void close() {
        if (!isClose && database != null) {
            this.db.close();
            this.openHelper.close();
            isClose = true;
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}

