package com.threezj.fuli.Util;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.threezj.fuli.model.ImageFuli;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;

/**
 * Created by Zj on 2015/12/28.
 */
public class ResponseHandleUtil {
    public static ArrayList<ImageFuli> HandleResponseFromHttp(String response,Context context) throws ExecutionException, InterruptedException {
        ArrayList<ImageFuli> imageFulis =new ArrayList<ImageFuli>();
        String regEx = "\"url\":\".*?\"";
        Pattern pat = Pattern.compile(regEx);
        Matcher mat = pat.matcher(response);
        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();//开启事务
        while (mat.find()) {
            String url = mat.group().substring(7,mat.group().length()-1);

            ImageFuli imageFuli = new ImageFuli(url);

            Bitmap bitmap = Glide.with(context).load(url).asBitmap()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            imageFuli.setWidth(bitmap.getWidth());
            imageFuli.setHeight(bitmap.getHeight());
            //载入数据库
            realm.copyToRealm(imageFuli);

            imageFulis.add(imageFuli);

        }
        realm.commitTransaction();//提交事务
        return imageFulis;
    }
}
