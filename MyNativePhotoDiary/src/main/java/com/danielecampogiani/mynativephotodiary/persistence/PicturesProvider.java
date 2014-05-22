package com.danielecampogiani.mynativephotodiary.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class PicturesProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.danielecampogiani.picturesprovider/pictures");
    private static final int ALLROWS = 1;
    private static final int SINGLE_ROW = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.danielecampogiani.picturesprovider", "pictures", ALLROWS);
        uriMatcher.addURI("com.danielecampogiani.picturesprovider", "pictures/#", SINGLE_ROW);
    }

    public static final String KEY_ID = "_id";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_URI = "uri";

    private MySQLiteOpenHelper mySQLiteOpenHelper;

    @Override
    public boolean onCreate() {

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getContext(),MySQLiteOpenHelper.DATABASE_NAME,null,MySQLiteOpenHelper.DATABASE_VERSION);

        return true;
    }

    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)){
            case ALLROWS : return "vnd.android.cursor.dir/vnd.danielecampogiani.pictures";
            case SINGLE_ROW : return "vnd.android.cursor.item/vnd.danielecampogiani.pictures";
            default: throw new IllegalArgumentException("Unsupported URI:" +uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();

        String nullColumnHack = null;

        long id = db.insert(MySQLiteOpenHelper.DATABASE_TABLE,nullColumnHack,values);

        if (id>-1){
            Uri insertedId = ContentUris.withAppendedId(CONTENT_URI,id);
            getContext().getContentResolver().notifyChange(insertedId,null);
            return insertedId;
        }
        else
            return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case SINGLE_ROW :
                String rowID= uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                "AND (" + selection + ")" : "" );
            default: break;
        }

        int updateCount = db.update(MySQLiteOpenHelper.DATABASE_TABLE,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);

        return updateCount;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case SINGLE_ROW :
                String rowID = uri.getPathSegments().get(1);
                selection = KEY_ID + "=" + rowID
                        + (!TextUtils.isEmpty(selection) ?
                "AND (" + selection + ")" : "" );
            default: break;
        }

        if (selection==null)
            selection="1"; //to delete all rows

        int deleteCount = db.delete(MySQLiteOpenHelper.DATABASE_TABLE,selection,selectionArgs);

        getContext().getContentResolver().notifyChange(uri,null);

        return deleteCount;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mySQLiteOpenHelper.getWritableDatabase();
        String groupBy = null;
        String having = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(MySQLiteOpenHelper.DATABASE_TABLE);

        switch (uriMatcher.match(uri)){
            case SINGLE_ROW :
                String rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(KEY_ID + "=" +rowID);
            default: break;
        }

        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,groupBy,having,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;

    }


    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME ="picturesDatabase.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_TABLE = "picturesTable";

        private static final String DATABASE_CREATE = "create table "+
                DATABASE_TABLE + " ( " + KEY_ID +
                " integer primary key autoincrement, " +
                KEY_DESCRIPTION + " text not null, " +
                KEY_LATITUDE + " real, " +
                KEY_LONGITUDE + " real," +
                KEY_URI + " text not null);";

        private MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if it exists "+ DATABASE_TABLE);
            onCreate(db);
        }
    }

}
