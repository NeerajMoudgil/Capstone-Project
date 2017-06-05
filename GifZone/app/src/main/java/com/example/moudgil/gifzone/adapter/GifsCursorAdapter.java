package com.example.moudgil.gifzone.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.data.GifContract;
import com.example.moudgil.gifzone.data.GifImage;

/**
 * Created by apple on 30/05/17.
 */

public class GifsCursorAdapter extends RecyclerView.Adapter<GifsCursorAdapter.GifsAdapterViewHolder> {


    private final GifsCursorOnClickHandler mClickHandler;
    private Cursor mCursor;
    private Context mContext;


    public GifsCursorAdapter(GifsCursorOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @Override
    public GifsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        int layoutIdForListItem = R.layout.category_card;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new GifsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GifsAdapterViewHolder holder, int position) {

        int gifIDindex = mCursor.getColumnIndex(GifContract.GifEntry.COLUMN_GIFID);
        final int urlIndex = mCursor.getColumnIndex(GifContract.GifEntry.COLUMN_GIF_URL);
        Log.i("posterIndex", String.valueOf(gifIDindex));

        mCursor.moveToPosition(position);
        ViewCompat.setTransitionName(holder.gifImage, String.valueOf(position) + "_image");

        String gifID = mCursor.getString(gifIDindex);
        final String gifURL = mCursor.getString(urlIndex);
        Glide.with(mContext).load(Uri.parse(gifURL)).asGif().into(holder.gifImage);
        ;

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void swapCursor(Cursor c) {

        mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();

        }

    }


    /**
     * The interface that provides onclick.
     */
    public interface GifsCursorOnClickHandler {
        void onClickCursor(GifsAdapterViewHolder gifsAdapterViewHolder, GifImage gifImage);
    }

    public class GifsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public final ImageView gifImage;

        public GifsAdapterViewHolder(View itemView) {
            super(itemView);
            gifImage = (ImageView) itemView.findViewById(R.id.gif_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int gifIDindex = mCursor.getColumnIndex(GifContract.GifEntry.COLUMN_GIFID);
            final int urlIndex = mCursor.getColumnIndex(GifContract.GifEntry.COLUMN_GIF_URL);
            Log.i("posterIndex", String.valueOf(gifIDindex));
            String gifID = mCursor.getString(gifIDindex);
            final String gifURL = mCursor.getString(urlIndex);

            GifImage gifImage = new GifImage(gifURL, gifID, null);
            mClickHandler.onClickCursor(this, gifImage);
        }
    }
}
