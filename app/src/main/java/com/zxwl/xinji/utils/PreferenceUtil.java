package com.zxwl.xinji.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zxwl.commonlibrary.utils.LocContext;
import com.zxwl.xinji.activity.LoginActivity;
import com.zxwl.network.bean.response.LoginBean;


/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * SP缓存工具类
 */
public final class PreferenceUtil {
    public static void reset(final Context context) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.clear();
        edit.apply();
    }

    public static String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(LocContext.getContext()).getString(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(LocContext.getContext()).getLong(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return PreferenceManager.getDefaultSharedPreferences(LocContext.getContext()).getFloat(key, defValue);
    }

    public static void put(String key, String value) {
        putString(key, value);
    }

    public static void put(String key, int value) {
        putInt(key, value);
    }

    public static void put(String key, float value) {
        putFloat(key, value);
    }

    public static void put(String key, boolean value) {
        putBoolean(key, value);
    }

    private static void putFloat(String key, float value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
    }

    public static int getInt(String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(LocContext.getContext()).getInt(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(LocContext.getContext()).getBoolean(key, defValue);
    }

    public static void putStringProcess(String key, String value) {
        SharedPreferences sharedPreferences = LocContext.getContext().getSharedPreferences("preference_mu", Context.MODE_MULTI_PROCESS);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringProcess(String key, String defValue) {
        SharedPreferences sharedPreferences = LocContext.getContext().getSharedPreferences("preference_mu", Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(key, defValue);
    }

    public static boolean hasString(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        return sharedPreferences.contains(key);
    }

    private static void putString(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putLong(String key, long value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static void putInt(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void remove(String... keys) {
        if (keys != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    LocContext.getContext());
            Editor editor = sharedPreferences.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.apply();
        }
    }

    private static Gson gson = new Gson();

    private static String USER_INFO = "USER_INFO";

    public static void putUserInfo(Context context, LoginBean.AccountBean userInfoBean) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocContext.getContext());
        Editor editor = sharedPreferences.edit();
        editor.putString(USER_INFO, gson.toJson(userInfoBean));
        editor.apply();
    }

    public static LoginBean.AccountBean getUserInfo(Context context) {
        String userString = PreferenceManager.getDefaultSharedPreferences(context).getString(USER_INFO, "");
        if (TextUtils.isEmpty(userString) || "null".equals(userString)) {
            LoginActivity.startActivity(context);
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity.getApplicationContext(), "请登录后查看", Toast.LENGTH_SHORT).show();
                    }
                });
                activity.finish();
            }
            return null;
        } else {
            return gson.fromJson(userString, LoginBean.AccountBean.class);
        }
    }
}
