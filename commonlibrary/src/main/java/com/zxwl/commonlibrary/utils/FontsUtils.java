package com.zxwl.commonlibrary.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * author：pc-20171125
 * data:2019/6/10 09:37
 */
public class FontsUtils {
    /**
     * 设置自定义字体
     *
     * @param context
     * @param staticTypefaceFieldName 需要替换的系统字体样式
     * @param fontAssetName           替换后的字体样式
     */
    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        // 根据路径得到Typeface
        Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        // 设置全局字体样式
        replaceFont(staticTypefaceFieldName, regular);
    }

    private static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            //替换系统字体样式
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置单个控件的字体
     */
    public static void setSingleFont(Context context, TextView textView, boolean isBlod) {
        //从asset 读取字体
        //得到AssetManager
        AssetManager mgr = context.getAssets();
        Typeface tf = null;
        if (isBlod) {
            //根据路径得到Typeface
            tf = Typeface.createFromAsset(mgr, "fonts/sourcehanserifcn_bold.ttf");
        } else {
            //根据路径得到Typeface
            tf = Typeface.createFromAsset(mgr, "fonts/sourcehanserifcn_regular.ttf");
        }
        //设置字体
        textView.setTypeface(tf);
    }

    /**
     * 设置单个控件的字体
     */
    public static void setSingleFontSerif(TextView... textViews) {
        for (TextView tvContent : textViews) {
            tvContent.setTypeface(Typeface.SANS_SERIF);
        }
    }
    /**
     * 设置单个控件的字体
     */
    public static void setSingleFontSerifBlod(TextView... textViews) {
        for (TextView tvContent : textViews) {
            tvContent.setTypeface(Typeface.SANS_SERIF);
            tvContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }


}
