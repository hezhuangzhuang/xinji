package com.zxwl.xinji.bean;

import android.content.Context;

/**
 * author：pc-20171125
 * data:2019/5/17 13:03
 */
public class Data2Source
//        implements DataSourceListener
{
    public static final String TAG = "OfficeDataSource";

    private Context context;

    public Data2Source(Context context) {
        this.context = context.getApplicationContext();
    }

//    @Override
//    public com.google.android.exoplayer2.upstream.DataSource.Factory getDataSourceFactory() {
//        // OkHttpClient okHttpClient = new OkHttpClient();
//        // OkHttpDataSourceFactory OkHttpDataSourceFactory=    new OkHttpDataSourceFactory(okHttpClient, Util.getUserAgent(context, context.getApplicationContext().getPackageName()),new DefaultBandwidthMeter() );
//        //使用OkHttpClient 数据源工厂
//        //   return  OkHttpDataSourceFactory;
//        //默认数据源工厂
//        return new DefaultHttpDataSourceFactory(context.getPackageName(),null ,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
//                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,true);
//
//    }
}
