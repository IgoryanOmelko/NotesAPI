package ru.example.notesapi.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory cursor, int version) {
        super(context, name, cursor, version);
    }

    /**
     * Method executes creation of db
     * It not rewrite db, if db already exist
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        String queryTableSetting = "CREATE TABLE settings (id INTEGER PRIMARY KEY, name VARCHAR(30), password VARCHAR(30), token VARCHAR(10), cstr VARCHAR);";
        query = queryTableSetting;
        db.execSQL(query);
        query = "PRAGMA foreign_keys=ON";
        db.execSQL(query);
    }
    /*
    Working with settings
     */

    /**
     * Method is inserting id and connection string in setting table
     * Default id equals 0
     *
     * @param cstr Connection string, what need to record
     */
    public void SetSettings(String cstr) {
        SQLiteDatabase db = getWritableDatabase();
        int id = 0;
        String querySetID = "INSERT INTO settings VALUES('" + id + "','" + null + "','" + null + "','" + null + "','" + cstr + "');";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method is recording id in setting table
     * Default id equals 0
     */
    public void SetSettingsID() {
        SQLiteDatabase db = getWritableDatabase();
        int id = 0;
        String querySetID = "UPDATE settings SET id ='" + id + "';";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Methode read ID of settings from database
     *
     * @return value of ID
     */
    public int GetSettingsID() {
        SQLiteDatabase db = getReadableDatabase();
        int ID = 0;
        String query = "SELECT (id) FROM settings";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst() == true) {
                ID = cursor.getInt(0);
                return ID;
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * Method executes write name in setting table
     *
     * @param name name, what need to record
     */
    public void SetSettingsName(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String querySetID = "UPDATE settings SET name ='" + name + "' WHERE id=0;";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Methode read name from settings table from database
     *
     * @return value of name
     */
    public String GetSettingsName() {
        SQLiteDatabase db = getReadableDatabase();
        String name = "";
        String query = "SELECT (name) FROM settings WHERE id = 0";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst() == true) {
                name = cursor.getString(0);
                return name;
            } else {
                return "";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Method is recording password in setting table
     *
     * @param password password, what need to record
     */
    public void SetSettingsPassword(String password) {
        SQLiteDatabase db = getWritableDatabase();
        String querySetID = "UPDATE settings SET password ='" + password + "' WHERE id=0;";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Methode read password from settings table from database
     *
     * @return value of password
     */
    public String GetSettingsPassword() {
        SQLiteDatabase db = getReadableDatabase();
        String password = "";
        String query = "SELECT (password) FROM settings WHERE id = 0";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst() == true) {
                password = cursor.getString(0);
                return password;
            } else {
                return "";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Method is recording token in setting table
     *
     * @param token token, what need to record
     */
    public void SetSettingsToken(String token) {
        SQLiteDatabase db = getWritableDatabase();
        String querySetID = "UPDATE settings SET token ='" + token + "' WHERE id=0;";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Methode read token from settings table from database
     *
     * @return value of token
     */
    public String GetSettingsToken() {
        SQLiteDatabase db = getReadableDatabase();
        String token = "";
        String query = "SELECT (token) FROM settings WHERE id = 0";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst() == true) {
                token = cursor.getString(0);
                return token;
            } else {
                return "";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Method is recording connection string in setting table
     *
     * @param cstr connection string, what need to record
     */
    public void SetSettingsCSTR(String cstr) {
        SQLiteDatabase db = getWritableDatabase();
        String querySetID = "UPDATE settings SET cstr ='" + cstr + "' WHERE id=0;";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Methode read connection string from settings table from database
     *
     * @return value of connection string
     */
    public String GetSettingsCSTR() {
        SQLiteDatabase db = getReadableDatabase();
        String cstr = "";
        String query = "SELECT (cstr) FROM settings WHERE id = 0";
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst() == true) {
                cstr = cursor.getString(0);
                return cstr;
            } else {
                return "";
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Method is deleting data from setting table
     */
    public void DeleteSettings() {
        SQLiteDatabase db = getWritableDatabase();
        String querySetID = "DELETE FROM settings;";
        try {
            db.execSQL(querySetID);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //TODO Work with graphs, nodes and links
}
