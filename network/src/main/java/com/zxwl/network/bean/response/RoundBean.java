package com.zxwl.network.bean.response;

import com.sunfusheng.marqueeview.IMarqueeImageItem;

/**
 * author：pc-20171125
 * data:2019/11/22 14:43
 */
public class RoundBean implements IMarqueeImageItem {
    /**
     * id : 910
     * title : 我是督办
     * type : 2
     */
    public static final int TYPE_TZGG=1;
    public static final int TYPE_SJDB=2;
    public static final int TYPE_ZXPB=3;

    public int id;
    public String title;
    public int type;
    public int imageRes;

    @Override
    public CharSequence marqueeMessage() {
        return title;
    }

    @Override
    public int getImageRes() {
        return imageRes;
    }
}
