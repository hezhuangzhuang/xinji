package com.zxwl.network.api;

import com.zxwl.network.bean.BaseData;

import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * author：Administrator
 * data:2019/12/24 11:32
 */
public interface ConfApi {
    /**
     * 加入会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("conf/addSiteByAccessCode")
    Observable<BaseData> joinConfNew(
            @Query("accessCode") String accessCode,
            @Query("sites") String sites);
}
