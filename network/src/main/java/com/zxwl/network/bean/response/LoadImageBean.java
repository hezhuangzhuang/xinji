package com.zxwl.network.bean.response;

/**
 * author：pc-20171125
 * data:2019/8/19 18:12
 */
public class LoadImageBean {

    /**
     * result : success
     * message : 上传成功!
     * dataList : [http://192.168.20.140:8080/xjdj/picFolder/hamletaff/20190819181203237_91.jpeg, http://192.168.20.140:8080/xjdj/picFolder/hamletaff/20190819181203237_91.jpeg]
     */

    private String result;
    private String message;
    private String dataList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataList() {
        return dataList;
    }

    public void setDataList(String dataList) {
        this.dataList = dataList;
    }
}

