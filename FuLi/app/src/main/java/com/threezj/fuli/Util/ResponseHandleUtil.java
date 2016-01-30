package com.threezj.fuli.Util;

import android.content.Context;
import android.util.Log;

import com.threezj.fuli.model.ImageFuli;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ExecutionException;

import io.realm.Realm;

/**
 * Created by Zj on 2015/12/28.
 */
public class ResponseHandleUtil {

    public static void HandleGankResponseFromHttp( Context context,String response, int currentImagePosition,int onceLoad,int type) throws ExecutionException, InterruptedException, JSONException {

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();//开启事务

        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("results");

        for(int i=currentImagePosition;currentImagePosition<jsonArray.length() && i<currentImagePosition+onceLoad;i++){
            jsonObject=jsonArray.getJSONObject(i);
            String url = jsonObject.getString("url");

            ImageFuli imageFuli =new ImageFuli(url);
//            Bitmap bitmap = Glide.with(context).load(imageFuli.getUrl()).asBitmap().thumbnail(0.1f)
//                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .get();
//            imageFuli.setWidth(bitmap.getWidth());
//            imageFuli.setHeight(bitmap.getHeight());
            imageFuli.setType(type);
            realm.copyToRealm(imageFuli);

        }

        realm.commitTransaction();//提交事务
        realm.close();
    }

    public static void HandleDoubanResponseFromHttp(Context context, String httpResponse,int type) throws ExecutionException, InterruptedException {
        Log.d("http","http"+type);
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();//开启事务
        Document document = Jsoup.parse(httpResponse);
        Elements elements = document.select("div[class=thumbnail]>div[class=img_single]>a>img");
        for (Element e :elements){
            String url = e.attr("src");
            ImageFuli imageFuli =new ImageFuli(url);
//            Bitmap bitmap = Glide.with(context).load(imageFuli.getUrl()).asBitmap().thumbnail(0.1f)
//                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .get();
//            imageFuli.setWidth(bitmap.getWidth());
//            imageFuli.setHeight(bitmap.getHeight());
            imageFuli.setType(type);
            realm.copyToRealm(imageFuli);
        }
        realm.commitTransaction();//提交事务
        realm.close();
    }
}
