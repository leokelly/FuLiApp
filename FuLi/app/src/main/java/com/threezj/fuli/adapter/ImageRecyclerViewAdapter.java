package com.threezj.fuli.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.threezj.fuli.R;
import com.threezj.fuli.model.ImageFuli;
import com.threezj.fuli.widget.RatioImageView;

import java.util.List;

/**
 * Created by Zj on 2015/12/28.
 */
public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    public static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;
    private boolean hasFooter;//设置是否显示Footer
    private boolean hasMoreData;//设置是否可以继续加载数据
    private List<ImageFuli> images;
    private Context context;

    public ImageRecyclerViewAdapter(Context context, List<ImageFuli> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){//底部 加载view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_progress, parent, false);
            return new FooterViewHolder(view);
        } else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list, null);
            ImageViewHolder ivh = new ImageViewHolder(layoutView);
            return ivh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FooterViewHolder) {
            if(hasMoreData){
                ((FooterViewHolder) holder).mProgressView.setVisibility(View.VISIBLE);
            } else {
                ((FooterViewHolder) holder).mProgressView.setVisibility(View.GONE);
            }
        } else {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            ImageFuli imageFuli= images.get(position);
            imageViewHolder.imageView.setOriginalSize(imageFuli.getWidth(), imageFuli.getHeight());
            Glide.with(context)
                    .load(imageFuli.getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewHolder.imageView);
            ViewCompat.setTransitionName(imageViewHolder.imageView,imageFuli.getUrl());
        }

    }

    @Override
    public int getItemCount() {
        return this.images.size() + (hasFooter ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == getBasicItemCount() && hasFooter) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;//0
    }

    private int getBasicItemCount() {
        return images.size();
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    public void setHasFooter(boolean hasFooter) {
        if(this.hasFooter != hasFooter) {
            this.hasFooter = hasFooter;
            notifyDataSetChanged();
        }
    }


    public boolean hasMoreData() {
        return hasMoreData;
    }

    public void setHasMoreData(boolean isMoreData) {
        if(this.hasMoreData != isMoreData) {
            this.hasMoreData = isMoreData;
            notifyDataSetChanged();
        }
    }
    public void setHasMoreDataAndFooter(boolean hasMoreData, boolean hasFooter) {
        if(this.hasMoreData != hasMoreData || this.hasFooter != hasFooter) {
            this.hasMoreData = hasMoreData;
            this.hasFooter = hasFooter;
            notifyDataSetChanged();
        }
    }


    public  class ImageViewHolder extends RecyclerView.ViewHolder {
        private RatioImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (RatioImageView) itemView.findViewById(R.id.image);
        }
    }
    public  class FooterViewHolder extends RecyclerView.ViewHolder {
        public final View mProgressView;

        public FooterViewHolder(View view) {
            super(view);
            mProgressView = View.inflate(context,R.layout.footer_progress, null);
        }
    }
}

