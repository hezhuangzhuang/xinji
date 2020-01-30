package com.zxwl.network.api;

import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.CodeBean;
import com.zxwl.network.bean.response.LoginBean;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * author：pc-20171125
 * data:2018/12/18 17:52
 */

public interface UserInfoApi {

    /**
     * 修改个人信息
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("accountAction_updAccount.action")
    Observable<LoginBean> updAccount(
            @QueryMap Map<String, String> map
    );

    /**
     * 上传头像
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json", "Connection:close"})
    @POST("operatorAction_loadAvatar.action")
    Observable<BaseData> loadAvatar(
            @Query("accountId") String accountId,
            @Body RequestBody body
    );

    /**
     * 修改头像
     */
    @Headers({"Content-Type: application/json", "Accept: application/json", "Connection:close"})
    @POST("operatorAction_updAccount.action")
    Observable<BaseData> updateAvatar(
            @Query("id") String accountId,
            @Query("url") String url
    );

    /**
     * 获取sessionID方法
     */
    @POST
    Observable<BaseData> getSessionId(@Url String url);

    /**
     * 登录
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> login(@Url String url,
                               @Body RequestBody body);

    /**
     * 注册
     * ?telephone=13548893190&password=zhangsan&name=张三
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("accountAction_addAccount.action")
    Observable<BaseData> register(
            @Query("telephone") String telephone,
            @Query("password") String password,
            @Query("name") String name
    );


    /**
     * 登录
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("accountAction_login.action")
    Observable<LoginBean> login(
            @Query("telephone") String telephone,
            @Query("password") String password,
            @Query("deviceID") String deviceID
    );

    /**
     * 登出
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("accountAction_logout.action")
    Observable<BaseData> logout();

    /**
     * 发送验证码
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("accountAction_sendShortMsg.action")
    Observable<CodeBean> sendCode(@Query("phone") String phone, @Query("code") String code);

    /*
     * 上传访客记录
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> checkIn(@Url String url,
                                 @Field("name") String name,
                                 @Field("tel") String tel,
                                 @Field("studentName") String studentName,
                                 @Field("plate") String plate,
                                 @Field("remark") String remark,
                                 @Field("src") String base64);

    /*
     * 上传访客记录
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Observable<BaseData> checkIn(@Url String url,
                                 @Body RequestBody body);

    /**
     * 更改sessionID方法
     */
    @POST
    Observable<BaseData> changeSessionId(@Url String url);

}
