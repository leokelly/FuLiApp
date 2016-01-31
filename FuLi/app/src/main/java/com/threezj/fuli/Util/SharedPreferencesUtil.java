package com.threezj.fuli.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Zj on 2016/1/24.
 */
public class SharedPreferencesUtil {

    private static final String CURRENT_IMAGE_KEY = "current_image_position";
    private static final String CURRENT_PAGE_KEY = "current_image_position";

    private static SharedPreferences getSharePreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveCurrentImagePositionAndPage(Context context, int currentImagePosition,int page, int type){
        Log.d("test", "save" + type);

        SharedPreferences.Editor editor = getSharePreferences(context).edit();
        editor.putInt(CURRENT_IMAGE_KEY+type, currentImagePosition);
        editor.putInt(CURRENT_PAGE_KEY+type,page);
        editor.commit();
    }

    public static int getCurrentImagePosition(Context context,int type){
        Log.d("test","get"+type);
        SharedPreferences prefs= getSharePreferences(context);
        int currentImagePosition = prefs.getInt(CURRENT_IMAGE_KEY+type,0);
        return  currentImagePosition;
    }

    public static int getCurrentPage(Context context,int type){
        Log.d("test","get"+type);
        SharedPreferences prefs= getSharePreferences(context);
        int page = prefs.getInt(CURRENT_PAGE_KEY+type,1);
        return  page;
    }
}
