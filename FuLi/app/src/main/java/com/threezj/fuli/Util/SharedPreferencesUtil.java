package com.threezj.fuli.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Zj on 2016/1/24.
 */
public class SharedPreferencesUtil {

    private static String CURRENT_IMAGE_KEY = "current_image_position";

    private static SharedPreferences getSharePreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveCurrentImagePosition(Context context,int currentImagePosition){
        SharedPreferences.Editor editor = getSharePreferences(context).edit();
        editor.putInt(CURRENT_IMAGE_KEY, currentImagePosition);
        editor.commit();
    }

    public static int getCurrentImagePosition(Context context){

        SharedPreferences prefs= getSharePreferences(context);

        int currentImagePosition = prefs.getInt(CURRENT_IMAGE_KEY,0);
        return  currentImagePosition;
    }
}
