package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SqlLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USUARIOS="tUsuarios";
    private static String DATABASE_NOMBRE ="datos.db";

    public SqlLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }
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
    public boolean insertarUsuario(String dni, String nombre, String telefono){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dni",dni);
        values.put("nombre",nombre);
        values.put("telefono",telefono);

        long result = db.insert(TABLE_USUARIOS, null, values);
        return result !=-1;
    }
    public Cursor obtenerUsuarios(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_USUARIOS, null);
    }
    public boolean actualizarUsuario(String dni, String nombre, String telefono){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("telefono", telefono);

        int filas = db.update(TABLE_USUARIOS, values, "dni=?", new String[]{dni});
        return filas > 0;
    }
    public boolean eliminarUsuario(String dni){
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete(TABLE_USUARIOS,"dni=?", new String[]{dni});
        return filas > 0;
    }
}
