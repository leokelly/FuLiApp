package com.threezj.fuli.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.astuetz.PagerSlidingTabStrip;
import com.threezj.fuli.Fragment.PageFragment;
import com.threezj.fuli.R;

public class MainActivity extends AppCompatActivity {


    private ViewPager mainViewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        mainViewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_indicator);
        pagerSlidingTabStrip.setViewPager(mainViewPager);

    }



    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3" };

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }
}
