package com.example.moudgil.gifzone.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by apple on 30/05/17.
 */

public class GifContract {
    public static final String CONTENT_AUTHORITY = "com.example.moudgil.gifzone";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NAME="gifs";
    public static final class GifEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "gifs";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME)
                .build();


        public static final String COLUMN_GIFID="gifid";

        public static final String COLUMN_GIF_URL="gifurl";

        public static Uri buildMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}
