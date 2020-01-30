package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/6/12 10:35
 */
public class VersionBean {
    /**
     * message : 查询成功！
     * result : success
     * data : {"apkUrl":"http://localhost:8080/xuexi/apkFolder/xxqx_222222.apk",
     * "context":"222",
     * "id":"20",
     * "uploadTime":"2019-06-12 10:21:29",
     * "versionNumber":"222222"
     * }
     *
     * {"message":"查询失败！","result":"error","data":""}
     */
    public String message;
    public String result;
    public DataBean data;

    public static class DataBean {
        /**
         * apkUrl : http://localhost:8080/xuexi/apkFolder/xxqx_222222.apk
         * context : 222
         * id : 20
         * uploadTime : 2019-06-12 10:21:29
         * versionNumber : 222222
         */
        public String apkUrl;
        public String context;
        public String id;
        public String uploadTime;
        public long versionNumber;
    }

}
