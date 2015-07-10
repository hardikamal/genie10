package com.getgenieapp.android.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.getgenieapp.android.Objects.Messages;
import com.getgenieapp.android.Objects.MessageValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raviteja on 7/8/2015.
 */
public class DBDataSource {
    private Context context;
    private SQLiteDatabase database;

    public DBDataSource(Context context) {
        this.context = context;
    }

    public void open() throws SQLException {
        database = new DBHandler(context).getWritableDatabase();
    }

    public boolean isOpen() {
        return database.isOpen();
    }

    public void clean() throws SQLException {
        if (!isOpen())
            open();
        database.execSQL("delete from " + DBHandler.TABLE);
    }

    public void close() throws SQLException {
        database.close();
    }

    public void addNormal(Messages message) {
        open();
        ContentValues values = new ContentValues();

        values.put(DBHandler.message_id, String.valueOf(message.get_id()));
        values.put(DBHandler.agent_id, String.valueOf(message.getAgentId()));
        values.put(DBHandler.sender_id, String.valueOf(message.getSenderId()));
        values.put(DBHandler.message_type, String.valueOf(message.getMessageType()));
        values.put(DBHandler.category_id, String.valueOf(message.getCategory()));
        values.put(DBHandler.message_values, String.valueOf(message.getMessageValues().toString()));
        values.put(DBHandler.status, String.valueOf(message.getStatus()));
        values.put(DBHandler.created_at, String.valueOf(message.getCreatedAt()));
        values.put(DBHandler.updated_at, String.valueOf(message.getUpdatedAt()));
        values.put(DBHandler.direction, String.valueOf(message.getDirection()));
        database.insert(DBHandler.TABLE, null, values);
        close();
    }

    public void addFast(ArrayList<Messages> data) {
        open();
        String sql = "INSERT OR REPLACE INTO " + DBHandler.TABLE + " ( " + DBHandler.message_id + ", "
                + DBHandler.agent_id + ", " + DBHandler.sender_id + " , " + DBHandler.message_type +
                DBHandler.category_id + ", " + DBHandler.message_values + " , " + DBHandler.status +
                DBHandler.created_at + ", " + DBHandler.updated_at + " , " + DBHandler.direction +
                " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

        database.beginTransactionNonExclusive();
        // db.beginTransaction();

        SQLiteStatement stmt = database.compileStatement(sql);
        for (Messages message : data) {

            stmt.bindString(1, String.valueOf(message.get_id()));
            stmt.bindString(2, String.valueOf(message.getAgentId()));
            stmt.bindString(3, String.valueOf(message.getSenderId()));
            stmt.bindString(4, String.valueOf(message.getMessageType()));
            stmt.bindString(5, String.valueOf(message.getCategory()));
            stmt.bindString(6, String.valueOf(message.getMessageValues().toString()));
            stmt.bindString(7, String.valueOf(message.getStatus()));
            stmt.bindString(8, String.valueOf(message.getCreatedAt()));
            stmt.bindString(9, String.valueOf(message.getUpdatedAt()));
            stmt.bindString(10, String.valueOf(message.getDirection()));

            stmt.execute();
            stmt.clearBindings();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close();
    }

    public ArrayList<Messages> getAllMessages() {
        open();
        Cursor cursor = database.query(DBHandler.TABLE,
                new String[]{DBHandler.message_id, DBHandler.agent_id, DBHandler.sender_id
                        , DBHandler.message_type, DBHandler.category_id
                        , DBHandler.message_values, DBHandler.status
                        , DBHandler.created_at, DBHandler.updated_at
                        , DBHandler.direction},
                null, null, null, null, null);
        ArrayList<Messages> labels = parseCursor(cursor);
        cursor.close();
        close();
        return labels;
    }

    private ArrayList<Messages> parseCursor(Cursor cursor) {
        ArrayList<Messages> labels = new ArrayList<Messages>();
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject;
                MessageValues messageValues = new MessageValues();
                try {
                    jsonObject = new JSONObject(cursor.getString(5));
                    if (jsonObject.has("id")) {
                        if (jsonObject.getInt("id") == 1) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"));
                        }
                        if (jsonObject.getInt("id") == 2) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("url"), jsonObject.getString("caption"));
                        }
                        if (jsonObject.getInt("id") == 3) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("caption"), jsonObject.getDouble("lng"), jsonObject.getDouble("lat"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                labels.add(new Messages(cursor.getString(0), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), messageValues,
                        Integer.parseInt(cursor.getString(6)), Long.parseLong(cursor.getString(7)),
                        Long.parseLong(cursor.getString(8)), Integer.parseInt(cursor.getString(9))));
            } while (cursor.moveToNext());
        }
        return labels;
    }

    public ArrayList<Messages> getAllListBasedOnCategory(String category_id) {
        open();
        Cursor cursor = database.query(DBHandler.TABLE,
                new String[]{DBHandler.message_id, DBHandler.agent_id, DBHandler.sender_id
                        , DBHandler.message_type, DBHandler.category_id
                        , DBHandler.message_values, DBHandler.status
                        , DBHandler.created_at, DBHandler.updated_at
                        , DBHandler.direction},
                DBHandler.category_id + "== " + category_id, null, null, null, null);
        ArrayList<Messages> labels = parseCursor(cursor);
        cursor.close();
        close();
        return labels;
    }
}