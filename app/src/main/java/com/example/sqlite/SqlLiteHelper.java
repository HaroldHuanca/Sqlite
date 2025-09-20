package com.example.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqlLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NOMBRE ="datos.db";

    public SqlLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    private static final String TABLE_USUARIOS="tUsuarios";
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+ TABLE_USUARIOS +
                " ( dni text primary key, " +
                "nombre text," +
                "telefono text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE " + TABLE_USUARIOS);
        onCreate(db);
    }
}
