package com.zxwl.network.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author：hw
 * data:2017/5/31 16:28
 */
public class SessionIdInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        //如果SessionID不为空则添加
        Request request = null;
        if (!chain.request().url().toString().contains("WEB_RequestSessionIDAPI")) {
                    request = chain.request()
                    .newBuilder()
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .build();
        }
        return chain.proceed(request);
    }
}
