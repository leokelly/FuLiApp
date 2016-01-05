package com.threezj.fuli;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by Zj on 2015/12/28.
 */
public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder>{

    private List<ImageFuli> images;
    private Context context;

    public ImageRecyclerViewAdapter(Context context, List<ImageFuli> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list, null);
        ImageViewHolder ivh = new ImageViewHolder(layoutView);
        return ivh;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ImageFuli imageFuli= images.get(position);
        holder.imageView.setOriginalSize(imageFuli.getWidth(), imageFuli.getHeight());
        Glide.with(context)
                .load(imageFuli.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        ViewCompat.setTransitionName(holder.imageView,imageFuli.getUrl());
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }


    public  class ImageViewHolder extends RecyclerView.ViewHolder {
        private RatioImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (RatioImageView) itemView.findViewById(R.id.image);
        }
    }
}

