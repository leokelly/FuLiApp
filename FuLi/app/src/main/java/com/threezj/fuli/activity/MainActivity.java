package com.threezj.fuli.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.threezj.fuli.ApiUrl;
import com.threezj.fuli.R;
import com.threezj.fuli.Util.HttpUtil;
import com.threezj.fuli.Util.ResponseHandleUtil;
import com.threezj.fuli.adapter.ImageRecyclerViewAdapter;
import com.threezj.fuli.model.ImageFuli;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private List<ImageFuli> imagesList = new ArrayList<ImageFuli>();
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean onLoading = false;
    private int loadImageCount = 15;
    private int loadTimes=1;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getImagesDataFromHttp();

    }

    private void init() {
        recyclerView = (RecyclerView)findViewById(R.id.content);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresher);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImagesDataFromHttp();
            }
        });

        recyclerView.setHasFixedSize(true);

        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(MainActivity.this, imagesList);
        recyclerView.setAdapter(imageRecyclerViewAdapter);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

//        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(gaggeredGridLayoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                imageRecyclerViewAdapter.setHasFooter(true);
//                getImagesDataFromHttp();
//            }
//        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void getImagesDataFromHttp() {
        HttpUtil.httpRequestToGank(ApiUrl.gankApiUrl+(loadImageCount+5*loadTimes)+"/1" , new HttpUtil.HttpUtilCallBack() {
            @Override
            public void onFinsh(String response) {
                try {
                    imagesList = ResponseHandleUtil.HandleResponseFromHttp(response, MainActivity.this);
                    loadTimes++;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(MainActivity.this, imagesList);
//                        recyclerView.setAdapter(imageRecyclerViewAdapter);
                        imageRecyclerViewAdapter.notifyDataSetChanged();
                        imageRecyclerViewAdapter.setHasFooter(false);

                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }
        });

    }
}
