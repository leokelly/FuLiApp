package com.threezj.fuli.Fragment;


import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Zj on 2016/1/28.
 */
public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;


    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private ArrayList<ImageFuli> imagesList = new ArrayList<>();
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String jsonResponseFromGank;
    private final int LOAD_IMAGE_COUNT = 1000;
    private final int ONCE_LOAD_NUMBER = 20;
    private int currentImagePosition;
    private Realm realm;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        realm = Realm.getInstance(getActivity());
        currentImagePosition= SharedPreferencesUtil.getCurrentImagePosition(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.content);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresher);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        recyclerView.addOnScrollListener(getOnBottomListener(gaggeredGridLayoutManager));
    }

    private void init() {

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
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getActivity(), imagesList){
            @Override
            protected void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
                intent.putExtra("CURRENT_INDEX",position);
                startActivity(intent);
            }
        };
        recyclerView.setAdapter(imageRecyclerViewAdapter);

    }

    private void getImagesDataFromHttp() {
        Log.d("test", "getDataFromHttp");
        if(jsonResponseFromGank==null){
            HttpUtil.httpRequestToGank(getActivity(), ApiUrl.gankApiUrl + (LOAD_IMAGE_COUNT) + "/1", new HttpUtil.HttpUtilCallBack() {
                @Override
                public void onFinsh(String response) {
                    jsonResponseFromGank = response;
                    handleResponse();
                }

                @Override
                public void onError(Exception e) {

                    Toast.makeText(getActivity(), "加载失败！", Toast.LENGTH_SHORT).show();
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
                    ResponseHandleUtil.HandleResponseFromHttp(jsonResponseFromGank, getActivity(), imagesList, currentImagePosition);
                    currentImagePosition += ONCE_LOAD_NUMBER;
                } catch (ExecutionException | InterruptedException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                getActivity().runOnUiThread(new Runnable() {
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
        RealmResults<ImageFuli> images = realm.allObjects(ImageFuli.class);

        if(images.size()==0){
            return false;
        }
        else{
            for(ImageFuli imageFuli : images){
                imagesList.add(imageFuli);
            }
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
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        SharedPreferencesUtil.saveCurrentImagePosition(getActivity(), currentImagePosition);

    }
}
