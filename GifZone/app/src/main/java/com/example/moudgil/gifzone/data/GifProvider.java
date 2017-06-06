package com.example.moudgil.gifzone.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.moudgil.gifzone.app.Config;

/**
 * Created by apple on 30/05/17.
 */

public class GifProvider extends ContentProvider {

    final static int GIFSALL = 100;
    final static int GIFBYID = 101;
    final static int TRENDINGGIFS = 200;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GifDBHelper mGifDBHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher urimatcher = new UriMatcher(UriMatcher.NO_MATCH);
        urimatcher.addURI(GifContract.CONTENT_AUTHORITY, GifContract.PATH_NAME, GIFSALL);
        urimatcher.addURI(GifContract.CONTENT_AUTHORITY, GifContract.PATH_NAME + "/*", GIFBYID);
        urimatcher.addURI(GifContract.CONTENT_AUTHORITY, GifContract.PATH_NAME_TRENDING, TRENDINGGIFS);
        return urimatcher;

    }

    @Override
    public boolean onCreate() {
        mGifDBHelper = new GifDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;


        Log.i("GIF URI", uri.toString());

        switch (sUriMatcher.match(uri)) {
            case GIFSALL: {
                cursor = mGifDBHelper.getReadableDatabase().query(
                        GifContract.GifEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;
            }
            case GIFBYID: {

                String gifIO = uri.getLastPathSegment();

                String[] Selectionargs = new String[]{gifIO};

                cursor = mGifDBHelper.getReadableDatabase().query(
                        GifContract.GifEntry.TABLE_NAME,
                        projection,
                        selection + " = ?  ",
                        Selectionargs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;

            }

            case TRENDINGGIFS: {
                cursor = mGifDBHelper.getReadableDatabase().query(
                        GifContract.GifEntry.TRENDING_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mGifDBHelper.getWritableDatabase();


        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case GIFSALL:

                long id = db.insert(GifContract.GifEntry.TABLE_NAME, null, values);
                Log.i("dataInserted", String.valueOf(id));
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(GifContract.GifEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);


        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mGifDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case TRENDINGGIFS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {


                        long _id = db.insert(GifContract.GifEntry.TRENDING_TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();

                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                Log.i("rowsInserted", String.valueOf(rowsInserted));
                Intent dataUpdatedIntent = new Intent(Config.WIDGET_UPDATE_ACTION);
                getContext().sendBroadcast(dataUpdatedIntent);
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mGifDBHelper.getWritableDatabase();
        int id;
        switch (sUriMatcher.match(uri)) {
            case GIFBYID:
                String gifID = uri.getPathSegments().get(1);

                id = db.delete(GifContract.GifEntry.TABLE_NAME, GifContract.GifEntry.COLUMN_GIFID + "=?", new String[]{gifID});

                Log.i("DELETED", String.valueOf(id));

                break;

            case TRENDINGGIFS:
                id = db.delete(GifContract.GifEntry.TRENDING_TABLE_NAME, selection, selectionArgs);
                Log.i("DELETED", String.valueOf(id));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }


        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
