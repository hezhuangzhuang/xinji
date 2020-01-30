package com.zxwl.xinji.bean;

import com.zxwl.network.bean.response.CurrencyBean;

/**
 * author：pc-20171125
 * data:2019/11/26 15:29
 */
public class StudyHeadBean implements CurrencyBean {
    public String title;
    public int resId;

    public StudyHeadBean(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }
}
