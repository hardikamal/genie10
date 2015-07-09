package com.getgenieapp.android.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.getgenieapp.android.Extras.DataFields;
import com.getgenieapp.android.Objects.MessageValues;

/**
 * Created by Raviteja on 7/8/2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    public static final String id = "id";
    public static final String message_id = "message_id";
    public static final String agent_id = "agent_id";
    public static final String sender_id = "sender_id";
    public static final String message_type = "message_type";
    public static final String category_id = "category_id";
    public static final String message_values = "message_values";
    public static final String status = "status";
    public static final String created_at = "created_at";
    public static final String updated_at = "updated_at";
    public static final String direction = "direction";
    
    public static final String TABLE = "getgenietable";

    private static final String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE +
            "(" + id + " INTEGER PRIMARY KEY autoincrement," + message_id + " TEXT," + agent_id + " TEXT,"
            + sender_id + " TEXT," + message_type + " TEXT,"
            + category_id + " TEXT," + message_values + " TEXT," + status + " TEXT,"
            + created_at + " TEXT," + updated_at + " TEXT," + direction + " TEXT)";

    public DBHandler(Context context) {
        super(context, DataFields.DBName, null, DataFields.DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // toast
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
