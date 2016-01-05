package com.threezj.fuli.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zj on 2015/12/28.
 */
public class HttpUtil {

    public static void httpRequestToGank(final String address, final HttpUtilCallBack httpUtilCallBack){
        new Thread(new Runnable() {

            @Override
            public void run() {

                HttpURLConnection connection=null;
                try{
                    URL url =new URL(address);
                    connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader =new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder("");
                    String line ;
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }

                    if(httpUtilCallBack!=null){
                        httpUtilCallBack.onFinsh(response.toString());
                    }
                }
                catch(Exception e){
                    if(httpUtilCallBack!=null){
                        httpUtilCallBack.onError(e);
                    }
                }
            }
        }).start();
    }

    public interface HttpUtilCallBack {
        void onFinsh(String response);
        void onError(Exception e);

    }
}
