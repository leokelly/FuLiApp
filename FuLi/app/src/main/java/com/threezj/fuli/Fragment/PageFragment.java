package com.threezj.fuli.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.threezj.fuli.ApiUrl;
import com.threezj.fuli.R;
import com.threezj.fuli.Util.HttpUtil;
import com.threezj.fuli.Util.ResponseHandleUtil;
import com.threezj.fuli.Util.SharedPreferencesUtil;
import com.threezj.fuli.activity.ImageViewerActivity;
import com.threezj.fuli.adapter.ImageRecyclerViewAdapter;
import com.threezj.fuli.model.ImageFuli;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Zj on 2016/1/28.
 */
public class PageFragment extends Fragment {

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RealmResults<ImageFuli> images;

    private final int PRELOAD_SIZE = 6;
    private int mPage;

    private String httpResponse;
    private final int LOAD_IMAGE_COUNT = 1000;
    private final int ONCE_LOAD_NUMBER = 20;
    private Realm realm;
    private boolean isFirstRequestToGank = true;

    private static final String TPEY_ARGS_KEY = "TYPE_KEY";

    private final int REQUEST_TO_GANK = 0;
    private final int REQUEST_TO_DOUBAN = 1;
    private final int REQUEST_TO_DOUBAN_DAXIONG = 2;
    private final int REQUEST_TO_DOUBAN_QIAOTUN = 3;
    private final int REQUEST_TO_DOUBAN_HEISI = 4;
    private final int REQUEST_TO_DOUBAN_MEITUI = 5;
    private final int REQUEST_TO_YANZHI = 6;
    private int TYPE = REQUEST_TO_GANK;


    public static PageFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TPEY_ARGS_KEY, type);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TYPE = getArguments().getInt(TPEY_ARGS_KEY);
        realm = Realm.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.content);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        mPage=SharedPreferencesUtil.getCurrentPage(getActivity(),TYPE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();

        if(images.isEmpty()){
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

            preHttpRequest();

        }
        else recyclerView.getAdapter().notifyDataSetChanged();

        recyclerView.addOnScrollListener(getOnBottomListener(gaggeredGridLayoutManager));
    }

    private void init() {


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "onRefresh");
                isFirstRequestToGank = true;
                mPage = 1;

                realm.beginTransaction();
                realm.where(ImageFuli.class)
                        .equalTo("type",TYPE).findAll().clear();
                realm.commitTransaction();
                preHttpRequest();
            }
        });


        images= realm.where(ImageFuli.class)
                .equalTo("type", TYPE).findAll();

        recyclerView.setHasFixedSize(true);
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getActivity(), images){
            @Override
            protected void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                intent.putExtra("CURRENT_INDEX",position);
                intent.putExtra("TYPE",TYPE);
                startActivity(intent);
            }
        };
        recyclerView.setAdapter(imageRecyclerViewAdapter);

    }

    private void preHttpRequest(){
        String url = "";
        HttpUtil.HttpUtilCallBack httpUtilCallBack=null;
        switch (TYPE){
            case REQUEST_TO_GANK:
                url = ApiUrl.gankApiUrl + (LOAD_IMAGE_COUNT) + "/1";
                break;
            case REQUEST_TO_DOUBAN:
                url= ApiUrl.DouBanAllUrl + mPage;
                break;
            case REQUEST_TO_DOUBAN_DAXIONG:
                url= ApiUrl.DouBanDaXiongUrl + mPage;
                break;
            case REQUEST_TO_DOUBAN_QIAOTUN:
                url= ApiUrl.DouBanXiaoQiaoTunUrl + mPage;
                break;
            case REQUEST_TO_DOUBAN_HEISI:
                url= ApiUrl.DouBanHeiSiWaTunUrl + mPage;
                break;
            case REQUEST_TO_DOUBAN_MEITUI:
                url= ApiUrl.DouBanMeiTuiUrl + mPage;
                break;
            case REQUEST_TO_YANZHI:
                url= ApiUrl.DouBanYanZhiUrl + mPage;
                break;
            default:
                break;
        }
        httpUtilCallBack = getHttpUtilCallBack();
        getImagesDataFromHttp(url, httpUtilCallBack);
    }

    @NonNull
    private HttpUtil.HttpUtilCallBack getHttpUtilCallBack() {

        return new HttpUtil.HttpUtilCallBack() {
            @Override
            public void onFinsh(String response) {
                httpResponse = response;
                isFirstRequestToGank = false;
                handleResponse();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "加载失败！", Toast.LENGTH_SHORT).show();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };

    }

    private void getImagesDataFromHttp(String url, HttpUtil.HttpUtilCallBack httpUtilCallBack) {

        if(isFirstRequestToGank || TYPE != REQUEST_TO_GANK){
            HttpUtil.httpRequest(getActivity(), url,httpUtilCallBack);
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
                    switch (TYPE){
                        case REQUEST_TO_GANK:
                            ResponseHandleUtil.HandleGankResponseFromHttp(getActivity(),httpResponse,(mPage-1) * ONCE_LOAD_NUMBER,ONCE_LOAD_NUMBER,TYPE);
                            break;
                        default:
                            ResponseHandleUtil.HandleDoubanResponseFromHttp(getActivity(),httpResponse,TYPE);
                            break;
                    }

                } catch (ExecutionException | InterruptedException |JSONException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "加载失败，请再次尝试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //recyclerView.getAdapter().notifyDataSetChanged();
                        if(mPage * ONCE_LOAD_NUMBER - ONCE_LOAD_NUMBER - 1>0)
                            recyclerView.getAdapter().notifyItemRangeChanged(mPage * ONCE_LOAD_NUMBER - ONCE_LOAD_NUMBER - 1, ONCE_LOAD_NUMBER);
                        else
                            recyclerView.getAdapter().notifyDataSetChanged();
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        }).start();

    }

    private RecyclerView.OnScrollListener getOnBottomListener(final StaggeredGridLayoutManager layoutManager) {
        return new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView rv, int dx, int dy) {
                boolean isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1] >= imageRecyclerViewAdapter.getItemCount() - PRELOAD_SIZE;
                if (!swipeRefreshLayout.isRefreshing() && isBottom) {
                    swipeRefreshLayout.setRefreshing(true);
                    mPage++;
                    preHttpRequest();

                }
            }
        };
    }


    @Override
    public void onDestroy() {
        realm.close();
        SharedPreferencesUtil.saveCurrentImagePositionAndPage(getActivity(),mPage, TYPE);
        super.onDestroy();
    }
}
