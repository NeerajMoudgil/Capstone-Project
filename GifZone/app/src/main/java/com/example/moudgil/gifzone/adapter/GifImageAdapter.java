package com.example.moudgil.gifzone.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.data.GifImage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 24/05/17.
 */

public class GifImageAdapter extends RecyclerView.Adapter<GifImageAdapter.MyViewHolder> {
    private Context mContext;
    private List<GifImage> gifImageList;
    // handling click events on view item
    private ImageClickedListener mClickListener;

    public GifImageAdapter(ImageClickedListener imgClickedListenr) {
        this.mClickListener = imgClickedListenr;
    }

    @Override
    public GifImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card, parent, false);

        return new GifImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GifImageAdapter.MyViewHolder holder, int position) {
        GifImage gifImage = gifImageList.get(position);

        ViewCompat.setTransitionName(holder.gifImage, String.valueOf(position) + "_image");

        String tag = gifImage.getHashTAg();
        if (tag != null && !TextUtils.isEmpty(tag)) {
            holder.hashTagTextView.setVisibility(View.VISIBLE);
            String tagText = "#" + tag;
            holder.hashTagTextView.setText(tagText);
        }
        // loading gif image using Glide library
        Glide.with(mContext).load(Uri.parse(gifImage.getUrl())).asGif().into(holder.gifImage);


    }

    @Override
    public int getItemCount() {
        return gifImageList == null ? 0 : gifImageList.size();
    }

    public void setGifImageList(List<GifImage> gifImageList) {
        this.gifImageList = gifImageList;
        notifyDataSetChanged();
    }

    //Interface callback for handling click events
    public interface ImageClickedListener {
        void onImageClicekd(MyViewHolder viewHolder, GifImage gifImage);
    }

    /**
     *  View holder class for item
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView gifImage;
        @BindView(R.id.hashTag_text)
        TextView hashTagTextView;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            gifImage = (ImageView) view.findViewById(R.id.gif_image);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            GifImage gifImage = gifImageList.get(getAdapterPosition());
            mClickListener.onImageClicekd(this, gifImage);

        }
    }

}
