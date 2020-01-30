package com.zxwl.xinji.adapter;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;

/**
 * author：pc-20171125
 * data:2019/11/27 10:14
 */
public class TopSmoothScroller extends LinearSmoothScroller {
    public TopSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected int getVerticalSnapPreference() {
        return LinearSmoothScroller.SNAP_TO_START;
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        // 移动一英寸需要花费3ms
        return 3f / displayMetrics.density;
    }
}
