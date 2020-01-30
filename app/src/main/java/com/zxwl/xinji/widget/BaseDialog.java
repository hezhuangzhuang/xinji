package com.zxwl.xinji.widget;

import android.app.Dialog;
import android.content.Context;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * author：pc-20171125
 * data:2019/11/29 16:34
 */
public class BaseDialog extends Dialog implements CancelAdapt {
    private int res;

    public BaseDialog(Context context, int theme, int res) {
        super(context, theme);
        // TODO 自动生成的构造函数存根
        setContentView(res);
        this.res = res;
        setCanceledOnTouchOutside(false);
    }

}
