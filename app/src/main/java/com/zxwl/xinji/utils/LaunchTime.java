package com.zxwl.xinji.utils;

import com.zxwl.network.interceptor.CommonLogger;

/**
 * author：pc-20171125
 * data:2019/7/2 14:39
 */
public class LaunchTime {
    private static long sTime;

    public static void startTime(String msg) {
        sTime = System.currentTimeMillis();
        CommonLogger.i("LaunchTime", "开始计算时间:" + sTime);
    }

    public static long endTime(String msg) {
        long endTime = System.currentTimeMillis() - sTime;
        CommonLogger.i("LaunchTime", "启动花费时间:" + endTime);
        return endTime;
    }
}
