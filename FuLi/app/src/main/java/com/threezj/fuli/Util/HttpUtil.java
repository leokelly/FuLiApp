package com.threezj.fuli.Util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Zj on 2015/12/28.
 */
public class HttpUtil {

    public static void httpRequestToGank(Context context,final String address, final HttpUtilCallBack httpUtilCallBack){
        RequestQueue mQueue = Volley.newRequestQueue(context);

        Response.Listener<String> requestToGankListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                httpUtilCallBack.onFinsh(response);
            }
        };
        Response.ErrorListener requestToGankErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                httpUtilCallBack.onError(error);
            }
        };

        StringRequest requestToGank = new StringRequest(address, requestToGankListener, requestToGankErrorListener);

        mQueue.add(requestToGank);
    }


    public interface HttpUtilCallBack {
        void onFinsh(String response);
        void onError(Exception e);
    }
}
