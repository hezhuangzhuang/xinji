package com.zxwl.xinji.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.PackageUtil;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

import me.jessyan.autosize.internal.CancelAdapt;

public class AboutActivity extends BaseActivity implements View.OnClickListener, CancelAdapt {

    private RelativeLayout rlTopTitle;
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvVersion;
    private ImageView ivErweima;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        rlTopTitle = (RelativeLayout) findViewById(R.id.rl_top_title);
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        ivErweima = (ImageView) findViewById(R.id.iv_erweima);
    }

    @Override
    protected void initData() {
        tvTopTitle.setText("关于我们");

        tvVersion.setText(String.format(getResources().getString(R.string.version), PackageUtil.getVersionName(this)));

        StatusBarUtil.setTranslucentForImageView(this, 0, rlTopTitle);

        rlTopTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.tran));
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);

        ivErweima.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String url = "http://61.182.50.12:8080/xjdj/app/appHelper.html";
                ClipData clipData = ClipData.newPlainText("", url);
                ClipboardManager systemService = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                systemService.setPrimaryClip(clipData);

                showHintDialog(url);
                return true;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_operate:
                finish();
                break;

        }
    }

    /**
     * 显示对话框
     */
    private MaterialDialog hintDialog = null;

    /**
     * 显示对话框
     */
    private void showHintDialog(String url) {
        View inflate = View.inflate(this, R.layout.dialog_call, null);
        TextView tvNumber = inflate.findViewById(R.id.tv_title);
        TextView tvCall = inflate.findViewById(R.id.tv_ok);
        TextView tvCancle = inflate.findViewById(R.id.tv_cancle);

        tvNumber.setText("下载地址已复制,是否跳转到浏览器下载?");
        tvNumber.setTextColor(ContextCompat.getColor(this, R.color.color_E42417));
        tvCall.setText("确定");
        tvCancle.setText("取消");

        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != hintDialog) {
                    hintDialog.dismiss();
                }

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != hintDialog) {
                    hintDialog.dismiss();
                }
            }
        });

        hintDialog = new MaterialDialog.Builder(this)
                .customView(inflate, false)
                .build();
        //点击对话框以外的地方，对话框不消失
        hintDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //callDialog.setCancelable(false);
        hintDialog.show();
    }
}
