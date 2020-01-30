package com.zxwl.commonlibrary.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * author：pc-20171125
 * data:2019/4/12 15:17
 */
public class DialogUtils {

    private static MaterialDialog dialog;

    public static void showProgressDialog(Context context, String content) {
        dialog = new MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();
        //点击对话框以外的地方，对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //dialog.setCancelable(false);
        dialog.show();
    }

    public static void dismissProgressDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }


}
