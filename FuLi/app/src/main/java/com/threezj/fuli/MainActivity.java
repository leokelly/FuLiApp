package com.threezj.fuli;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    List<ImageFuli> gaggeredList = new ArrayList<ImageFuli>();
    ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        getImagesDataFromHttp();

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

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
    }

    private void getImagesDataFromHttp() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        HttpUtil.httpRequestToGank(ApiUrl.gankApiUrl, new HttpUtil.HttpUtilCallBack() {
            @Override
            public void onFinsh(String response) {
                try {
                    gaggeredList=ResponseHandleUtil.HandleResponseFromHttp(response,MainActivity.this);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(MainActivity.this, gaggeredList);
                        recyclerView.setAdapter(imageRecyclerViewAdapter);
                        imageRecyclerViewAdapter.notifyDataSetChanged();
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {

            }
        });

    }
}
