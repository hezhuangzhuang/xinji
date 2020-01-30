package com.zxwl.xinji.widget;

import android.app.Dialog;
import android.content.Context;

/**
 * author：pc-20171125
 * data:2019/6/17 10:07
 */
public class CustomVersionDialog extends Dialog {
    private int res;

    public CustomVersionDialog(Context context, int theme, int res) {
        super(context, theme);
        // TODO 自动生成的构造函数存根
        setContentView(res);
        this.res = res;
        setCanceledOnTouchOutside(false);
    }

}
