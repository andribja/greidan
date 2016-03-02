package com.greidan.greidan.greidan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daníel on 01/03/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "greidan.db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbSchema.DROP_TABLE_ADS);
        db.execSQL(DbSchema.DROP_TABLE_USERS);

        db.execSQL(DbSchema.CREATE_TABLE_ADS);
        db.execSQL(DbSchema.CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: implement this
    }
}
