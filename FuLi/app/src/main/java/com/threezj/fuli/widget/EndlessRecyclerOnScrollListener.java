package com.threezj.fuli.widget;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Zj on 2016/1/5.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = false;
    //list到达 最后一个item的时候 触发加载
    private int visibleThreshold = 1;
    // The minimum amount of items to have below your current scroll position before loading more.
    int[] firstVisibleItem = new int[2];
    int visibleItemCount, totalItemCount;
    //默认第一页
    private int current_page = 1;

    private StaggeredGridLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(StaggeredGridLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPositions(null);

        //判断加载完成了...
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        // // TODO: 15/10/7 可能把 Toolbar 的高度也算上了
        //totalItemCount > visibleItemCount 超过一个页面才有加载更多
        if (!loading && totalItemCount > visibleItemCount && (totalItemCount - visibleItemCount) <= (firstVisibleItem[0] + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    public int getVisibleThreshold() {
        return visibleThreshold;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public abstract void onLoadMore(int current_page);
}