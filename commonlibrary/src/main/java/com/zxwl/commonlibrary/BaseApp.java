package com.zxwl.commonlibrary;

import android.app.Application;

/**
 * author：pc-20171125
 * data:2018/12/18 15:48
 */

public class BaseApp extends Application {

    public static BaseApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        init();
    }

    private void init() {
    }

    public static BaseApp getInstance() {
        return mInstance;
    }
}
