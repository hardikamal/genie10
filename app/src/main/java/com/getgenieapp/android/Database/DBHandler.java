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
    public static final String category_id = "category_id";
    public static final String message_values = "message_values";
    public static final String status = "status";
    public static final String created_at = "created_at";
    public static final String updated_at = "updated_at";
    public static final String direction = "direction";

    public static final String fav_id = "fav_id";
    public static final String name = "name";
    public static final String lng = "lng";
    public static final String lat = "lat";
    public static final String address = "address";

    public static final String cat_count_id = "cat_count_id";
    public static final String cat_id = "cat_id";
    public static final String cat_name = "cat_name";
    public static final String img_url = "img_url";
    public static final String description = "description";
    public static final String bg_color = "bg_color";
    public static final String hide_chats_time = "hide_chats_time";
    public static final String notification = "notification";

    public static final String FAVTABLE = "favstable";
    public static final String TABLE = "getgenietable";
    public static final String CATTABLE = "cattable";

    private static final String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE +
            "(" + id + " INTEGER PRIMARY KEY autoincrement," + message_id + " TEXT,"
            + category_id + " TEXT," + message_values + " TEXT," + status + " TEXT,"
            + created_at + " TEXT," + updated_at + " TEXT," + direction + " TEXT)";

    private static final String DATABASE_CREATE_TABLE_FAVS = "CREATE TABLE IF NOT EXISTS " + FAVTABLE +
            "(" + fav_id + " INTEGER PRIMARY KEY autoincrement," + name + " TEXT," + lng + " TEXT,"
            + lat + " TEXT," + address + " TEXT)";

    private static final String DATABASE_CREATE_TABLE_CAT = "CREATE TABLE IF NOT EXISTS " + CATTABLE +
            "(" + cat_count_id + " INTEGER PRIMARY KEY autoincrement," + cat_id + " INT, " + notification + " INT, " + cat_name + " TEXT," + img_url + " TEXT,"
            + description + " TEXT," + bg_color + " TEXT," + hide_chats_time + " TEXT)";

    public DBHandler(Context context) {
        super(context, DataFields.DBName, null, DataFields.DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_TABLE);
        db.execSQL(DATABASE_CREATE_TABLE_FAVS);
        db.execSQL(DATABASE_CREATE_TABLE_CAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // toast
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FAVTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CATTABLE);
        onCreate(db);
    }
}
