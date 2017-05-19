package com.kankanla.m0417b;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by kankanla on 2017/04/21.
 */

public class FileSQL {
    private Context context;
    private Data_db data_db;
    private static final String db_name = "m0417b.db";
    private static final int db_version = 1;
    public static final String ALBUM_FRAG_NOACTIVETE = "NOACTIVITE";
    public static final String ALBUM_FRAG_ACTIVETE = "ACTIVITE";

    public FileSQL(Context context) {
        this.context = context;
        data_db = new Data_db(context);
        data_db.getReadableDatabase();
        data_db.close();
    }

    public void getUriinfo(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                System.out.println("uri.toString");
                System.out.println(uri.toString());
                System.out.println(uri.getAuthority());

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    System.out.println("getColumnName    " + cursor.getColumnName(i));
                    System.out.println("getString    " + cursor.getString(i));
                }
            }
        }
    }

    public boolean add_album() {
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
        long cont = sqLiteDatabase.insert(table_name, null, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_name", "New Album");
        contentValues.put("last_modified", System.currentTimeMillis());
        sqLiteDatabase.insert(table_name, null, contentValues);
        sqLiteDatabase.close();
        if (cont > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean up_album_name(String album_name, String album_id) {
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_name", album_name);
        contentValues.put("last_modified", System.currentTimeMillis());
        int count = sqLiteDatabase.update(table_name, contentValues, "_id", new String[]{album_id});
        sqLiteDatabase.close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean up_album_icon(String album_icon, String album_id) {
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_icon", album_icon);
        int count = sqLiteDatabase.update(table_name, contentValues, "_id", new String[]{album_id});
        sqLiteDatabase.close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }


    /*
    com.android.externalstorage.documents
    _display_name
    com.google.android.apps.docs.storage
    _display_name
    com.microsoft.skydrive.content.StorageAccessProvider
    _display_name
     */

    public long add_info_sound(Uri uri) {
        String table_name = "info_sound";
        String table_key_uri = "document_uri";
        String table_key_name = "_display_name";
        ContentValues contentValues = new ContentValues();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        switch (uri.getAuthority()) {
            case "com.android.externalstorage.documents":
                cursor.moveToFirst();
                contentValues.put(table_key_uri, uri.toString());
                contentValues.put(table_key_name, cursor.getString(cursor.getColumnIndex("_display_name")));
                break;
            case "com.google.android.apps.docs.storage":
                cursor.moveToFirst();
                contentValues.put(table_key_uri, uri.toString());
                contentValues.put(table_key_name, cursor.getString(cursor.getColumnIndex("_display_name")));
                break;
            case "com.microsoft.skydrive.content.StorageAccessProvider":
                cursor.moveToFirst();
                contentValues.put(table_key_uri, uri.toString());
                contentValues.put(table_key_name, cursor.getString(cursor.getColumnIndex("_display_name")));
                break;
            default:
                break;
        }

        long id = 0;
        if (contentValues.size() > 0) {
            SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
            id = sqLiteDatabase.insertWithOnConflict(table_name, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
            sqLiteDatabase.close();
        }
        return id;
    }


    public long get_info_sound_id(Uri uri) {
        String table_name = "info_sound";
        SQLiteDatabase sqLiteDatabase = data_db.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"*"}, "document_uri == ?", new String[]{uri.toString()}, null, null, null);

        if (cursor.getCount() == 0) {
            return add_info_sound(uri);
        } else {
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex("_id"));
            return id;
        }
    }

    public Cursor info_sound_all_quiry() {
        String table_name = "info_sound";
        SQLiteDatabase sqLiteDatabase = data_db.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"*"}, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String key = cursor.getColumnName(i);
                    String val = cursor.getString(i);
                }
            }
        }
        sqLiteDatabase.close();
        return cursor;
    }

    public void set_album_frag(String album_frag, String album_id) {
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("flags", album_frag);
        int cont = sqLiteDatabase.update(table_name, contentValues, "_id = ?", new String[]{album_id});
        sqLiteDatabase.close();
    }

    public void set_album_icon(Uri album_icon_uri,String album_id){
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_icon",album_icon_uri.toString());
        int i = sqLiteDatabase.update(table_name,contentValues,"_id = ? ",new String[]{album_id});
        if(i > 0){

        }
        sqLiteDatabase.close();
    }

    public Cursor get_album_all_item() {
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"*"}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String key = cursor.getColumnName(i);
                    String val = cursor.getString(i);
                }
            }
        }
        sqLiteDatabase.close();
        return cursor;
    }


    public Cursor get_album_all_item_active() {
        String table_name = "album_list";
        SQLiteDatabase sqLiteDatabase = data_db.getReadableDatabase();
        String selected = "flags == ?";
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"*"}, selected, new String[]{ALBUM_FRAG_ACTIVETE}, null, null, null);
        if (cursor != null) {
            return cursor;
        } else {
            sqLiteDatabase.close();
        }
        return null;
    }

    /*
     */
    public Cursor get_album_id_item(String album_id) {
//        String table_name = "link_list";
//        String table_name1 = "info_sound";
        SQLiteDatabase sqLiteDatabase = data_db.getReadableDatabase();
//        String selected = "album_list_id == ? ";
//        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{"*"}, selected, new String[]{album_id}, null, null, null);
//        SELECT カラム名 , ... FROM テーブル名 WHERE カラム NOT IN(SELECT カラム名 FROM テーブル名);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from info_sound where _id in(Select info_sound_id from link_list where album_list_id == ?  )", new String[]{album_id});

        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public boolean setAlbum_link_list(String album_list_id, String info_sound_id) {
        String table_name = "link_list";
        SQLiteDatabase sqLiteDatabase = data_db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("album_list_id", album_list_id);
        contentValues.put("info_sound_id", info_sound_id);
        long id = sqLiteDatabase.insertWithOnConflict(table_name, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        sqLiteDatabase.close();
        if (id > 0) {
            return true;
        } else {
            return false;
        }
    }


    private class Data_db extends SQLiteOpenHelper {

        public Data_db(Context context) {
            super(context, db_name, null, db_version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql_cmd1 = "create table info_sound (" +
                    "_id Integer primary key autoincrement," +
                    "document_uri text UNIQUE, " +
                    "_display_name text" +
                    "play_count integer)";

            String sql_cmd2 = "create table album_list (" +
                    "_id integer primary key autoincrement," +
                    "album_name text," +
                    "album_icon text," +
                    "last_modified integer," +
                    "flags text default ACTIVITE," +
                    "play_count integer)";

            String sql_cmd3 = "create table link_list (" +
                    "_id integer primary key autoincrement," +
                    "album_list_id text," +
                    "info_sound_id text," +
                    "flags text)";

            db.execSQL(sql_cmd1);
            db.execSQL(sql_cmd2);
            db.execSQL(sql_cmd3);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
