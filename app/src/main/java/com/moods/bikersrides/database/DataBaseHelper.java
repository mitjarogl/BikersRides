package com.moods.bikersrides.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.moods.bikersrides.database.dao.DaoMaster;
import com.moods.bikersrides.database.dao.DaoSession;

public class DataBaseHelper {
    private static DataBaseHelper sInstance = null;
    private static final String DATABASE_NAME = "bikersrides.db";
    private SQLiteDatabase db;
    DaoMaster.DevOpenHelper helper;
    private DaoMaster daoMaster;

    private DataBaseHelper(Context context) {
        helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
    }

    public static DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DaoSession getSession() {
        return daoMaster.newSession();
    }

}
