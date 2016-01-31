package com.threezj.fuli.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.astuetz.PagerSlidingTabStrip;
import com.threezj.fuli.Fragment.PageFragment;
import com.threezj.fuli.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private ViewPager mainViewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UmengUpdateAgent.update(this);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        mainViewPager.setOffscreenPageLimit(1);
        mainViewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_indicator);
        pagerSlidingTabStrip.setViewPager(mainViewPager);

        //指示条颜色
        pagerSlidingTabStrip.setIndicatorColor(R.color.blue_500);
        //指示条高度
        pagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
    }



    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Gank美女", "豆瓣所有", "大胸妹", "小翘臀", "黑丝袜", "美图控", "有颜值"};

        private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            //if(fragments.size()<=position) fragments.add(PageFragment.newInstance(position));
            return PageFragment.newInstance(position);
            //return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
