package com.example.moudgil.gifzone.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.Target;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.app.Config;
import com.example.moudgil.gifzone.data.GifContract;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by apple on 30/05/17.
 */

public class GifWidgetRemoteViewService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(GifContract.GifEntry.CONTENT_URI_TRENDING,
                        null,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                final RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list);

                final int urlIndex = data.getColumnIndex(GifContract.GifEntry.COLUMN_GIF_URL);

                data.moveToPosition(position);
                final String gifURL = data.getString(urlIndex);


                BitmapRequestBuilder builder =
                        Glide.with(getApplicationContext())
                                .load(Uri.parse(gifURL))
                                .asBitmap()
                                .centerCrop();
                FutureTarget futureTarget = builder.into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                try {
                    views.setImageViewBitmap(R.id.gif_image, (Bitmap) futureTarget.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }


                final Intent fillInIntent = new Intent();
                //   fillInIntent.putExtra("symbol",data.getString(Contract.Quote.POSITION_SYMBOL));
                fillInIntent.putExtra(Config.NAV_TYPE, Config.NAV_TRENDING);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
