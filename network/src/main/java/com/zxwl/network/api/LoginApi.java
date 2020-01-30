package com.zxwl.network.api;

import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.CheckBean;
import com.zxwl.network.bean.response.LoginBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * author：pc-20171125
 * data:2018/12/18 17:52
 */

public interface LoginApi {
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
     * 修改密码
     * ?telephone=13548893190&password=zhangsan&name=张三
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("accountAction_updPwdByTelephone.action")
    Observable<BaseData> changePwd(
            @Query("telephone") String telephone,
            @Query("password") String password
    );

    /**
     * 心跳接口
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("operatorAction_checkAccount.action")
    Observable<CheckBean> checkAccount(
            @Query("account") String account,
            @Query("deviceID") String deviceID
    );

    /**
     * 登录
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("operatorAction_loginApp.action")
    Observable<LoginBean> login(
            @Query("account") String account,
            @Query("password") String password,
            @Query("deviceID") String deviceID
    );

    /**
     * 登出
     * 正常退出type为0
     * accountAction_login.action
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("operatorAction_logoutApp.action")
    Observable<BaseData> logout(@Query("type") int type);

    /**
     * 强制登出
     * accountAction_login.action
     * 强制退出type为1
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("operatorAction_logoutApp.action")
    Observable<BaseData> doubleLogout(@Query("type") int type);

    /**
     * @param phone
     * @param code
     * @param type  1:注册，2找回密码，3：修改手机号
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("operatorAction_updPwd.action")
    Observable<BaseData> updatePwd(@Query("password") String password);

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


}
