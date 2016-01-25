package com.threezj.fuli.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.threezj.fuli.R;
import com.threezj.fuli.model.ImageFuli;
import com.threezj.fuli.widget.ExtendedViewPager;
import com.threezj.fuli.widget.TouchImageView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Zj on 2016/1/25.
 */
public class ImageViewerActivity extends AppCompatActivity  {

    Realm realm;
    private RealmResults<ImageFuli> images;
    private int currentIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_example);
        ExtendedViewPager mViewPager = (ExtendedViewPager) findViewById(R.id.view_pager);

        currentIndex = getIntent().getIntExtra("CURRENT_INDEX", 0);

        realm=Realm.getInstance(this);
        images=realm.allObjects(ImageFuli.class);

        mViewPager.setAdapter(new TouchImageAdapter());
        mViewPager.setCurrentItem(currentIndex);
    }


    private class TouchImageAdapter extends PagerAdapter implements RequestListener<String, GlideDrawable> {
        private TouchImageView img;
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            img = new TouchImageView(container.getContext());
            Glide.with(ImageViewerActivity.this)
                    .load(images.get(position).getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade(0)
                    .listener(this)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            img.setImageDrawable(resource);
            notifyDataSetChanged();
            return true;
        }
    }
}
