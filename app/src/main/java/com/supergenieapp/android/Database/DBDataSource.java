package com.supergenieapp.android.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.supergenieapp.android.Extras.DataFields;
import com.supergenieapp.android.Objects.Categories;
import com.supergenieapp.android.Objects.FavValues;
import com.supergenieapp.android.Objects.Messages;
import com.supergenieapp.android.Objects.MessageValues;

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

    public void cleanTable() throws SQLException {
        open();
        database.execSQL("delete from " + DBHandler.TABLE);
        close();
    }

    public void cleanCatTable() throws SQLException {
        open();
        database.execSQL("delete from " + DBHandler.CATTABLE);
        close();
    }

    public void cleanFavTable() throws SQLException {
        open();
        database.execSQL("delete from " + DBHandler.FAVTABLE);
        close();
    }

    public void close() throws SQLException {
        database.close();
    }

    public void addNormal(Messages message) {
        open();
        ContentValues values = new ContentValues();

        values.put(DBHandler.message_id, String.valueOf(message.get_id()));
        values.put(DBHandler.category_id, String.valueOf(message.getCategory()));
        values.put(DBHandler.message_values, String.valueOf(message.getMessageValues().toString()));
        values.put(DBHandler.status, String.valueOf(message.getStatus()));
        values.put(DBHandler.created_at, String.valueOf(message.getCreatedAt()));
        values.put(DBHandler.updated_at, String.valueOf(message.getUpdatedAt()));
        values.put(DBHandler.direction, String.valueOf(message.getDirection()));
        database.insert(DBHandler.TABLE, null, values);
        close();
    }

    public void addNormalCategories(Categories categories) {
        if (getCategoriesExists(String.valueOf(categories.getId())) == 1) {
            open();
            ContentValues values = new ContentValues();
            values.put(DBHandler.cat_name, categories.getName());
            values.put(DBHandler.img_url, categories.getImage_url());
            values.put(DBHandler.description, categories.getDescription());
            values.put(DBHandler.bg_color, categories.getBg_color());
            values.put(DBHandler.hide_chats_time, String.valueOf(categories.getHide_chats_time()));
            database.update(DBHandler.CATTABLE, values, DBHandler.cat_id + "==" + categories.getId(), null);
        } else {
            deleteCat(String.valueOf(categories.getId()));
        }
        close();
    }

    private void deleteCat(String id) {
        database.delete(DBHandler.CATTABLE, DBHandler.cat_id + "==" + id, null);
    }

    public void addFast(ArrayList<Messages> data) {
        open();
        String sql = "INSERT OR REPLACE INTO " + DBHandler.TABLE + " ( " + DBHandler.message_id + ", " +
                DBHandler.category_id + ", " + DBHandler.message_values + " , " + DBHandler.status + ", " +
                DBHandler.created_at + ", " + DBHandler.updated_at + " , " + DBHandler.direction +
                " ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";

        database.beginTransactionNonExclusive();
        // db.beginTransaction();

        SQLiteStatement stmt = database.compileStatement(sql);
        for (Messages message : data) {

            stmt.bindString(1, String.valueOf(message.get_id()));
            stmt.bindString(2, String.valueOf(message.getCategory()));
            stmt.bindString(3, String.valueOf(message.getMessageValues().toString()));
            stmt.bindString(4, String.valueOf(message.getStatus()));
            stmt.bindString(5, String.valueOf(message.getCreatedAt()));
            stmt.bindString(6, String.valueOf(message.getUpdatedAt()));
            stmt.bindString(7, String.valueOf(message.getDirection()));

            stmt.execute();
            stmt.clearBindings();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close();
    }

    public void addFastCategories(ArrayList<Categories> data) {
        open();
        String sql = "INSERT OR REPLACE INTO " + DBHandler.CATTABLE + " ( " + DBHandler.cat_id + ", "
                + DBHandler.notification + ", " + DBHandler.cat_name + " , " + DBHandler.img_url + " , " +
                DBHandler.description + ", " + DBHandler.bg_color + " , " + DBHandler.hide_chats_time +
                " ) VALUES ( ?, ?, ?, ?, ?, ?, ? )";

        database.beginTransactionNonExclusive();
        // db.beginTransaction();

        SQLiteStatement stmt = database.compileStatement(sql);
        for (Categories categories : data) {

            stmt.bindString(1, String.valueOf(categories.getId()));
            stmt.bindString(2, String.valueOf(categories.getNotification_count()));
            stmt.bindString(3, String.valueOf(categories.getName()));
            stmt.bindString(4, String.valueOf(categories.getImage_url()));
            stmt.bindString(5, String.valueOf(categories.getDescription()));
            stmt.bindString(6, String.valueOf(categories.getBg_color()));
            stmt.bindString(7, String.valueOf(categories.getHide_chats_time()));

            stmt.execute();
            stmt.clearBindings();
        }

        database.setTransactionSuccessful();
        database.endTransaction();
        close();
    }

    public void UpdateCatNotification(int catId, int notification) {
        open();
        ContentValues cv = new ContentValues();
        cv.put(DBHandler.notification, notification);
        database.update(DBHandler.CATTABLE, cv, DBHandler.cat_id + "=" + catId, null);
        close();
    }

    public ArrayList<Categories> getAllCategories() {
        open();
        Cursor cursor = database.query(DBHandler.CATTABLE,
                new String[]{DBHandler.cat_id, DBHandler.notification
                        , DBHandler.cat_name, DBHandler.img_url
                        , DBHandler.description, DBHandler.bg_color
                        , DBHandler.hide_chats_time},
                null, null, null, null, null);
        ArrayList<Categories> labels = parseCursorCat(cursor);
        cursor.close();
        close();
        return labels;
    }

    public Categories getCategories(int catId) {
        open();
        Cursor cursor = database.query(DBHandler.CATTABLE,
                new String[]{DBHandler.cat_id, DBHandler.notification
                        , DBHandler.cat_name, DBHandler.img_url
                        , DBHandler.description, DBHandler.bg_color
                        , DBHandler.hide_chats_time},
                DBHandler.cat_id + "== " + catId, null, null, null, null);
        ArrayList<Categories> list = parseCursorCat(cursor);
        Categories labels = null;
        if (list.size() > 0)
            labels = list.get(0);
        cursor.close();
        close();
        return labels;
    }

    public int getCategoriesExists(String cat_id) {
        open();
        Cursor cursor = database.query(DBHandler.CATTABLE,
                new String[]{DBHandler.cat_id, DBHandler.notification
                        , DBHandler.cat_name, DBHandler.img_url
                        , DBHandler.description, DBHandler.bg_color
                        , DBHandler.hide_chats_time},
                DBHandler.cat_id + "== " + cat_id, null, null, null, null);
        int labels = parseCursorCat(cursor).size();
        cursor.close();
        close();
        return labels;
    }

    public ArrayList<Messages> getAllMessages() {
        open();
        Cursor cursor = database.query(DBHandler.TABLE,
                new String[]{DBHandler.message_id, DBHandler.category_id
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
                    jsonObject = new JSONObject(cursor.getString(2));
                    if (jsonObject.has("id")) {
                        if (jsonObject.getInt("id") == DataFields.TEXT) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"));
                        } else if (jsonObject.getInt("id") == DataFields.IMAGE) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("url"), jsonObject.getString("text"));
                        } else if (jsonObject.getInt("id") == DataFields.LOCATION) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"), jsonObject.getDouble("lng"), jsonObject.getDouble("lat"));
                        } else if (jsonObject.getInt("id") == DataFields.PAYNOW) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                labels.add(new Messages(cursor.getString(0), messageValues.get_id(), Integer.parseInt(cursor.getString(1)), messageValues,
                        Integer.parseInt(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                        Long.parseLong(cursor.getString(5)), Integer.parseInt(cursor.getString(6))));
            } while (cursor.moveToNext());
        }
        return labels;
    }

    private ArrayList<Categories> parseCursorCat(Cursor cursor) {
        ArrayList<Categories> labels = new ArrayList<Categories>();
        if (cursor.moveToFirst()) {
            do {
                labels.add(new Categories(cursor.getInt(0), cursor.getInt(1), cursor.getString(5),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(2), Long.parseLong(cursor.getString(6))));
            } while (cursor.moveToNext());
        }
        return labels;
    }

    private ArrayList<Messages> parseCursorWithHideTime(Cursor cursor, long hide_time) {
        ArrayList<Messages> labels = new ArrayList<Messages>();
        if (cursor.moveToFirst()) {
            do {
                JSONObject jsonObject;
                MessageValues messageValues = new MessageValues();
                try {
                    jsonObject = new JSONObject(cursor.getString(2));
                    if (jsonObject.has("id")) {
                        if (jsonObject.getInt("id") == DataFields.TEXT) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"));
                        } else if (jsonObject.getInt("id") == DataFields.IMAGE) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("url"), jsonObject.getString("text"));
                        } else if (jsonObject.getInt("id") == DataFields.LOCATION) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"), jsonObject.getDouble("lng"), jsonObject.getDouble("lat"));
                        } else if (jsonObject.getInt("id") == DataFields.PAYNOW) {
                            messageValues = new MessageValues(jsonObject.getInt("id"), jsonObject.getString("text"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                long createdTime = Long.parseLong(cursor.getString(4));
                if (createdTime > hide_time) {
                    labels.add(new Messages(cursor.getString(0), messageValues.get_id(), Integer.parseInt(cursor.getString(1)), messageValues,
                            Integer.parseInt(cursor.getString(3)), Long.parseLong(cursor.getString(4)),
                            Long.parseLong(cursor.getString(5)), Integer.parseInt(cursor.getString(6))));
                }
            } while (cursor.moveToNext());
        }
        return labels;
    }

    public ArrayList<Messages> getAllListBasedOnCategory(String category_id) {
        open();
        Cursor cursor = database.query(DBHandler.TABLE,
                new String[]{DBHandler.message_id, DBHandler.category_id
                        , DBHandler.message_values, DBHandler.status
                        , DBHandler.created_at, DBHandler.updated_at
                        , DBHandler.direction},
                DBHandler.category_id + " == " + category_id, null, null, null, null);
        ArrayList<Messages> labels = parseCursor(cursor);
        cursor.close();
        close();
        return labels;
    }

    public ArrayList<Messages> getAllListBasedOnCategoryWithHideTime(String category_id, long hide_time) {
        open();
        Cursor cursor = database.query(DBHandler.TABLE,
                new String[]{DBHandler.message_id, DBHandler.category_id
                        , DBHandler.message_values, DBHandler.status
                        , DBHandler.created_at, DBHandler.updated_at
                        , DBHandler.direction},
                DBHandler.category_id + "== " + category_id, null, null, null, null);
        ArrayList<Messages> labels = parseCursorWithHideTime(cursor, hide_time);
        cursor.close();
        close();
        return labels;
    }

    public void addFavNormal(FavValues favValues) {
        open();
        ContentValues values = new ContentValues();
        values.put(DBHandler.address, favValues.getText());
        values.put(DBHandler.lat, String.valueOf(favValues.getLat()));
        values.put(DBHandler.lng, String.valueOf(favValues.getLng()));
        values.put(DBHandler.name, favValues.getName());
        database.insert(DBHandler.FAVTABLE, null, values);
        close();
    }

    public ArrayList<FavValues> getAllFav() {
        open();
        Cursor cursor = database.query(DBHandler.FAVTABLE,
                new String[]{DBHandler.name, DBHandler.address, DBHandler.lat
                        , DBHandler.lng},
                null, null, null, null, null);
        ArrayList<FavValues> labels = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                labels.add(new FavValues(3, cursor.getString(1), Double.parseDouble(cursor.getString(3)),
                        Double.parseDouble(cursor.getString(2)), cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return labels;
    }

    public boolean CheckIfExists(String name) {
        open();
        Cursor cursor = database.query(DBHandler.FAVTABLE,
                new String[]{DBHandler.name},
                null, null, null, null, null);
        ArrayList<String> namesList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                namesList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        boolean status = namesList.contains(name);
        cursor.close();
        close();
        return status;
    }

    public void UpdateMessages(int id, long hide_chats_time) {
        open();
        long time = (System.currentTimeMillis() - (hide_chats_time));
        database.delete(DBHandler.TABLE, DBHandler.category_id + "=" + id + " AND " + DBHandler.created_at + "<" + time, null);
        close();
    }

    public void cleanAll() {
        cleanCatTable();
        cleanFavTable();
        cleanTable();
    }

    public void deleteFav(FavValues favValues) {
        open();
        database.delete(DBHandler.FAVTABLE, DBHandler.name + " == '" + favValues.getName() + "'", null);
        close();
    }
}