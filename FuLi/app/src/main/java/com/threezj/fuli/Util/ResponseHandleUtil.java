package com.threezj.fuli.Util;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.threezj.fuli.model.ImageFuli;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;

/**
 * Created by Zj on 2015/12/28.
 */
public class ResponseHandleUtil {

    public static void HandleResponseFromHttp(String response, Context context, ArrayList<ImageFuli> imagesList, int currentImagePosition) throws ExecutionException, InterruptedException {

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();//开启事务
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            for(int i=currentImagePosition;currentImagePosition<jsonArray.length() && i<currentImagePosition+10;i++){
                jsonObject=jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");

                ImageFuli imageFuli =new ImageFuli(url);
                Bitmap bitmap = Glide.with(context).load(imageFuli.getUrl()).asBitmap()
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
                imageFuli.setWidth(bitmap.getWidth());
                imageFuli.setHeight(bitmap.getHeight());

                realm.copyToRealm(imageFuli);

                imagesList.add(imageFuli);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        realm.commitTransaction();//提交事务
        realm.close();
    }

}
