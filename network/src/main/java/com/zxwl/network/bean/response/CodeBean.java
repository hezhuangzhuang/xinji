package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/6/3 10:26
 */
public class CodeBean {

    /**
     * message : ok
     * result : success
     * data : {"bizId":"964702959528522809^0","code":"OK","message":"OK","requestId":"DD30971E-C7C9-4F14-8F31-BBD8FD96F08F"}
     */
    public String message;
    public String result;
    public DataBean data;


    public static class DataBean {
        /**
         * bizId : 964702959528522809^0
         * code : OK
         * message : OK
         * requestId : DD30971E-C7C9-4F14-8F31-BBD8FD96F08F
         */

        public String bizId;
        public String code;
        public String message;
        public String requestId;

    }
}
