package com.example.moudgil.gifzone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 30/05/17.
 */

public class GifDBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "gifs.db";
    public static final int DBVERSION = 1;

    public GifDBHelper(Context context) {
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_STATEMENT = "CREATE TABLE " + GifContract.GifEntry.TABLE_NAME + " (" +


                GifContract.GifEntry._ID + " INTEGER, " +

                GifContract.GifEntry.COLUMN_GIFID + " TEXT NOT NULL, " +

                GifContract.GifEntry.COLUMN_GIF_URL + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(CREATE_STATEMENT);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GifContract.GifEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}