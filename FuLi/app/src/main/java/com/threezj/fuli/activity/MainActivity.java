package com.threezj.fuli.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.threezj.fuli.ApiUrl;
import com.threezj.fuli.R;
import com.threezj.fuli.Util.HttpUtil;
import com.threezj.fuli.Util.ResponseHandleUtil;
import com.threezj.fuli.Util.SharedPreferencesUtil;
import com.threezj.fuli.adapter.ImageRecyclerViewAdapter;
import com.threezj.fuli.model.ImageFuli;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private ArrayList<ImageFuli> imagesList = new ArrayList<>();
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isFirst = true;
    private String jsonResponseFromGank;
    private final int LOAD_IMAGE_COUNT = 1000;
    private final int ONCE_LOAD_NUMBER = 10;
    private int currentImagePosition;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if(!findFromDb()){
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
            getImagesDataFromHttp();
        }
        Log.d("test", "addScrolListener");
        recyclerView.addOnScrollListener(getOnBottomListener(gaggeredGridLayoutManager));
    }

    private void init() {

        currentImagePosition=SharedPreferencesUtil.getCurrentImagePosition(this);

        recyclerView = (RecyclerView)findViewById(R.id.content);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresher);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "onRefresh");
                getImagesDataFromHttp();
            }
        });

        recyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(MainActivity.this, imagesList);
        recyclerView.setAdapter(imageRecyclerViewAdapter);

    }

    private void getImagesDataFromHttp() {
        Log.d("test", "getDataFromHttp");
        if(jsonResponseFromGank==null){
            HttpUtil.httpRequestToGank(this,ApiUrl.gankApiUrl + (LOAD_IMAGE_COUNT) + "/1", new HttpUtil.HttpUtilCallBack() {
                @Override
                public void onFinsh(String response) {
                    jsonResponseFromGank=response;
                    handleResponse();
                }

                @Override
                public void onError(Exception e) {

                    Toast.makeText(MainActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
        else{
            handleResponse();
        }
    }

    private void handleResponse() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResponseHandleUtil.HandleResponseFromHttp(jsonResponseFromGank, MainActivity.this, imagesList, currentImagePosition);
                    currentImagePosition += ONCE_LOAD_NUMBER;
                } catch (ExecutionException | InterruptedException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //recyclerView.getAdapter().notifyDataSetChanged();
                        recyclerView.getAdapter().notifyItemRangeChanged(currentImagePosition - ONCE_LOAD_NUMBER, ONCE_LOAD_NUMBER);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        }).start();

    }

    public boolean findFromDb(){
        Realm realm = Realm.getInstance(this);
        RealmResults<ImageFuli> images = realm.allObjects(ImageFuli.class);

        if(images.size()==0){
            //realm.close();
            return false;
        }
        else{
            isFirst=false;
            imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(MainActivity.this, images);
            recyclerView.setAdapter(imageRecyclerViewAdapter);
            recyclerView.getAdapter().notifyDataSetChanged();
            return true;
        }
    }

    private static final int PRELOAD_SIZE = 6;


    private RecyclerView.OnScrollListener getOnBottomListener(final StaggeredGridLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView rv, int dx, int dy) {
                boolean isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1] >= imageRecyclerViewAdapter.getItemCount() - PRELOAD_SIZE;
                if (!swipeRefreshLayout.isRefreshing() && isBottom) {
                    swipeRefreshLayout.setRefreshing(true);
                    getImagesDataFromHttp();

                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.saveCurrentImagePosition(this, currentImagePosition);
    }
}
