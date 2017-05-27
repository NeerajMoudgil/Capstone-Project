package com.example.moudgil.gifzone.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moudgil.gifzone.R;
import com.example.moudgil.gifzone.TopGifActivity;
import com.example.moudgil.gifzone.data.Home;

import java.util.List;

/**
 * Created by apple on 24/05/17.
 */

public class HomeAdapter  extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{
        private Context mContext;
        private List<Home> homeList;

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView title, count;
    public ImageView thumbnail, overflow;

    public MyViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        view.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.d("adpte","onclick");
        Intent intent= new Intent(mContext, TopGifActivity.class);
        mContext.startActivity(intent);
    }
}


    public HomeAdapter(Context mContext, List<Home> homeList) {
        this.mContext = mContext;
        this.homeList = homeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Home home = homeList.get(position);
        holder.title.setText(home.getTitle());


        // loading album cover using Glide library
        Glide.with(mContext).load(home.getDrawableId()).into(holder.thumbnail);


    }




    @Override
    public int getItemCount() {
        return homeList.size();
    }
}